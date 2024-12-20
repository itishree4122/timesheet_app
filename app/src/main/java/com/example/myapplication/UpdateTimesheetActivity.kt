package com.example.myapplication

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
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
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.backend_api.RetrofitClient
import com.example.myapplication.backend_api.RetrofitClient.attendanceApiService
import com.example.myapplication.backend_api.SharedPreferencesHelper
import com.example.myapplication.interface_api.EmployeeData
import com.example.myapplication.interface_api.EmployeeSearchRequest
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.UnitValue
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class UpdateTimesheetActivity : AppCompatActivity() {

    private lateinit var fixedTable: TableLayout
    private lateinit var scrollableTable: TableLayout

    private lateinit var dojLayout: TextInputLayout
    private lateinit var zoneLayout: TextInputLayout
    private lateinit var empCodeLayout: TextInputLayout
    private lateinit var empNameLayout: TextInputLayout
    private lateinit var deptLayout: TextInputLayout
    private lateinit var ctgrLayout: TextInputLayout
    private lateinit var sprLayout: TextInputLayout

    private lateinit var dojInput: TextInputEditText
    private lateinit var zoneInput: AutoCompleteTextView
    private lateinit var empCodeInput: TextInputEditText
    private lateinit var empNameInput: TextInputEditText
    private lateinit var deptInput: TextInputEditText
    private lateinit var ctgrInput: TextInputEditText
    private lateinit var sprInput: TextInputEditText

    private lateinit var serchBtn: Button
    private lateinit var resetBtn: Button
    private lateinit var cardView2: CardView

    private var editingFixedRow: TableRow? = null
    private var editingScrollableRow: TableRow? = null

    private var selectedRowIndex: Int = -1


    private lateinit var searchProgressBar: ProgressBar
    private val existingCode = mutableSetOf<String>()


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_timesheet)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.purple)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        dojLayout = findViewById(R.id.dateOfJoin)
        zoneLayout = findViewById(R.id.zone)
        empCodeLayout = findViewById(R.id.employeeCode)
        empNameLayout = findViewById(R.id.employeeName)
        deptLayout = findViewById(R.id.department)
        ctgrLayout = findViewById(R.id.Category)
        sprLayout = findViewById(R.id.supervisor)

        dojInput = findViewById(R.id.dateOfJoin2)
        zoneInput = findViewById(R.id.zone2)
        empCodeInput = findViewById(R.id.employeeCode2)
        empNameInput = findViewById(R.id.employeeName2)
        deptInput = findViewById(R.id.department2)
        ctgrInput = findViewById(R.id.Category2)
        sprInput = findViewById(R.id.supervisor2)

        serchBtn = findViewById(R.id.search)
        resetBtn = findViewById(R.id.Reset)

        cardView2 = findViewById(R.id.cardView2)

        fixedTable = findViewById(R.id.fixedTable)
        scrollableTable = findViewById(R.id.scrollableTable)


        searchProgressBar = findViewById(R.id.searchProgressBar)

        // Disable keyboard for the EditText
        dojInput.isFocusable = false
        dojInput.isFocusableInTouchMode = false

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")) // Use UTC to prevent timezone shift
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC") // Ensure the date is in UTC

        dojInput.setOnClickListener {
            val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Set the formatted date
                val selectedDate = dateFormat.format(calendar.time)
                Log.d("SelectedDate", "Date selected: $selectedDate")
                dojInput.setText(selectedDate)
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



        // Define the dropdown items
        val items = listOf("all","zone-1", "zone-2", "zone-3", "zone-4")

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


        serchBtn.setOnClickListener {

            searchProgressBar.visibility = View.VISIBLE
            // Retrieve access token
            val accessToken = SharedPreferencesHelper.getAccessToken(this)

            if (accessToken == null) {
                Toast.makeText(
                    this,
                    "Token not found. Please log in again.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Get the search criteria from the input fields
            val employeeCode = empCodeInput.text.toString()
            val employeeName = empNameInput.text.toString() // Assuming you have an input for employee name
            val zone = zoneInput.text.toString() // Assuming you have an input for zone
            val category = ctgrInput.text.toString() // Assuming you have an input for category
            val supervisorName =
                sprInput.text.toString() // Assuming you have an input for supervisor name

            // Check if all input fields are empty
            if (employeeCode.isEmpty() && employeeName.isEmpty() && zone.isEmpty() && category.isEmpty() && supervisorName.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter at least one search criterion.",
                    Toast.LENGTH_SHORT
                ).show()
                searchProgressBar.visibility = View.GONE
                return@setOnClickListener
            }

            // Call the API to search employee data
//            searchEmployeeData(
//                accessToken,
//                employeeCode = if (employeeCode.isNotEmpty()) employeeCode else null,
//                employeeName = if (employeeName.isNotEmpty()) employeeName else null,
//                zone = if (zone.isNotEmpty()) zone else null,
//                category = if (category.isNotEmpty()) category else null,
//                supervisorName = if (supervisorName.isNotEmpty()) supervisorName else null
//            )

        }

        resetBtn.setOnClickListener {
            resetForm()
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
    // Download Table as PDF
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
                val document = Document(pdfDocument)

                // Set margins (top, right, bottom, left)
                document.setMargins(10f, 10f, 10f, 10f)

                // Define column widths (adjust as necessary)
                val columnWidths = floatArrayOf(2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f)
                val table = Table(columnWidths).setWidth(UnitValue.createPercentValue(100f))

                // Add data rows from both tables
                for (i in 0 until fixedTable.childCount) {
                    val fixedRow = fixedTable.getChildAt(i) as TableRow
                    val scrollableRow = scrollableTable.getChildAt(i) as TableRow

                    // Get data from fixedRow
                    for (j in 0 until fixedRow.childCount) {
                        val view = fixedRow.getChildAt(j)
                        val cellText = if (view is TextView) view.text.toString() else ""
                        table.addCell(Paragraph(cellText))
                    }

                    // Get data from scrollableRow
                    for (j in 0 until scrollableRow.childCount - 1) { // Exclude the last column for Action buttons
                        val view = scrollableRow.getChildAt(j)
                        val cellText = if (view is TextView) view.text.toString() else (view as? EditText)?.text.toString()
                        table.addCell(Paragraph(cellText))
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

        // Populate rows with data from both tables
        for (i in 0 until fixedTable.childCount) {
            val fixedRow = fixedTable.getChildAt(i) as TableRow
            val scrollableRow = scrollableTable.getChildAt(i) as TableRow
            val dataRow = sheet.createRow(i + 1) // Start from the second row

            // Get data from fixedRow
            for (j in 0 until fixedRow.childCount) {
                val view = fixedRow.getChildAt(j)
                val cellText = if (view is TextView) view.text.toString() else ""
                dataRow.createCell(j).setCellValue(cellText)
            }

            // Get data from scrollableRow
            for (j in 0 until scrollableRow.childCount - 1) { // Exclude last column for Action buttons
                val view = scrollableRow.getChildAt(j)
                val cellText = if (view is TextView) view.text.toString() else (view as? EditText)?.text.toString()
                dataRow.createCell(j + fixedRow.childCount).setCellValue(cellText)
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

//    private fun searchEmployeeData(
//        accessToken: String,
//        employeeCode: String? = null,
//        employeeName: String? = null,
//        zone: String? = null,
//        category: String? = null,
//        supervisorName: String? = null
//    ) {
//        // Create an instance of your API service
//        val apiService = RetrofitClient.timeSheetEmployeeSearchApi()
//
//        // Create the search request body
//        val searchRequest = EmployeeSearchRequest(
//            employee_code = employeeCode,
//            employee_name = employeeName,
//            zone = zone,
//            category = category,
//            supervisor_name = supervisorName
//        )
//
//        // Call the API to search for employee data
//        apiService.searchTimeSheetEmployeeData("Bearer $accessToken", searchRequest).enqueue(object :
//            Callback<List<EmployeeData>> {
//            override fun onResponse(call: Call<List<EmployeeData>>, response: Response<List<EmployeeData>>) {
//
//                if (response.isSuccessful) {
//                    // Clear previous data from the table before displaying new data
//                    clearTable()
//
//                    // Assuming the response body contains a list of employee data
//                    val employeeDataList = response.body()
//
//                    // Check if employeeDataList is not null and populate the table
//                    if (!employeeDataList.isNullOrEmpty()) {
//                        // Iterate through the employee data list and add each row to the table
//                        for (employee in employeeDataList) {
//                            addDataToTable(
//                                dateOfJoin = employee.date_of_work,
//                                zone = employee.zone,
//                                employeeCode = employee.employee_code,
//                                employeeName = employee.employee_name,
//                                department = employee.department,
//                                category = employee.category,
//                                supervisor = employee.supervisor_name,
//                                sk = employee.sk,
//                                sk_ot = employee.sk_ot,
//                                ssk = employee.ssk,
//                                ssk_ot = employee.ssk_ot,
//                                usk = employee.usk,
//                                usk_ot = employee.usk_ot,
//                                attendance = employee.attendance
//                            )
//                        }
//                        searchProgressBar.visibility = View.GONE
//
//                        // Show a success toast message
//                        Toast.makeText(this@UpdateTimesheetActivity, "Employee data retrieved successfully.", Toast.LENGTH_SHORT).show()
//
//                        showCardView()
//                    } else {
//                        searchProgressBar.visibility = View.GONE
//                        Toast.makeText(this@UpdateTimesheetActivity, "No employee found with the given criteria.", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    searchProgressBar.visibility = View.GONE
//
//                    Toast.makeText(this@UpdateTimesheetActivity, "Error fetching employee data: ${response.message()}", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<List<EmployeeData>>, t: Throwable) {
//                searchProgressBar.visibility = View.GONE
//
//                Toast.makeText(this@UpdateTimesheetActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }

    private fun clearTable() {
        // Assuming the header is always the first row
        val headerRowCount = 1

        // Remove all child views except for the header row
        while (fixedTable.childCount > headerRowCount) {
            fixedTable.removeViewAt(headerRowCount)
        }
        while (scrollableTable.childCount > headerRowCount) {
            scrollableTable.removeViewAt(headerRowCount)
        }
    }

    // Method to add data to the table after successful save
    @SuppressLint("SuspiciousIndentation")
    private fun addDataToTable(
        dateOfJoin: String,
        zone: String,
        employeeCode: String,
        employeeName: String,
        department: String,
        category: String,
        supervisor: String,
        sk: Int,
        sk_ot: Int,
        ssk: Int,
        ssk_ot: Int,
        usk: Int,
        usk_ot: Int,
        attendance: String,
    ) {
        val fixedRow = TableRow(this)
        val scrollableRow = TableRow(this)

        // Create and add TextViews to the TableRow
        fixedRow.addView(createTextView(dateOfJoin))
        fixedRow.addView(createTextView(zone))
        scrollableRow.addView(createTextView(employeeCode))
        scrollableRow.addView(createTextView(employeeName))
        scrollableRow.addView(createTextView(department))
        scrollableRow.addView(createTextView(category))
        scrollableRow.addView(createTextView(supervisor))

        scrollableRow.addView(createTextView(sk?.toString() ?: "")) // For SK
        scrollableRow.addView(createTextView(sk_ot?.toString() ?: "")) // For OT
        scrollableRow.addView(createTextView(ssk?.toString() ?: "")) // For SSK
        scrollableRow.addView(createTextView(ssk_ot?.toString() ?: "")) // For OT
        scrollableRow.addView(createTextView(usk?.toString() ?: "")) // For USK
        scrollableRow.addView(createTextView(usk_ot?.toString() ?: "")) // For OT
        scrollableRow.addView(createTextView(attendance)) // For ATTND

        // Declare the icons and provide an empty click listener initially
//        val editIcon = createImageView(R.drawable.edit, View.OnClickListener { })
//        val saveIcon = createImageView(R.drawable.save, View.OnClickListener { })

//        // Initially, only the edit icon is visible
//        saveIcon.visibility = View.GONE
//
//        // Action when the edit icon is clicked
//        editIcon.setOnClickListener { v ->
//            // Create and display a confirmation dialog
//            AlertDialog.Builder(this).apply {
//                setTitle("Edit Confirmation")
//                setMessage("Do you want to edit the record?")
//
//                // Set the "Yes" button action
//                setPositiveButton("Yes") { dialog, which ->
//                    // Hide the edit icon and show the save icon
//                    editIcon.visibility = View.GONE
//                    saveIcon.visibility = View.VISIBLE

                    // Replace TextViews with EditTexts to enable editing
                    replaceTextViewWithEditText(scrollableRow, 0, employeeCode) // Employee Code
                    replaceTextViewWithEditText(scrollableRow, 1, employeeName) // Employee Name
                    replaceTextViewWithEditText(scrollableRow, 2, department) // Department
                    replaceTextViewWithEditText(scrollableRow, 3, category) // Category
                    replaceTextViewWithEditText(scrollableRow, 4, supervisor) // Supervisor
                    replaceTextViewWithEditText(scrollableRow, 5, sk?.toString() ?: "") // For SK
                    replaceTextViewWithEditText(scrollableRow, 6, sk_ot?.toString() ?: "") // For OT
                    replaceTextViewWithEditText(scrollableRow, 7, ssk?.toString() ?: "") // For SSK
                    replaceTextViewWithEditText(scrollableRow, 8, ssk_ot?.toString() ?: "") // For OT
                    replaceTextViewWithEditText(scrollableRow, 9, usk?.toString() ?: "") // For USK
                    replaceTextViewWithEditText(scrollableRow, 10, usk_ot?.toString() ?: "") // For OT
                    replaceTextViewWithEditText(scrollableRow, 11, attendance) // For ATTND

                    // Perform any additional logic for editing the row
                    editRow(fixedRow, scrollableRow) // Your custom edit logic here
                }

                // Set the "No" button action
//                setNegativeButton("No") { dialog, which ->
//                    // Do nothing and dismiss the dialog
//                    dialog.dismiss()
//                }
//
//                // Show the dialog
//                show()
//            }
//        }
//
//        // Action when the save icon is clicked
//        saveIcon.setOnClickListener { v ->
//            // Hide the save icon and show the edit icon
//            saveIcon.visibility = View.GONE
//            editIcon.visibility = View.VISIBLE
//
//            // Gather the updated data from EditTexts
//            val updatedEmployeeCode = getEditTextValue(scrollableRow, 0)
//            val updatedEmployeeName = getEditTextValue(scrollableRow, 1)
//            val updatedDepartment = getEditTextValue(scrollableRow, 2)
//            val updatedCategory = getEditTextValue(scrollableRow, 3)
//            val updatedSupervisor = getEditTextValue(scrollableRow, 4)
//            val updatedSk = getEditTextValue(scrollableRow, 5)
//            val updatedSkOt = getEditTextValue(scrollableRow, 6)
//            val updatedSsk = getEditTextValue(scrollableRow, 7)
//            val updatedSskOt = getEditTextValue(scrollableRow, 8)
//            val updatedUsk = getEditTextValue(scrollableRow, 9)
//            val updatedUskOt = getEditTextValue(scrollableRow, 10)
//            val updatedAttendance = getEditTextValue(scrollableRow, 11)
//
//            // Ensure that you have the necessary date and zone values
//            val dateOfWork = dojInput.text.toString()
//            val zone = zoneInput.text.toString()
//
//                // Save the updated data to the backend
//                saveUpdatedDataToBackend(
//                    dateOfWork,
//                    zone,
//                    updatedEmployeeCode,
//                    updatedEmployeeName,
//                    updatedDepartment,
//                    updatedCategory,
//                    updatedSupervisor,
//                    updatedSk,
//                    updatedSkOt,
//                    updatedSsk,
//                    updatedSskOt,
//                    updatedUsk,
//                    updatedUskOt,
//                    updatedAttendance
//                )
//
//            // Replace EditTexts with TextViews to make the row non-editable
//            replaceEditTextWithTextView(scrollableRow, 0, updatedEmployeeCode)
//            replaceEditTextWithTextView(scrollableRow, 1, updatedEmployeeName)
//            replaceEditTextWithTextView(scrollableRow, 2, updatedDepartment)
//            replaceEditTextWithTextView(scrollableRow, 3, updatedCategory)
//            replaceEditTextWithTextView(scrollableRow, 4, updatedSupervisor)
//            replaceEditTextWithTextView(scrollableRow, 5, updatedSk)
//            replaceEditTextWithTextView(scrollableRow, 6, updatedSkOt)
//            replaceEditTextWithTextView(scrollableRow, 7, updatedSsk)
//            replaceEditTextWithTextView(scrollableRow, 8, updatedSskOt)
//            replaceEditTextWithTextView(scrollableRow, 9, updatedUsk)
//            replaceEditTextWithTextView(scrollableRow, 10, updatedUskOt)
//            replaceEditTextWithTextView(scrollableRow, 11, updatedAttendance)
//        }
//
//
//        // Add the icons to the action layout
//        val actionLayout = LinearLayout(this)
//        actionLayout.orientation = LinearLayout.HORIZONTAL
//        actionLayout.gravity = Gravity.CENTER
//        actionLayout.addView(editIcon)
//        actionLayout.addView(saveIcon)
//
//        // Add the action layout to the row
//        scrollableRow.addView(actionLayout)
//
//        // Add the TableRow to the TableLayout
//        fixedTable.addView(fixedRow)
//        scrollableTable.addView(scrollableRow)
//
//        // Add employee code to the set of existing codes
//        existingCode.add(employeeCode)
//    }


    // Helper function to get the value from EditText
    private fun getEditTextValue(row: TableRow, index: Int): String {
        val editText = row.getChildAt(index) as EditText
        return editText.text.toString().trim()
    }

    // Function to save the updated data to the backend
    private fun saveUpdatedDataToBackend(
        dateOfWork: String, // Pass the selected date from the UI
        zone: String, // Pass the selected zone from the UI
        employeeCode: String, employeeName: String, department: String, category: String,
        supervisor: String, sk: String, skOt: String, ssk: String, sskOt: String,
        usk: String, uskOt: String, attendance: String
    ) {
        val accessToken = SharedPreferencesHelper.getAccessToken(this)

        if (accessToken.isNullOrEmpty()) {
            // Handle token not found
            Log.e("Error", "Access token not found")
            return
        }

//        val employeeData = EmployeeData(
//            date_of_work = dateOfWork,  // Use the dynamically selected date
//            zone = zone,  // Use the dynamically selected zone
//            employee_code = employeeCode,
//            employee_name = employeeName,
//            department = department,
//            category = category,
//            supervisor_name = supervisor,
//            sk = sk.toInt(),
//            sk_ot = skOt.toInt(),
//            ssk = ssk.toInt(),
//            ssk_ot = sskOt.toInt(),
//            usk = usk.toInt(),
//            usk_ot = uskOt.toInt(),
//            attendance = attendance
//        )

//        lifecycleScope.launch {
//            try {
//                val response = attendanceApiService.updateAttendance(
//                    token = "Bearer $accessToken",
//                    employeeCode = employeeCode,
//                    date = employeeData.date_of_work,
//                    attendanceData = employeeData
//                )
//
//                if (response.isSuccessful) {
//                    Log.d("Success", "Attendance updated successfully")
//                    Toast.makeText(this@UpdateTimesheetActivity, "Data saved successfully", Toast.LENGTH_SHORT).show()
//                } else {
//                    Log.e("Error", "Failed to update attendance: ${response.errorBody()?.string()}")
//                }
//            } catch (e: Exception) {
//                Log.e("Error", "Error occurred while updating attendance", e)
//            }
//        }
    }


    // Utility function to get the current date (modify as needed)
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    // Utility function to get the current zone (modify as needed)
    private fun getCurrentZone(): String {
        // Implement logic to return the current zone
        return "SomeZone" // Placeholder for actual logic
    }


    //adding more table rows
    private fun createTextView(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT)
        textView.setPadding(15, 15, 15, 15)
        textView.setBackgroundResource(R.drawable.table_column)
        textView.setTextColor(resources.getColor(R.color.white))
        textView.textSize = 16f
        return textView
    }

    private fun replaceTextViewWithEditText(row: TableRow, index: Int, text: String) {
        val textView = row.getChildAt(index) as TextView
        val editText = createEditText()
        editText.setText(text)
        row.removeViewAt(index)
        editText.layoutParams = TableRow.LayoutParams(150,TableRow.LayoutParams.WRAP_CONTENT)
        editText.setPadding(15, 15, 15, 15)
        editText.setBackgroundResource(R.drawable.table_column)
        editText.setTextColor(resources.getColor(R.color.white))
        editText.textSize = 16f
        editText.maxLines = 1 // Ensure the EditText does not expand vertically
        editText.inputType = InputType.TYPE_CLASS_TEXT
        row.addView(editText, index)
    }

    private fun replaceEditTextWithTextView(row: TableRow, index: Int, newText: String) {
        val editText = row.getChildAt(index) as? EditText
        if (editText != null) {
            val textView = TextView(this)
            textView.text = newText
            textView.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT)
            textView.setPadding(15, 15, 15, 15)
            textView.layoutParams = editText.layoutParams // Copy the layout params from the EditText
            textView.setBackgroundResource(R.drawable.table_column)
            textView.setTextColor(resources.getColor(R.color.white))
            row.removeViewAt(index) // Remove the EditText
            textView.textSize = 16f
            row.addView(textView, index) // Add the TextView at the same position
        }
    }


    // Function to create an EditText for editable columns (SK to ATTND)
    private fun createEditText(): EditText {
        val editText = EditText(this)
        // textView.text = text
        editText.layoutParams = TableRow.LayoutParams(150,TableRow.LayoutParams.WRAP_CONTENT)
        editText.setPadding(15, 15, 15, 15)
        editText.setBackgroundResource(R.drawable.table_column)
        editText.setTextColor(resources.getColor(R.color.white))
        editText.textSize = 16f
        editText.maxLines = 1 // Ensure the EditText does not expand vertically
        editText.inputType = InputType.TYPE_CLASS_TEXT // Set input type to prevent multi-line input
        return editText
    }

    //to add action icons in action column
    private fun createImageView(
        @DrawableRes drawableId: Int,
        onClickListener: View.OnClickListener
    ): ImageView {
        val imageView = ImageView(this).apply {
            setImageResource(drawableId)

            // Adjust layout parameters to ensure the image is centered and spaced
            layoutParams = TableRow.LayoutParams(60, 60).apply {
                gravity = Gravity.CENTER // Center the icon vertically and horizontally
                setMargins(10, 0, 10, 0) // Set margins to create space between the icons (left, top, right, bottom)
            }

            // Set the click listener
            setOnClickListener(onClickListener)
        }

        return imageView
    }


    //to edit rows in table
    private fun editRow(fixedRow: TableRow, scrollableRow: TableRow) {
        // Set the row references to the rows being edited
        editingFixedRow = fixedRow
        editingScrollableRow = scrollableRow

        // Populate the form fields with the row's data
        val dojView = fixedRow.getChildAt(0) as? TextView
        val zoneView = fixedRow.getChildAt(1) as? TextView
        val empCodeView = scrollableRow.getChildAt(0) as? TextView
        val empNameView = scrollableRow.getChildAt(1) as? TextView
        val deptView = scrollableRow.getChildAt(2) as? TextView
        val ctgrView = scrollableRow.getChildAt(3) as? TextView
        val sprView = scrollableRow.getChildAt(4) as? TextView

        // Ensure views are not null before populating the form
        dojInput.setText(dojView?.text ?: "")
        zoneInput.setText(zoneView?.text ?: "")
        empCodeInput.setText(empCodeView?.text ?: "")
        empNameInput.setText(empNameView?.text ?: "")
        deptInput.setText(deptView?.text ?: "")
        ctgrInput.setText(ctgrView?.text ?: "")
        sprInput.setText(sprView?.text ?: "")

        // Show the update button (if hidden)

    }

    // Function to collect all table data including fixed and scrollable rows
//    private fun collectTableData(): List<EmployeeData> {
//        val employeeDataList = mutableListOf<EmployeeData>()
//
//        // Use UTC timezone for consistent date formatting
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        dateFormat.timeZone = TimeZone.getTimeZone("UTC") // Set the time zone to UTC
//
//        // Assuming `zone` is set from the main form
//        val zone = zoneInput.text.toString().trim()
//
//        if (zone.isEmpty()) {
//            Toast.makeText(this, "Zone cannot be empty!", Toast.LENGTH_SHORT).show()
//            return employeeDataList
//        }
//
//        // Start from the second row (index 1) to skip the header row
//        for (i in 1 until scrollableTable.childCount) {
//            val scrollableRow = scrollableTable.getChildAt(i) as TableRow
//
//            val employeeCode = (scrollableRow.getChildAt(0) as? TextView)?.text.toString().trim()
//            Log.d("EmployeeCode", "Retrieved Employee Code: '$employeeCode'")
//
//            val employeeName = (scrollableRow.getChildAt(1) as? TextView)?.text.toString().trim()
//            val department = (scrollableRow.getChildAt(2) as? TextView)?.text.toString().trim()
//            val category = (scrollableRow.getChildAt(3) as? TextView)?.text.toString().trim()
//            val supervisor = (scrollableRow.getChildAt(4) as? TextView)?.text.toString().trim()
//
//            val sk = (scrollableRow.getChildAt(5) as? EditText)?.text.toString().toIntOrNull() ?: 0
//            val ot1 = (scrollableRow.getChildAt(6) as? EditText)?.text.toString().toIntOrNull() ?: 0
//            val ssk = (scrollableRow.getChildAt(7) as? EditText)?.text.toString().toIntOrNull() ?: 0
//            val ot2 = (scrollableRow.getChildAt(8) as? EditText)?.text.toString().toIntOrNull() ?: 0
//            val usk = (scrollableRow.getChildAt(9) as? EditText)?.text.toString().toIntOrNull() ?: 0
//            val ot3 = (scrollableRow.getChildAt(10) as? EditText)?.text.toString().toIntOrNull() ?: 0
//            val attnd = (scrollableRow.getChildAt(11) as? EditText)?.text.toString().trim()
//
//            // Get the current date in UTC format
//            val formattedDate = dateFormat.format(Date())
//
//            // Validate Employee Code
//            val validEmployeeCode = if (employeeCode.length <= 10 && employeeCode.all { it.isLetterOrDigit() }) {
//                employeeCode
//            } else {
//                "" // Set empty if validation fails
//            }
//
//            if (validEmployeeCode.isNotEmpty()) {
//                val employeeData = EmployeeData(
//                    date_of_work = formattedDate,
//                    zone = zone,
//                    employee_code = validEmployeeCode,
//                    employee_name = employeeName,
//                    department = department,
//                    category = category,
//                    supervisor_name = supervisor,
//                    sk = sk,
//                    sk_ot = ot1,
//                    ssk = ssk,
//                    ssk_ot = ot2,
//                    usk = usk,
//                    usk_ot = ot3,
//                    attendance = attnd
//                )
//
//                employeeDataList.add(employeeData)
//            } else {
//                Toast.makeText(this, "Invalid Employee Code: $employeeCode", Toast.LENGTH_SHORT).show()
//            }
//        }
//        return employeeDataList
//    }

//    private fun updateTableRow(employeeCode: String, date: String, employeeData: EmployeeData) {
//        // Find the index of the row to update based on employeeCode and date
//        for (i in 0 until scrollableTable.childCount) {
//            val row = scrollableTable.getChildAt(i) as TableRow
//            val empCodeView = row.getChildAt(0) as TextView
//            val dateView = (fixedTable.getChildAt(i) as TableRow).getChildAt(0) as TextView
//
//            // Check if both employeeCode and date match
//            if (empCodeView.text.toString() == employeeCode && dateView.text.toString() == date) {
//                // Only update the SK, SK_OT, SSK, SSK_OT, USK, USK_OT, and attendance fields in the scrollable part
//                (row).apply {
//                    // Assuming the positions of the SK, SK_OT, SSK, SSK_OT, USK, USK_OT, and attendance fields
//                    (getChildAt(5) as TextView).text = employeeData.sk.toString()
//                    (getChildAt(6) as TextView).text = employeeData.sk_ot.toString()
//                    (getChildAt(7) as TextView).text = employeeData.ssk.toString()
//                    (getChildAt(8) as TextView).text = employeeData.ssk_ot.toString()
//                    (getChildAt(9) as TextView).text = employeeData.usk.toString()
//                    (getChildAt(10) as TextView).text = employeeData.usk_ot.toString()
//                    (getChildAt(11) as TextView).text = employeeData.attendance
//                }
//
//                // No need to continue once the correct row has been updated
//                break
//            }
//        }
//    }




    // Function to save employee data using the provided access token

    private fun findParentTableRow(view: View): TableRow? {
        var parent = view.parent
        while (parent != null && parent !is TableRow) {
            parent = parent.parent
        }
        Log.d("UpdateTimeSheetActivity", "Found TableRow: $parent")
        return parent as? TableRow
    }
    private fun hideTable() {
        Log.d("FormActivity", "Attempting to hide CardView")
        cardView2.visibility = View.GONE
        Log.d("FormActivity", "CardView visibility set to GONE")
    }

    //table data will be visible
    private fun showCardView() {
        cardView2.visibility = View.VISIBLE
    }

    private fun selectRow(row: TableRow) {
        // Get the index of the selected row
        selectedRowIndex = scrollableTable.indexOfChild(row)

        // Clear previous highlights
        for (i in 0 until scrollableTable.childCount) {
            val childRow = scrollableTable.getChildAt(i) as TableRow
            childRow.setBackgroundColor(Color.TRANSPARENT) // Reset background color
        }

        // Highlight the selected row
        row.setBackgroundColor(Color.BLUE) // Highlight the selected row
    }
    // Function to show confirmation dialog
    private fun showEditConfirmationDialog(onConfirmed: (Boolean) -> Unit) {
        // Create an AlertDialog Builder
        val builder = AlertDialog.Builder(this)

        // Set dialog properties
        builder.setTitle("Confirm Edit")
            .setMessage("Are you sure you want to edit this record?")
            .setPositiveButton("Yes") { dialog, _ ->
                onConfirmed(true) // User confirmed
                dialog.dismiss() // Dismiss the dialog
            }
            .setNegativeButton("No") { dialog, _ ->
                onConfirmed(false) // User did not confirm
                dialog.dismiss() // Dismiss the dialog
            }

        // Create and show the dialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun resetForm(){
        zoneInput.text?.clear()
        empCodeInput.text?.clear()
        empNameInput.text?.clear()
        deptInput.text?.clear()
        ctgrInput.text?.clear()
        sprInput.text?.clear()
    }
}