package com.example.myapplication

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.pm.ActivityInfo
import android.graphics.Color
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
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import com.example.myapplication.interface_api.JobSheetDetails
import com.example.myapplication.interface_api.JobSheetDetailsResponse
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class JobSetDetailsActivity : AppCompatActivity() {

    private lateinit var dateLayout: TextInputLayout
    private lateinit var zoneLayout: TextInputLayout
    private lateinit var supervisorLayout: TextInputLayout
    private lateinit var lowStubLayout: TextInputLayout
    private lateinit var anodeCoveringLayout: TextInputLayout
    private lateinit var sideMakingLayout: TextInputLayout
    private lateinit var holeLayout: TextInputLayout
    private lateinit var houseKeepingLayout: TextInputLayout
    private lateinit var supplyLayout: TextInputLayout

    private lateinit var dateInput: TextInputEditText
    private lateinit var zoneInput: AutoCompleteTextView
    private lateinit var supervisorInput: TextInputEditText
    private lateinit var lowStubInput: TextInputEditText
    private lateinit var anodeCoveringInput: TextInputEditText
    private lateinit var holeInput: TextInputEditText
    private lateinit var houseKeepingInput: TextInputEditText
    private lateinit var supplyInput: TextInputEditText
    private lateinit var sideMakingInput: TextInputEditText

    private lateinit var save: Button
    private lateinit var saveProgressBar: ProgressBar
    private lateinit var back: Button

    private lateinit var formLayout: LinearLayout
    private lateinit var tableLayout: LinearLayout
    private lateinit var toolbar_text: TextView

    @SuppressLint("MissingInflatedId", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        setContentView(R.layout.activity_job_set_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.button)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        dateLayout = findViewById(R.id.date)
        dateInput = findViewById(R.id.dateInput)
        zoneLayout = findViewById(R.id.zone)
        zoneInput = findViewById(R.id.zoneInput)
        supervisorLayout = findViewById(R.id.supervisor)
        supervisorInput = findViewById(R.id.supervisorInput)
        lowStubLayout = findViewById(R.id.lowStub)
        lowStubInput = findViewById(R.id.lowStubInput)
        anodeCoveringLayout = findViewById(R.id.anodeCovering)
        anodeCoveringInput = findViewById(R.id.anodeCoveringInput)
        sideMakingLayout = findViewById(R.id.sideMaking)
        sideMakingInput = findViewById(R.id.sideMakingInput)
        holeLayout = findViewById(R.id.hole)
        holeInput = findViewById(R.id.holeInput)
        houseKeepingLayout = findViewById(R.id.houseKeeping)
        houseKeepingInput = findViewById(R.id.houseKeepingInput)
        supplyLayout = findViewById(R.id.supply)
        supplyInput = findViewById(R.id.supplyInput)
        save = findViewById(R.id.save)
        saveProgressBar = findViewById(R.id.saveProgressBar)
        back = findViewById(R.id.back)
        formLayout = findViewById(R.id.formLayout)
        tableLayout = findViewById(R.id.tableLayout)
        toolbar_text = findViewById(R.id.toolbar_text)

        // Initialize the Calendar and SimpleDateFormat
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Set the default date to today's date in the dojInput
        val currentDate = dateFormat.format(calendar.time)
        dateInput.setText(currentDate) // Display today's date by default

        // Disable direct editing of the input field
        dateInput.isFocusable = false
        dateInput.isFocusableInTouchMode = false

        // Set up the onClickListener to open the DatePickerDialog
        dateInput.setOnClickListener {
            val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                // Update the calendar with the selected date
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Format and display the selected date
                val selectedDate = dateFormat.format(calendar.time)
                Log.d("SelectedDate", "Date selected: $selectedDate") // Log the selected date
                dateInput.setText(selectedDate)
            }

            // Show the DatePickerDialog with today's date as default
            val dateDialog = DatePickerDialog(
                this,
                dateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dateDialog.show()
        }


        // Define the dropdown items
        val items = listOf("zone-1", "zone-2", "zone-3", "zone-4")

        // Create an ArrayAdapter with your list of items
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)

        // Attach the adapter to the AutoCompleteTextView
        zoneInput.setAdapter(adapter)

        // Set the dropdown to automatically show when the user clicks inside the field
        zoneInput.setOnClickListener {
            zoneInput.showDropDown()
        }

        // Handle item selection from the dropdown menu
        zoneInput.setOnItemClickListener { parent, _, position, _ ->
            val itemSelected = parent.getItemAtPosition(position) as String
            Toast.makeText(this, "Selected Zone: $itemSelected", Toast.LENGTH_SHORT).show()

        }

        save.setOnClickListener {

            // Validate inputs
            val dateEntry = dateInput.text.toString()
            val zoneEntry = zoneInput.text.toString()
            val supervisorNameEntry = supervisorInput.text.toString()
            val lowStubInputText = lowStubInput.text.toString()
            val anodeCoveringInputText = anodeCoveringInput.text.toString()
            val sideMakingInputText = sideMakingInput.text.toString()
            val holeInputText = holeInput.text.toString()
            val houseKeepingInputText = houseKeepingInput.text.toString()
            val supplyInputText = supplyInput.text.toString()

            if (dateEntry.isEmpty() || zoneEntry.isEmpty() || supervisorNameEntry.isEmpty() ||
                lowStubInputText.isEmpty() || anodeCoveringInputText.isEmpty() ||
                sideMakingInputText.isEmpty() || holeInputText.isEmpty() ||
                houseKeepingInputText.isEmpty() || supplyInputText.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

                // Collect data dynamically from UI
                val date = dateInput.text.toString()
                val zone = zoneInput.text.toString()
                val supervisorName = supervisorInput.text.toString()
                val lowStub = lowStubInput.text.toString().toIntOrNull() ?: 0
                val anodeCovering = anodeCoveringInput.text.toString().toIntOrNull() ?: 0
                val sideMaking = sideMakingInput.text.toString().toIntOrNull() ?: 0
                val hole = holeInput.text.toString().toIntOrNull() ?: 0
                val houseKeeping = houseKeepingInput.text.toString().toIntOrNull() ?: 0
                val supply = supplyInput.text.toString().toIntOrNull() ?: 0

                // Create JobSheetDetails object with dynamic values
                val jobSheetDetails = JobSheetDetails(
                    date = date,
                    zone = zone,
                    supervisor_name = supervisorName,
                    low_stub = lowStub,
                    anode_covering = anodeCovering,
                    side_making = sideMaking,
                    hole = hole,
                    house_keeping = houseKeeping,
                    supply = supply
                )

                // Retrieve the access token from SharedPreferencesHelper
                val accessToken = SharedPreferencesHelper.getAccessToken(this)

                // Call the function to submit data
                submitJobSheetData(jobSheetDetails, accessToken)

        }

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

    private fun submitJobSheetData(jobSheetDetails: JobSheetDetails, token: String?) {
        // Ensure token is not null
        val accessToken = token?.let { "Bearer $it" } ?: run {
            Toast.makeText(this, "Access token is missing", Toast.LENGTH_SHORT).show()
            return
        }

        // Show progress bar
        saveProgressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Make the network request using ApiClient's jobSheetDetailsApi
                val response = RetrofitClient.jobSheetDetailsApi.submitJobSheetDetails(accessToken, jobSheetDetails)

                // Handle the response on the main thread
                runOnUiThread {
                    saveProgressBar.visibility = View.GONE // Hide progress bar
                    if (response.isSuccessful) {
                        val savedJobSheetDetails = response.body()
                        if (savedJobSheetDetails != null) {
                            // Populate the table with the received data
                            populateTable(savedJobSheetDetails)
                        }
                        Toast.makeText(this@JobSetDetailsActivity, "Data submitted successfully!", Toast.LENGTH_SHORT).show()
                        resetFormFields()
                    } else {
                        // Extract error message from response body
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = if (!errorBody.isNullOrEmpty()) {
                            // Parse error body if possible
                            try {
                                val jsonObject = JSONObject(errorBody)
                                jsonObject.optString("message", errorBody) // Use the "message" field or fallback to raw body
                            } catch (e: JSONException) {
                                errorBody // Fallback to raw body if parsing fails
                            }
                        } else {
                            "Unknown error occurred."
                        }

                        // Check if it's a conflict error
                        if (errorMessage.contains("already exists", ignoreCase = true)) {
                            val supervisorName = jobSheetDetails.supervisor_name
                            val date = jobSheetDetails.date
                            Toast.makeText(
                                this@JobSetDetailsActivity,
                                "Data for supervisor '$supervisorName' on date '$date' already exists.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(this@JobSetDetailsActivity, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle exception (e.g., no internet connection)
                runOnUiThread {
                    saveProgressBar.visibility = View.GONE // Hide progress bar
                    Toast.makeText(this@JobSetDetailsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    // Function to populate the table with skilled, semi-skilled, and unskilled values
    private fun populateTable(jobSheetDetails: JobSheetDetailsResponse) {
        val tableLayout = findViewById<TableLayout>(R.id.fixedColumnTable)

        // Clear previous data in the table
        tableLayout.removeAllViews()

        // Create the header row for skilled, semi-skilled, unskilled
        val headerRow = TableRow(this)
        headerRow.gravity = Gravity.CENTER

        // Set background for header using the header shape
        headerRow.background = ContextCompat.getDrawable(this, R.drawable.table_row)

        // Add header text with styling
        headerRow.addView(createTextView("Skilled", isHeader = true))
        headerRow.addView(createVerticalDivider()) // Divider between columns
        headerRow.addView(createTextView("Semi Skilled", isHeader = true))
        headerRow.addView(createVerticalDivider()) // Divider between columns
        headerRow.addView(createTextView("Unskilled", isHeader = true))

        tableLayout.addView(headerRow)

        // Create a row for the calculated values
        val valueRow = TableRow(this)
        valueRow.gravity = Gravity.CENTER

        // Set background for the data row using the data shape
        valueRow.background = ContextCompat.getDrawable(this, R.drawable.table_column)

        valueRow.addView(createTextView(jobSheetDetails.skilled.toString()))
        valueRow.addView(createVerticalDivider()) // Divider between columns
        valueRow.addView(createTextView(jobSheetDetails.semi_skilled.toString()))
        valueRow.addView(createVerticalDivider()) // Divider between columns
        valueRow.addView(createTextView(jobSheetDetails.unskilled.toString()))

        tableLayout.addView(valueRow)

        // Hide the form and show the table
        hideForm()
        showTable()
    }

    // Helper function to create TextView
    private fun createTextView(text: String, isHeader: Boolean = false): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.gravity = Gravity.CENTER
        textView.setPadding(70, 70, 70, 70) // Optional: Padding for better spacing

        // Set text size and other properties based on whether it's a header or not
        if (isHeader) {
            textView.textSize = 18f // Set larger text size for header
            textView.setTextColor(ContextCompat.getColor(this, R.color.white)) // Set white text color for header
        } else {
            textView.textSize = 16f // Set default text size for values
        }

        return textView
    }

    // Helper function to create vertical divider between columns
    private fun createVerticalDivider(): View {
        val divider = View(this)
        val params = TableRow.LayoutParams(1, TableRow.LayoutParams.MATCH_PARENT)
        divider.layoutParams = params
        divider.setBackgroundColor(ContextCompat.getColor(this, R.color.divider)) // Set divider color
        return divider
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
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Job_set_details_${System.currentTimeMillis()}.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
        }

        val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        uri?.let {
            try {
                val pdfWriter = PdfWriter(contentResolver.openOutputStream(it)!!)
                val pdfDocument = PdfDocument(pdfWriter)
                pdfDocument.defaultPageSize = PageSize.A4.rotate() // Set to landscape orientation
                val document = Document(pdfDocument)

                // Create a table with number of columns
                val table = Table(3)  // Assuming 4 columns in the table (modify as needed)

                table.setWidth(UnitValue.createPercentValue(90f)) // Table takes 90% of the page width
                table.setHorizontalAlignment(HorizontalAlignment.CENTER)
                // Add header and rows to the PDF table
                val tableLayout = findViewById<TableLayout>(R.id.fixedColumnTable)

                // Iterate through all rows in the TableLayout
                for (i in 0 until tableLayout.childCount) {
                    val row = tableLayout.getChildAt(i) as TableRow

                    for (j in 0 until row.childCount) {
                        val cellView = row.getChildAt(j)
                        if (cellView is TextView) {
                            val cellText = cellView.text.toString()
                            table.addCell(Paragraph(cellText).setTextAlignment(TextAlignment.CENTER))
                        }
                    }
                }

                // Add the table to the document
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

    private fun downloadTableAsExcel() {
        // Create an Excel file in the Documents directory
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Job_set_details_${System.currentTimeMillis()}.xls")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.ms-excel")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
        }

        val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        uri?.let {
            try {
                val workbook = HSSFWorkbook() // Using HSSFWorkbook for .xls format (Excel 97-2003)
                val sheet = workbook.createSheet("Table Data")

                val tableLayout = findViewById<TableLayout>(R.id.fixedColumnTable)

                // Iterate through all rows in the TableLayout
                for (i in 0 until tableLayout.childCount) {
                    val row = sheet.createRow(i)
                    val tableRow = tableLayout.getChildAt(i) as TableRow

                    for (j in 0 until tableRow.childCount) {
                        val cellView = tableRow.getChildAt(j)
                        if (cellView is TextView) {
                            val cellText = cellView.text.toString()
                            val cell = row.createCell(j)
                            cell.setCellValue(cellText)
                        }
                    }
                }

                // Write Excel file
                val outputStream = contentResolver.openOutputStream(it)
                workbook.write(outputStream)
                workbook.close()

                Toast.makeText(this, "Excel file downloaded successfully", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Error creating Excel file: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
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
        formLayout.visibility = View.GONE
        back.visibility = View.VISIBLE
    }

    private fun hideTable() {
        tableLayout.visibility = View.GONE
        back.visibility = View.GONE
    }

    private fun showForm() {
        formLayout.visibility = View.VISIBLE
        back.visibility = View.GONE
    }

    private fun resetFormFields() {
        // Reset all EditText fields
        zoneInput.text.clear()
        supervisorInput.text?.clear()
        lowStubInput.text?.clear()
        anodeCoveringInput.text?.clear()
        sideMakingInput.text?.clear()
        holeInput.text?.clear()
        houseKeepingInput.text?.clear()
        supplyInput.text?.clear()

    }

}