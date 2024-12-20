package com.example.myapplication

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Typeface
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
import androidx.core.view.marginEnd
import com.example.myapplication.backend_api.RetrofitClient
import com.example.myapplication.backend_api.SharedPreferencesHelper
import com.example.myapplication.interface_api.AttendanceRecord
import com.example.myapplication.interface_api.AttendanceReportRequest
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
import org.apache.poi.hssf.record.Margin
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

class ReportActivity : AppCompatActivity() {

    private lateinit var dateLayout1: TextInputLayout
    private lateinit var dateInput1: TextInputEditText


    private lateinit var dateLayout2: TextInputLayout
    private lateinit var dateInput2: TextInputEditText

    private lateinit var supervisorLayout: TextInputLayout
    private lateinit var supervisorInput: TextInputEditText


    private lateinit var back: Button
    private lateinit var report: Button
    private lateinit var reportProgressBar: ProgressBar

    private lateinit var toolbar_text: TextView

    private lateinit var cardView1: LinearLayout
    private lateinit var tableLayout: LinearLayout

    private lateinit var fixedColumnTable: TableLayout
    private lateinit var scrollableColumnTable: TableLayout

    private var isMenuEnabled = false

    @SuppressLint("MissingInflatedId", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        setContentView(R.layout.activity_report)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.button)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        dateLayout1 = findViewById(R.id.date1)
        dateInput1 = findViewById(R.id.dateEditText1)
        dateLayout2 = findViewById(R.id.date2)
        dateInput2 = findViewById(R.id.dateEditText2)
        supervisorLayout = findViewById(R.id.supervisor)
        supervisorInput = findViewById(R.id.supervisorEditText)
        toolbar_text = findViewById(R.id.toolbar_text)
        fixedColumnTable = findViewById(R.id.fixedColumnTable)
        scrollableColumnTable = findViewById(R.id.scrollableColumnTable)
        cardView1 = findViewById(R.id.cardView1)
        tableLayout = findViewById(R.id.tableLayout)
        back = findViewById(R.id.backButton)
        report = findViewById(R.id.report)
        reportProgressBar = findViewById(R.id.reportProgressBar)


        // Disable keyboard for the EditText
        dateInput1.isFocusable = false
        dateInput1.isFocusableInTouchMode = false

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Correct format

        dateInput1.setOnClickListener {
            val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                // Adjust the month for zero-based index
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Set the formatted date
                val selectedDate = dateFormat.format(calendar.time)
                Log.d("SelectedDate", "Date selected: $selectedDate") // Log the selected date
                dateInput1.setText(selectedDate)
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
        dateInput2.isFocusable = false
        dateInput2.isFocusableInTouchMode = false

        val calendar1 = Calendar.getInstance()
        val dateFormat1 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Correct format

        dateInput2.setOnClickListener {
            val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                // Adjust the month for zero-based index
                calendar1.set(Calendar.YEAR, year)
                calendar1.set(Calendar.MONTH, month)
                calendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Set the formatted date
                val selectedDate = dateFormat1.format(calendar1.time)
                Log.d("SelectedDate", "Date selected: $selectedDate") // Log the selected date
                dateInput2.setText(selectedDate)
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

        report.setOnClickListener {
            isMenuEnabled = true  // Enable the menu when the OK button is clicked
            invalidateOptionsMenu()  // Refresh the options menu
            // Get the token from SharedPreferences
            val token = SharedPreferencesHelper.getAccessToken(this)

            // Get dynamic values for from_date, to_date, and supervisor_name from user inputs
            val fromDate = dateInput1.text.toString()
            val toDate = dateInput2.text.toString()
            val supervisor = supervisorInput.text.toString()

            // Check if the inputs are valid
            if (fromDate.isEmpty() || toDate.isEmpty() ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show the progress bar while the API call is in progress
            reportProgressBar.visibility = View.VISIBLE

            // Create the request object with the dynamic values
            val request = AttendanceReportRequest(
                from_date = fromDate,
                to_date = toDate,
                supervisor_name = supervisor
            )

            // Make the API call using Retrofit
            RetrofitClient.reportApiService().getAttendanceReport("Bearer $token", request)
                .enqueue(object : Callback<List<AttendanceRecord>> { // Expecting a list of records
                    override fun onResponse(
                        call: Call<List<AttendanceRecord>>,
                        response: Response<List<AttendanceRecord>>
                    ) {
                        reportProgressBar.visibility = View.GONE
                        if (response.isSuccessful) {
                            val records = response.body()
                            records?.let { recordsList ->
                                // Pass the records to display data in the table
                                resetForm()
                                displayDataInTable1(recordsList)
                            } ?: Toast.makeText(this@ReportActivity, "No records found.", Toast.LENGTH_SHORT).show()
                        } else {
                            when (response.code()) {
                                404 -> {
                                    Toast.makeText(
                                        this@ReportActivity,
                                        "No data found for the given period.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else -> {
                                    Toast.makeText(this@ReportActivity, "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<AttendanceRecord>>, t: Throwable) {
                        reportProgressBar.visibility = View.GONE
                        Toast.makeText(this@ReportActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
        back.visibility = View.GONE
        back.setOnClickListener {
            showForm()
            hideTable()
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


        // Check if the user is admin or supervisor

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
    }

    fun displayDataInTable1(records: List<AttendanceRecord>) {
        // Sort records by employee_name only
        val sortedRecords = records.sortedBy { it.employee_name }

        // Remove any existing views in the tables
        fixedColumnTable.removeAllViews()
        scrollableColumnTable.removeAllViews()

        // Add header for fixed column (Employee Code)
        val fixedHeaderRow = TableRow(this)
        fixedHeaderRow.addView(createHeaderTextView("Employee Code"))
        fixedColumnTable.addView(fixedHeaderRow)

        // Add header for scrollable columns (Employee Name, Zone, Department, Category, Supervisor)
        val scrollableHeaderRow = TableRow(this)
        val fixedColumns = listOf("Employee Name", "Zone", "Department", "Category", "Supervisor")
        fixedColumns.forEach { header ->
            scrollableHeaderRow.addView(createHeaderTextView(header))
        }

        // Parse and sort dates
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateHeaders = sortedRecords.map { it.date_of_work }.distinct()
            .sortedBy { dateFormat.parse(it) }

        // Add date-based columns for SK, SK OT, SSK, SSK OT, USK, USK OT for each date
        dateHeaders.forEach { date ->
            scrollableHeaderRow.addView(createHeaderTextView3("SK\n ($date)"))
            scrollableHeaderRow.addView(createHeaderTextView3("OT\n ($date)"))
            scrollableHeaderRow.addView(createHeaderTextView3("SSK\n ($date)"))
            scrollableHeaderRow.addView(createHeaderTextView3("OT\n ($date)"))
            scrollableHeaderRow.addView(createHeaderTextView3("USK\n ($date)"))
            scrollableHeaderRow.addView(createHeaderTextView3("OT\n ($date)"))
        }

        // Add Total Attendance and Total OT columns
        scrollableHeaderRow.addView(createHeaderTextView("Total Attendance"))
        scrollableHeaderRow.addView(createHeaderTextView("Total OT"))

        // Add header row to the scrollable table
        scrollableColumnTable.addView(scrollableHeaderRow)

        // Initialize total counters for each date column
        val totalSk = MutableList(dateHeaders.size) { 0 }
        val totalSkOt = MutableList(dateHeaders.size) { 0 }
        val totalSsk = MutableList(dateHeaders.size) { 0 }
        val totalSskOt = MutableList(dateHeaders.size) { 0 }
        val totalUsk = MutableList(dateHeaders.size) { 0 }
        val totalUskOt = MutableList(dateHeaders.size) { 0 }

        // Initialize total counters for attendance and OT
        var totalAttendanceForMonth = 0
        var totalOTForMonth = 0

        // Populate data rows
        sortedRecords.forEach { record ->
            val fixedRow = TableRow(this)
            fixedRow.addView(createDataTextView(record.employee_code))
            fixedColumnTable.addView(fixedRow)

            val scrollableRow = TableRow(this)
            val data = mutableListOf<String>()

            // Add fixed columns (Employee Name, Zone, Department, Category, Supervisor)
            data.add(record.employee_name)
            data.add(record.zone)
            data.add(record.department)
            data.add(record.category)
            data.add(record.supervisor_name)

            // Add date-based data for SK, SK OT, SSK, SSK OT, USK, USK OT for each date
            var totalAttendance = 0
            var totalOT = 0
            dateHeaders.forEachIndexed { index, date ->
                val recordForDate = sortedRecords.find { it.date_of_work == date && it.employee_code == record.employee_code }
                val sk = recordForDate?.sk ?: 0
                val skOt = recordForDate?.sk_ot ?: 0
                val ssk = recordForDate?.ssk ?: 0
                val sskOt = recordForDate?.ssk_ot ?: 0
                val usk = recordForDate?.usk ?: 0
                val uskOt = recordForDate?.usk_ot ?: 0

                // Update totals for each date
                totalSk[index] += sk
                totalSkOt[index] += skOt
                totalSsk[index] += ssk
                totalSskOt[index] += sskOt
                totalUsk[index] += usk
                totalUskOt[index] += uskOt

                // Add SK, SK OT, SSK, SSK OT, USK, USK OT data for the date
                data.add(sk.toString())
                data.add(skOt.toString())
                data.add(ssk.toString())
                data.add(sskOt.toString())
                data.add(usk.toString())
                data.add(uskOt.toString())

                // Calculate attendance and OT for this row
                totalAttendance += sk + ssk + usk
                totalOT += skOt + sskOt + uskOt
            }

            // Add the calculated total attendance and total OT for this row
            data.add(totalAttendance.toString())
            data.add(totalOT.toString())

            // Add the data row for the employee
            data.forEach { value ->
                scrollableRow.addView(createDataTextView(value))
            }
            scrollableColumnTable.addView(scrollableRow)

            // Update the totals for the whole month
            totalAttendanceForMonth += totalAttendance
            totalOTForMonth += totalOT
        }

//        // Add total row for totals across all employees for each date's columns and Total Attendance / Total OT
//        val totalRow = TableRow(this)
//        totalRow.addView(createDataTextView(" "))
//
//        // Add blank cells for "Employee Name", "Zone", "Department", "Category", "Supervisor"
//        repeat(4) {
//            totalRow.addView(createDataTextView(""))
//        }
//
//        // Add totals for each date's columns (SK, SK OT, SSK, SSK OT, USK, USK OT)
//        dateHeaders.forEachIndexed { index, _ ->
//            totalRow.addView(createDataTextView(totalSk[index].toString()))
//            totalRow.addView(createDataTextView(totalSkOt[index].toString()))
//            totalRow.addView(createDataTextView(totalSsk[index].toString()))
//            totalRow.addView(createDataTextView(totalSskOt[index].toString()))
//            totalRow.addView(createDataTextView(totalUsk[index].toString()))
//            totalRow.addView(createDataTextView(totalUskOt[index].toString()))
//        }
//
//        // Add total attendance and total OT for the whole month
//        totalRow.addView(createDataTextView(totalAttendanceForMonth.toString()))
//        totalRow.addView(createDataTextView(totalOTForMonth.toString()))
//
//        // Add the total row to the scrollable table
//        scrollableColumnTable.addView(totalRow)
//
//        // Add the same total row to the fixed table (if necessary, for the "Employee Code" column)
//        val fixedTotalRow = TableRow(this)
//        fixedTotalRow.addView(createDataTextView("Total"))
//        fixedColumnTable.addView(fixedTotalRow)

        // Add totals row to the fixed column
        val fixedTotalRow = TableRow(this)
        fixedTotalRow.addView(createDataTextView("Total")) // Add "Total" in the fixed column
        fixedColumnTable.addView(fixedTotalRow)

// Add totals row to the scrollable table
        val scrollableTotalRow = TableRow(this)


        repeat(5) {
            scrollableTotalRow.addView(createDataTextView(""))
        }

        dateHeaders.forEachIndexed { index, _ ->
            scrollableTotalRow.addView(createDataTextView(totalSk[index].toString()))
            scrollableTotalRow.addView(createDataTextView(totalSkOt[index].toString()))
            scrollableTotalRow.addView(createDataTextView(totalSsk[index].toString()))
            scrollableTotalRow.addView(createDataTextView(totalSskOt[index].toString()))
            scrollableTotalRow.addView(createDataTextView(totalUsk[index].toString()))
            scrollableTotalRow.addView(createDataTextView(totalUskOt[index].toString()))
        }

        scrollableTotalRow.addView(createDataTextView(totalAttendanceForMonth.toString()))
        scrollableTotalRow.addView(createDataTextView(totalOTForMonth.toString()))
        scrollableColumnTable.addView(scrollableTotalRow)

        // Hide the form and show the table
        hideForm()
        showTable()
    }

//    fun displayDataInTable1(records: List<AttendanceRecord>) {
//        // Sort records by employee_name
//        val sortedRecords = records.sortedBy { it.employee_name }
//
//        // Clear previous data
//        fixedColumnTable.removeAllViews()
//        scrollableColumnTable.removeAllViews()
//
//        // Add header for the fixed column (Employee Code)
//        val fixedHeaderRow = TableRow(this)
//        fixedHeaderRow.addView(createHeaderTextView2("Employee Code"))
//        fixedColumnTable.addView(fixedHeaderRow)
//
//        // Add subheader for the fixed column
//        val fixedSubHeaderRow = TableRow(this)
//        val subHeaderTextView = createHeaderTextView1(" Employee Code ")
//
//// Set layout parameters with increased height
//        val layoutParams = TableRow.LayoutParams(
//            TableRow.LayoutParams.MATCH_PARENT,
//            136 // Specify height in pixels (e.g., 100px)
//        ).apply {
//            topMargin = 30 // Add top margin if needed
//        }
//
//// Apply the layout parameters to the TextView
//        subHeaderTextView.layoutParams = layoutParams
//
//// Add padding for additional spacing inside the TextView (optional)
//        subHeaderTextView.setPadding(0, 20, 0, 20) // Top and bottom padding
//
//        fixedSubHeaderRow.addView(subHeaderTextView)
//        fixedColumnTable.addView(fixedSubHeaderRow)
//
//
//
//        // Add headers for the scrollable table
//        val scrollableMainHeaderRow = TableRow(this)
//        val scrollableSubHeaderRow = TableRow(this)
//
//        val fixedColumns = listOf("Employee Name", "Zone", "Department", "Category", "Supervisor")
//        fixedColumns.forEach { header ->
//            scrollableMainHeaderRow.addView(createHeaderTextView2(header))
//            scrollableSubHeaderRow.addView(createHeaderTextView(header))
//        }
//
//        // Parse and sort dates
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val dateHeaders = sortedRecords.map { it.date_of_work }.distinct()
//            .sortedBy { dateFormat.parse(it) }
//
//        // Add Date headers with subheaders
//        dateHeaders.forEach { date ->
//            val dateHeaderView = createHeaderTextView(date)
//            dateHeaderView.layoutParams = TableRow.LayoutParams().apply {
//                span = 6
//            }
//            scrollableMainHeaderRow.addView(dateHeaderView)
//
//            listOf(" SK ", " OT ", " SSK ", " OT ", " USK ", " OT ").forEach {
//                scrollableSubHeaderRow.addView(createHeaderTextView(it))
//            }
//        }
//
//        // Add total attendance and OT columns
//        scrollableMainHeaderRow.addView(createHeaderTextView2("Total Attendance"))
//        scrollableMainHeaderRow.addView(createHeaderTextView2("Total OT"))
//        scrollableSubHeaderRow.addView(createHeaderTextView("Total Attendance"))
//        scrollableSubHeaderRow.addView(createHeaderTextView("Total OT"))
//
//        // Add header rows
//        scrollableColumnTable.addView(scrollableMainHeaderRow)
//        scrollableColumnTable.addView(scrollableSubHeaderRow)
//
//        // Initialize total counters
//        val totalSk = MutableList(dateHeaders.size) { 0 }
//        val totalSkOt = MutableList(dateHeaders.size) { 0 }
//        val totalSsk = MutableList(dateHeaders.size) { 0 }
//        val totalSskOt = MutableList(dateHeaders.size) { 0 }
//        val totalUsk = MutableList(dateHeaders.size) { 0 }
//        val totalUskOt = MutableList(dateHeaders.size) { 0 }
//        var totalAttendanceForMonth = 0
//        var totalOTForMonth = 0
//
//        // Populate rows
//        sortedRecords.forEach { record ->
//            val fixedRow = TableRow(this)
//            fixedRow.addView(createDataTextView(record.employee_code))
//            fixedColumnTable.addView(fixedRow)
//
//            val scrollableRow = TableRow(this)
//            val rowData = mutableListOf<String>()
//
//            // Add fixed column data
//            rowData.add(record.employee_name)
//            rowData.add(record.zone)
//            rowData.add(record.department)
//            rowData.add(record.category)
//            rowData.add(record.supervisor_name)
//
//            // Add date-based data
//            var totalAttendance = 0
//            var totalOT = 0
//            dateHeaders.forEachIndexed { index, date ->
//                val recordForDate = sortedRecords.find { it.date_of_work == date && it.employee_code == record.employee_code }
//                val sk = recordForDate?.sk ?: 0
//                val skOt = recordForDate?.sk_ot ?: 0
//                val ssk = recordForDate?.ssk ?: 0
//                val sskOt = recordForDate?.ssk_ot ?: 0
//                val usk = recordForDate?.usk ?: 0
//                val uskOt = recordForDate?.usk_ot ?: 0
//
//                totalSk[index] += sk
//                totalSkOt[index] += skOt
//                totalSsk[index] += ssk
//                totalSskOt[index] += sskOt
//                totalUsk[index] += usk
//                totalUskOt[index] += uskOt
//
//                rowData.add(sk.toString())
//                rowData.add(skOt.toString())
//                rowData.add(ssk.toString())
//                rowData.add(sskOt.toString())
//                rowData.add(usk.toString())
//                rowData.add(uskOt.toString())
//
//                totalAttendance += sk + ssk + usk
//                totalOT += skOt + sskOt + uskOt
//            }
//
//            rowData.add(totalAttendance.toString())
//            rowData.add(totalOT.toString())
//
//            rowData.forEach { value ->
//                scrollableRow.addView(createDataTextView(value))
//            }
//            scrollableColumnTable.addView(scrollableRow)
//
//            totalAttendanceForMonth += totalAttendance
//            totalOTForMonth += totalOT
//        }
//
//        // Add totals row to the fixed column
//        val fixedTotalRow = TableRow(this)
//        fixedTotalRow.addView(createDataTextView("Total")) // Add "Total" in the fixed column
//        fixedColumnTable.addView(fixedTotalRow)
//
//// Add totals row to the scrollable table
//        val scrollableTotalRow = TableRow(this)
//
//
//        repeat(5) {
//            scrollableTotalRow.addView(createDataTextView(""))
//        }
//
//        dateHeaders.forEachIndexed { index, _ ->
//            scrollableTotalRow.addView(createDataTextView(totalSk[index].toString()))
//            scrollableTotalRow.addView(createDataTextView(totalSkOt[index].toString()))
//            scrollableTotalRow.addView(createDataTextView(totalSsk[index].toString()))
//            scrollableTotalRow.addView(createDataTextView(totalSskOt[index].toString()))
//            scrollableTotalRow.addView(createDataTextView(totalUsk[index].toString()))
//            scrollableTotalRow.addView(createDataTextView(totalUskOt[index].toString()))
//        }
//
//        scrollableTotalRow.addView(createDataTextView(totalAttendanceForMonth.toString()))
//        scrollableTotalRow.addView(createDataTextView(totalOTForMonth.toString()))
//        scrollableColumnTable.addView(scrollableTotalRow)
//
//        showTable()
//        hideForm()
//    }


    // Helper function to create header TextViews with bold text and background
    private fun createHeaderTextView(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            setPadding(15, 15, 15, 15)  // Add padding to make the text more readable
            setBackgroundResource(R.drawable.table_row)  // Set a background (assuming you have this drawable)
            setTypeface(null, Typeface.BOLD)  // Set bold text style
            setTextColor(Color.WHITE)  // Set text color to black
            gravity = Gravity.CENTER  // Center the text inside the TextView

            // Ensure the height is the same across all header rows (convert dp to px)
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                dpToPx(50)  // Using dpToPx to ensure height is consistent across screen densities
            )
        }
    }

    private fun createHeaderTextView3(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            setPadding(15, 15, 15, 15)  // Add padding to make the text more readable
            setBackgroundResource(R.drawable.table_row)  // Set a background (assuming you have this drawable)
            setTypeface(null, Typeface.BOLD)  // Set bold text style
            setTextColor(Color.WHITE)  // Set text color to black
            gravity = Gravity.CENTER  // Center the text inside the TextView

            // Ensure the height is the same across all header rows (convert dp to px)
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                dpToPx(55)  // Using dpToPx to ensure height is consistent across screen densities
            ).apply {
                topMargin = dpToPx(-6)
            }
        }
    }

    private fun createHeaderTextView1(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            setBackgroundResource(R.drawable.table_row)  // Set a background (assuming you have this drawable)
            setTypeface(null, Typeface.BOLD)  // Set bold text style
            setTextColor(Color.WHITE)  // Set text color to black
            gravity = Gravity.CENTER  // Center the text inside the TextView

        }
    }

    // Helper function to convert dp to px for consistency
    fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
    private fun createHeaderTextView2(s: String): View? {
        return TextView(this).apply {
            this.text = text
            setPadding(0, 0, 0, 0)  // Add padding to make the text more readable
            setTypeface(null, Typeface.BOLD)  // Set bold text style
            setTextColor(Color.WHITE)  // Set text color to black
            gravity = Gravity.CENTER  // Center the text inside the TextView

        }
    }


    // Helper function to create data TextViews with consistent styling
    private fun createDataTextView(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            setPadding(15, 15, 15, 15)
            setBackgroundResource(R.drawable.table_column)
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                150 // Fixed height in pixels
            )
        }
    }

    //Creating Download options
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.download_option, menu)

        // Find your menu item
        val downloadItem1 = menu?.findItem(R.id.download1)
        downloadItem1?.isEnabled = false
        val downloadItem2 = menu?.findItem(R.id.download2)
        downloadItem2?.isEnabled = false


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

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        // This method is called whenever the menu is being prepared or invalidated
        if (isMenuEnabled) {
            // Enable menu items when the flag is true
            menu?.findItem(R.id.download1)?.isEnabled = true
            menu?.findItem(R.id.download2)?.isEnabled = true
            // Enable more items here if needed
        }
        return super.onPrepareOptionsMenu(menu)
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
            put(MediaStore.MediaColumns.DISPLAY_NAME, "EmployeeData_${System.currentTimeMillis()}.pdf")
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
                val headerFontSize = 9f
                val cellFontSize = 8f

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
        val sheet: XSSFSheet = workbook.createSheet("Employee Data") as XSSFSheet

        // Adjust column widths for the headers
        val fixedColumnChildCount = (fixedColumnTable.getChildAt(0) as TableRow).childCount
        for (i in 0 until fixedColumnChildCount) {
            sheet.setColumnWidth(i, 6000) // Adjust width for fixed columns
        }

        // Populate rows with data from both tables
        for (i in 0 until fixedColumnTable.childCount) { // fixedColumnTable is the TableLayout
            val fixedRow = fixedColumnTable.getChildAt(i) as TableRow
            val scrollableRow = scrollableColumnTable.getChildAt(i) as TableRow
            val dataRow = sheet.createRow(i + 1) // Start from the second row

            // Add data from fixedRow
            for (j in 0 until fixedRow.childCount) {
                val view = fixedRow.getChildAt(j)
                if (view is TextView) {
                    val cellText = view.text.toString()
                    dataRow.createCell(j).setCellValue(cellText) // Add data from fixedRow to Excel

                    // Adjust column width for this header
                    if (i == 0) { // For header row
                        sheet.setColumnWidth(j, 6000) // Adjust width as needed
                    }
                }
            }

            // Add data from scrollableRow (excluding action buttons)
            for (j in 0 until scrollableRow.childCount) {
                val view = scrollableRow.getChildAt(j)
                if (view is TextView) {
                    val cellText = view.text.toString()
                    dataRow.createCell(j + fixedRow.childCount).setCellValue(cellText) // Adjust index for Excel

                    // Adjust column width for this header
                    if (i == 0) { // For header row
                        sheet.setColumnWidth(j + fixedRow.childCount, 6000) // Adjust width as needed
                    }
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


    private fun resetForm(){
        dateInput1.text?.clear()
        dateInput2.text?.clear()
        if (SharedPreferencesHelper.getIsAdmin(this)) {
            supervisorInput.text?.clear()
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

}
