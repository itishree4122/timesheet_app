package com.example.myapplication

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.backend_api.RetrofitClient
import com.example.myapplication.backend_api.SharedPreferencesHelper
import com.example.myapplication.interface_api.JobSetReportRequest
import com.example.myapplication.interface_api.JobSetReportResponse
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.ceil

class JobSetReportActivity : AppCompatActivity() {

    private lateinit var fromDateLayoout : TextInputLayout
    private lateinit var fromDateInput : TextInputEditText
    private lateinit var  toDateLayout: TextInputLayout
    private lateinit var toDateInput : TextInputEditText
    private lateinit var supervisorLayout : TextInputLayout
    private lateinit var supervisorInput : TextInputEditText
    private lateinit var toolbar_text: TextView
    private lateinit var cardView1: LinearLayout
    private lateinit var tableLayout: LinearLayout
    private lateinit var fixedColumnTable: TableLayout
    private lateinit var scrollableColumnTable: TableLayout
    private lateinit var submit: Button
    private lateinit var submitProgressBar: ProgressBar
    private lateinit var back: Button

    @SuppressLint("MissingInflatedId", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        setContentView(R.layout.activity_job_set_report)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.button)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar_text = findViewById(R.id.toolbar_text)
        fromDateLayoout = findViewById(R.id.date1)
        fromDateInput = findViewById(R.id.dateEditText1)
        toDateLayout = findViewById(R.id.date2)
        toDateInput = findViewById(R.id.dateEditText2)
        supervisorLayout = findViewById(R.id.supervisor)
        supervisorInput = findViewById(R.id.supervisorEditText)
        submit  = findViewById(R.id.submit)
        submitProgressBar = findViewById(R.id.submitProgressBar)
        fixedColumnTable = findViewById(R.id.fixedColumnTable)
        scrollableColumnTable = findViewById(R.id.scrollableColumnTable)
        cardView1 = findViewById(R.id.cardView1)
        tableLayout = findViewById(R.id.tableLayout)
        back = findViewById(R.id.backButton)

        // Disable keyboard for the EditText
        fromDateInput.isFocusable = false
        fromDateInput.isFocusableInTouchMode = false

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Correct format

        fromDateInput.setOnClickListener {
            val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                // Adjust the month for zero-based index
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Set the formatted date
                val selectedDate = dateFormat.format(calendar.time)
                Log.d("SelectedDate", "Date selected: $selectedDate") // Log the selected date
                fromDateInput.setText(selectedDate)
            }

            val dateDialog = DatePickerDialog(
                this,
                dateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            dateDialog.show()
        }

        // Disable keyboard for the EditText
        toDateInput.isFocusable = false
        toDateInput.isFocusableInTouchMode = false

        val calendar1 = Calendar.getInstance()
        val dateFormat1 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Correct format

        toDateInput.setOnClickListener {
            val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                // Adjust the month for zero-based index
                calendar1.set(Calendar.YEAR, year)
                calendar1.set(Calendar.MONTH, month)
                calendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Set the formatted date
                val selectedDate = dateFormat1.format(calendar1.time)
                Log.d("SelectedDate", "Date selected: $selectedDate") // Log the selected date
                toDateInput.setText(selectedDate)
            }

            val dateDialog = DatePickerDialog(
                this,
                dateListener,
                calendar1.get(Calendar.YEAR),
                calendar1.get(Calendar.MONTH),
                calendar1.get(Calendar.DAY_OF_MONTH)
            )

            dateDialog.show()
        }

        // Set click listener on the Submit button
        submit.setOnClickListener {

            fetchAttendanceData()
        }
        // Retrieve the supervisor name passed via Intent
        val supervisorName = intent.getStringExtra("SUPERVISOR_NAME")

        // Retrieve the admin status from SharedPreferences or Intent
        val isAdmin = SharedPreferencesHelper.getIsAdmin(this)  // or get the value directly from Intent

        // Check if the user is an admin or not
        if (isAdmin) {
            // If is_admin is true, clear the field and make it editable
            supervisorInput.text = Editable.Factory.getInstance().newEditable("") // Ensure the field is cleared
            supervisorInput.isFocusableInTouchMode = true
            supervisorInput.isEnabled = true
        } else {
            // If is_admin is false, set the supervisor name and make it non-editable
            supervisorName?.let {
                supervisorInput.text = Editable.Factory.getInstance().newEditable(it)
            }
            supervisorInput.isFocusableInTouchMode = false
            supervisorInput.isEnabled = false
        }
        val nameToShow = if (isAdmin) {
            SharedPreferencesHelper.getUsername(this) ?: "Admin" // Default to "Admin" if name is null
        } else {
            SharedPreferencesHelper.getSupervisorName(this) ?: "Supervisor" // Default to "Supervisor" if name is null
        }

        // Set the name to the TextView
        toolbar_text.text = "$nameToShow"

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Navigate back to the previous page
        }
        back.visibility = View.GONE

        back.setOnClickListener {
            showForm()
            hideTable()
        }
    }

    private fun fetchAttendanceData() {
        val attendanceApi = RetrofitClient.getJobSetReportApi()
        val accessToken = "Bearer " + SharedPreferencesHelper.getAccessToken(this)

        val supervisorName = supervisorInput.text.toString()
        val fromDate = fromDateInput.text.toString()
        val toDate = toDateInput.text.toString()

        if (fromDate.isEmpty() || toDate.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
            return
        }

        val request = JobSetReportRequest(
            supervisor_name = supervisorName,
            from_date = fromDate,
            to_date = toDate
        )

        submitProgressBar.visibility = View.VISIBLE

        attendanceApi.getJobSetReportData(accessToken, request).enqueue(object :
            Callback<JobSetReportResponse> {
            override fun onResponse(
                call: Call<JobSetReportResponse>,
                response: Response<JobSetReportResponse>
            ) {

                submitProgressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val attendanceData = response.body()
                    attendanceData?.let {
                        // Filter duplicate entries
                        val filteredJobSetSummary = it.job_set_summary.distinctBy { summary ->
                            Pair(summary.date, summary.supervisor_name)
                        }

                        val filteredAttendanceRecords = it.attendance_records.distinctBy { record ->
                            record.date // Filter by date for attendance records
                        }

                        // Create a new JobSetReportResponse with filtered data
                        val filteredResponse = JobSetReportResponse(
                            job_set_summary = filteredJobSetSummary,
                            attendance_records = filteredAttendanceRecords
                        )

                        Toast.makeText(
                            this@JobSetReportActivity,
                            "Data retrieved successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        resetFormFields()
                        populateTable(filteredResponse)
                    }
                } else {
                    Toast.makeText(
                        this@JobSetReportActivity,
                        "Failed to retrieve data: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<JobSetReportResponse>, t: Throwable) {
                submitProgressBar.visibility = View.GONE
                Toast.makeText(this@JobSetReportActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun populateTable(data: JobSetReportResponse) {
        // Clear any existing rows in the tables
        fixedColumnTable.removeAllViews()
        scrollableColumnTable.removeAllViews()

        // Add headers
        addHeaders()

        // Create a map of attendance records by date for easy lookup
        val attendanceMap = data.attendance_records.associateBy { it.date }

        // Sort job set summary and attendance records by date in ascending order
        val sortedJobSetSummary = data.job_set_summary.sortedBy { it.date }
        val sortedAttendanceRecords = data.attendance_records.sortedBy { it.date }

        // Track unique entries by date and supervisor
        val uniqueEntries = mutableSetOf<Pair<String, String>>() // Pair of date and supervisor name

        // Add data rows by merging attendance and job set summary
        sortedJobSetSummary.forEach { summary ->
            // Get the attendance record for the same date, if it exists
            val attendanceRecord = attendanceMap[summary.date]

            // If attendance record doesn't exist, use default values (0 for total SK, total SSK, total USK)
            val totalSk = attendanceRecord?.total_sk?.toString() ?: "0"
            val totalSsk = attendanceRecord?.total_ssk?.toString() ?: "0"
            val totalUsk = attendanceRecord?.total_usk?.toString() ?: "0"

            // If supervisor name is missing, fill with "NA"
            val supervisorName = if (summary.supervisor_name.isNullOrBlank()) "NA" else summary.supervisor_name

            // Check if this date-supervisor pair is already added
            if (!uniqueEntries.add(Pair(summary.date, supervisorName))) {
                // Skip this row as it is a duplicate
                return@forEach
            }

            // Highlight row if skilled, semi skilled, or unskilled is less than the total values
            val highlightRow = summary.skilled < (attendanceRecord?.total_sk ?: 0) ||
                    summary.semi_skilled < (attendanceRecord?.total_ssk ?: 0) ||
                    summary.unskilled < (attendanceRecord?.total_usk ?: 0)

            // Round up skilled, semi_skilled, and unskilled values
            val skilledRounded = ceil(summary.skilled).toInt()
            val semiSkilledRounded = ceil(summary.semi_skilled).toInt()
            val unskilledRounded = ceil(summary.unskilled).toInt()

            // Add the merged row (date, supervisor, skilled, semi-skilled, unskilled, total SK, total SSK, total USK)
            addRow(
                fixedColumn = summary.date,
                scrollableColumns = listOf(
                    supervisorName,
                    skilledRounded.toString(),
                    semiSkilledRounded.toString(),
                    unskilledRounded.toString(),
                    totalSk,
                    totalSsk,
                    totalUsk
                ),
                highlightRow = highlightRow
            )
        }

        // If there are any attendance records that don't have corresponding job set summary, add them too
        sortedAttendanceRecords.forEach { record ->
            if (data.job_set_summary.none { it.date == record.date }) {
                // Check if this date-supervisor pair is already added
                if (!uniqueEntries.add(Pair(record.date, "NA"))) {
                    // Skip this row as it is a duplicate
                    return@forEach
                }

                addRow(
                    fixedColumn = record.date,
                    scrollableColumns = listOf(
                        "NA", // Supervisor name is missing for these rows
                        "0",  // Skilled
                        "0",  // Semi Skilled
                        "0",  // Unskilled
                        record.total_sk.toString(),
                        record.total_ssk.toString(),
                        record.total_usk.toString()
                    ),
                    highlightRow = false // No need to highlight since it's an attendance-only record
                )
            }
        }

        // Hide the form and show the table
        hideForm()
        showTable()
    }

    private fun addHeaders() {
        // Fixed column header (e.g., "Date")
        val fixedHeaderRow = TableRow(this)
        fixedHeaderRow.addView(createHeaderTextView("Date"))
        fixedColumnTable.addView(fixedHeaderRow)

        // Scrollable column headers
        val scrollableHeaderRow = TableRow(this)
        val headers = listOf("Supervisor", "Skilled(Job Set)", "Semi-skilled(Job Set)", "Unskilled(Job Set)", "Skilled(Time Sheet)", "Semi-skilled(Time Sheet)", "Unskilled(Time Sheet)")
        headers.forEach { header ->
            scrollableHeaderRow.addView(createHeaderTextView(header))
        }
        scrollableColumnTable.addView(scrollableHeaderRow)
    }

    private fun addRow(fixedColumn: String, scrollableColumns: List<String>, highlightRow: Boolean) {
        // Add data to the fixed column table
        val fixedRow = TableRow(this)
        fixedRow.addView(createTextView(fixedColumn))
        fixedColumnTable.addView(fixedRow)

        // Add data to the scrollable column table
        val scrollableRow = TableRow(this)

        // Add the cells with conditional highlighting
        scrollableColumns.forEachIndexed { index, columnData ->
            val textView = createTextView(columnData)
            if (highlightRow && index in 1..3) {  // Highlight "Skilled", "Semi-skilled", "Unskilled"
                textView.setBackgroundColor(resources.getColor(R.color.highlight)) // Set the color for highlighting
            }
            scrollableRow.addView(textView)
        }

        scrollableColumnTable.addView(scrollableRow)
    }

    private fun createHeaderTextView(content: String): TextView {
        return TextView(this).apply {
            text = content
            setPadding(16, 32, 16, 32) // Increased padding to increase the height
            textSize = 16f // Slightly larger text for headers
            setBackgroundResource(R.drawable.table_row) // Set background from drawable resource
            setTextColor(resources.getColor(R.color.white)) // Set text color for header
            gravity = Gravity.CENTER // Center align text
            setTypeface(null, Typeface.BOLD) // Bold text for headers
        }
    }


    private fun createTextView(content: String): TextView {
        return TextView(this).apply {
            text = content
            setPadding(16, 32, 16, 32) // Increased padding to increase the height
            textSize = 14f
            setBackgroundResource(R.drawable.table_column) // Optional styling for cells
            gravity = Gravity.CENTER // Center align text
        }
    }


    //Creating Download options
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.download_option, menu)

        // Find your menu item
        val downloadItem1 = menu?.findItem(R.id.download1)
        val downloadItem2 = menu?.findItem(R.id.download2)


        // Create a SpannableString to customize the title
        val styledTitle1 = SpannableString("Download as PDF")
        styledTitle1.setSpan(ForegroundColorSpan(Color.BLACK), 0, styledTitle1.length, 0)
        styledTitle1.setSpan(RelativeSizeSpan(1.1f), 0, styledTitle1.length, 0)

        val styledTitle2 = SpannableString("Download in XCEL")
        styledTitle2.setSpan(ForegroundColorSpan(Color.BLACK), 0, styledTitle2.length, 0)
        styledTitle2.setSpan(RelativeSizeSpan(1.1f), 0, styledTitle2.length, 0)

        downloadItem1?.title = styledTitle1
        downloadItem2?.title = styledTitle2

        return true
    }

    // Handle menu item selection
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.download1 -> {
                // Download the table as PDF
                downloadTableAsPdf()
                true
            }
            R.id.download2 -> {
                // Download the table as Excel
                downloadTableAsExcel()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun downloadTableAsPdf() {
        // Create a PDF file in the Documents directory
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Job_set_report_${System.currentTimeMillis()}.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
        }

        val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        uri?.let {
            try {
                // Create a PdfWriter with the output stream
                val pdfWriter = PdfWriter(contentResolver.openOutputStream(it)!!)
                val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(pdfWriter)
                pdfDocument.defaultPageSize = PageSize.A4.rotate() // Set to landscape orientation
                val document = Document(pdfDocument)

                // Dynamically calculate the total number of columns
                val totalColumns = (fixedColumnTable.getChildAt(0) as TableRow).childCount +
                        (scrollableColumnTable.getChildAt(0) as TableRow).childCount

                // Calculate column widths (proportional widths)
                val columnWidths = FloatArray(totalColumns) { 1f } // Equal width for all columns

                // Configure the table to fit within the page
                val table = Table(columnWidths)
                    .setWidth(UnitValue.createPercentValue(100f)) // Table width to 100% of the page
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)

                // Font sizes for headers and cells
                val headerFontSize = 10f
                val cellFontSize = 9f

                // Add table headers
                val fixedHeaderRow = fixedColumnTable.getChildAt(0) as TableRow
                val scrollableHeaderRow = scrollableColumnTable.getChildAt(0) as TableRow

                for (j in 0 until fixedHeaderRow.childCount) {
                    val view = fixedHeaderRow.getChildAt(j)
                    if (view is TextView) {
                        val headerText = view.text.toString()
                        table.addHeaderCell(
                            Paragraph(headerText)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setFontSize(headerFontSize)
                        )
                    }
                }

                for (j in 0 until scrollableHeaderRow.childCount) {
                    val view = scrollableHeaderRow.getChildAt(j)
                    if (view is TextView) {
                        val headerText = view.text.toString()
                        table.addHeaderCell(
                            Paragraph(headerText)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setFontSize(headerFontSize)
                        )
                    }
                }

                // Add table data rows dynamically
                for (i in 1 until fixedColumnTable.childCount) { // Skip header row
                    val fixedRow = fixedColumnTable.getChildAt(i) as TableRow
                    val scrollableRow = scrollableColumnTable.getChildAt(i) as TableRow

                    // Add data from fixed columns
                    for (j in 0 until fixedRow.childCount) {
                        val view = fixedRow.getChildAt(j)
                        if (view is TextView) {
                            val cellText = view.text.toString()
                            table.addCell(
                                Paragraph(cellText)
                                    .setTextAlignment(TextAlignment.CENTER)
                                    .setFontSize(cellFontSize)
                            )
                        }
                    }

                    // Add data from scrollable columns
                    for (j in 0 until scrollableRow.childCount) {
                        val view = scrollableRow.getChildAt(j)
                        if (view is TextView) {
                            val cellText = view.text.toString()
                            table.addCell(
                                Paragraph(cellText)
                                    .setTextAlignment(TextAlignment.CENTER)
                                    .setFontSize(cellFontSize)
                            )
                        }
                    }
                }

                // Add table to the document
                document.add(table)
                document.close()

                Toast.makeText(this, "PDF downloaded successfully", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Error creating PDF file: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Error creating PDF file", Toast.LENGTH_SHORT).show()
        }
    }



    // Download Table as Excel
    private fun downloadTableAsExcel() {
        val workbook: Workbook = XSSFWorkbook()
        val sheet: XSSFSheet = workbook.createSheet("Job_set Report") as XSSFSheet

        // Populate rows with data from both tables
        for (i in 0 until fixedColumnTable.childCount) {
            val fixedRow = fixedColumnTable.getChildAt(i) as TableRow
            val scrollableRow = scrollableColumnTable.getChildAt(i) as TableRow
            val dataRow = sheet.createRow(i + 1) // Start from the second row

            // Add data from fixedRow
            for (j in 0 until fixedRow.childCount) {
                val view = fixedRow.getChildAt(j)
                if (view is TextView) {
                    val cellText = view.text.toString()
                    dataRow.createCell(j).setCellValue(cellText) // Add data from fixedRow to Excel
                }
            }

            // Add data from scrollableRow (excluding action buttons)
            for (j in 0 until scrollableRow.childCount) { // Exclude last column for Action buttons
                val view = scrollableRow.getChildAt(j)
                if (view is TextView) {
                    val cellText = view.text.toString()
                    dataRow.createCell(j + fixedRow.childCount).setCellValue(cellText) // Adjust index for Excel
                }
            }
        }

        // Create a unique filename with a timestamp
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "EmployeeData_${System.currentTimeMillis()}.xlsx")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
        }

        val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        uri?.let {
            try {
                val outputStream = contentResolver.openOutputStream(it)
                workbook.write(outputStream)
                outputStream?.close()
                workbook.close()
                Toast.makeText(this, "Excel file downloaded successfully", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Error downloading Excel file: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Error creating Excel file", Toast.LENGTH_SHORT).show()
        }

    }

    private fun showTable() {
        tableLayout.visibility = View.VISIBLE
        back.visibility = View.VISIBLE
    }

    private fun hideForm() {
        cardView1.visibility = View.GONE
        back.visibility = View.VISIBLE
    }

    private fun hideTable() {
        tableLayout.visibility = View.GONE
        back.visibility = View.GONE
    }

    private fun showForm() {
        cardView1.visibility = View.VISIBLE
        back.visibility = View.GONE
    }

    private fun resetFormFields() {
        // Reset all EditText fields
        toDateInput.text?.clear()
        fromDateInput.text?.clear()
        if (SharedPreferencesHelper.getIsAdmin(this)) {
            supervisorInput.text?.clear()
        }

    }
}