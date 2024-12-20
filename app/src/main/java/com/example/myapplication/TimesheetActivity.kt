package com.example.myapplication

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.backend_api.AuthRepository
import com.example.myapplication.backend_api.AuthViewModel
import com.example.myapplication.backend_api.AuthViewModelFactory
import com.example.myapplication.backend_api.RetrofitClient
import com.example.myapplication.backend_api.SharedPreferencesHelper
import com.example.myapplication.interface_api.EmployeeData
import com.example.myapplication.interface_api.EmployeeDataRequest
import com.example.myapplication.interface_api.EmployeeSearchRequest
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TimesheetActivity : AppCompatActivity() {

    private lateinit var fixedTable: TableLayout
    private lateinit var scrollableTable: TableLayout
    private lateinit var fixedColumnTable: TableLayout
    private lateinit var scrollableColumnTable: TableLayout
    private lateinit var cardView1: LinearLayout
    private lateinit var dojLayout: TextInputLayout
    private lateinit var zoneLayout: TextInputLayout
    private lateinit var empCodeLayout: TextInputLayout
    private lateinit var empNameLayout: TextInputLayout
    private lateinit var deptLayout: TextInputLayout
    private lateinit var ctgrLayout: TextInputLayout
    private lateinit var sprLayout: TextInputLayout
    private lateinit var shiftLayout: TextInputLayout
    private lateinit var dojInput: TextInputEditText
    private lateinit var zoneInput: AutoCompleteTextView
    private lateinit var empCodeInput: TextInputEditText
    private lateinit var empNameInput: TextInputEditText
    private lateinit var deptInput: TextInputEditText
    private lateinit var ctgrInput: TextInputEditText
    private lateinit var sprInput: TextInputEditText
    private lateinit var shiftInput: AutoCompleteTextView
    private lateinit var saveBtn: Button
    private lateinit var serchBtn: Button
    private lateinit var backButton: Button
    private lateinit var toolbar_text: TextView
    private lateinit var cardView2: CardView
    private lateinit var saveProgressBar: ProgressBar
    private lateinit var searchProgressBar: ProgressBar


    @SuppressLint("MissingInflatedId", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        setContentView(R.layout.activity_timesheet)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.button)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        cardView1 = findViewById(R.id.cardView1)
        dojLayout = findViewById(R.id.dateOfJoin)
        zoneLayout = findViewById(R.id.zone)
        empCodeLayout = findViewById(R.id.employeeCode)
        empNameLayout = findViewById(R.id.employeeName)
        deptLayout = findViewById(R.id.department)
        ctgrLayout = findViewById(R.id.Category)
        sprLayout = findViewById(R.id.supervisor)
        shiftLayout = findViewById(R.id.shift)
        dojInput = findViewById(R.id.dateOfJoin2)
        zoneInput = findViewById(R.id.zone2)
        empCodeInput = findViewById(R.id.employeeCode2)
        empNameInput = findViewById(R.id.employeeName2)
        deptInput = findViewById(R.id.department2)
        ctgrInput = findViewById(R.id.Category2)
        sprInput = findViewById(R.id.supervisor2)
        shiftInput = findViewById(R.id.shift2)
        saveBtn = findViewById(R.id.save)
        serchBtn = findViewById(R.id.search)
        backButton = findViewById(R.id.backButton)
        toolbar_text = findViewById(R.id.toolbar_text)
        cardView2 = findViewById(R.id.cardView2)
        fixedTable = findViewById(R.id.fixedTable)
        scrollableTable = findViewById(R.id.scrollableTable)
        saveProgressBar = findViewById(R.id.saveProgressBar)
        searchProgressBar = findViewById(R.id.searchProgressBar)
        fixedColumnTable = findViewById(R.id.fixedColumnTable)
        scrollableColumnTable = findViewById(R.id.scrollableColumnTable)


        // Initialize the Calendar and SimpleDateFormat
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Set the default date to today's date in the dojInput
        val currentDate = dateFormat.format(calendar.time)
        dojInput.setText(currentDate) // Display today's date by default

        // Disable direct editing of the input field
        dojInput.isFocusable = false
        dojInput.isFocusableInTouchMode = false

        // Set up the onClickListener to open the DatePickerDialog
        dojInput.setOnClickListener {
            val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                // Update the calendar with the selected date
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Format and display the selected date
                val selectedDate = dateFormat.format(calendar.time)
                Log.d("SelectedDate", "Date selected: $selectedDate") // Log the selected date
                dojInput.setText(selectedDate)
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

        // Define the dropdown items for shift
        val shift_items = listOf("A", "B", "C", "G")

        // Create an ArrayAdapter with your list of items
        val shift_adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, shift_items)

        // Attach the adapter to the AutoCompleteTextView
        shiftInput.setAdapter(shift_adapter)

        // Set the dropdown to automatically show when the user clicks inside the field
        shiftInput.setOnClickListener {
            shiftInput.showDropDown()
        }

        // Handle item selection from the dropdown menu
        shiftInput.setOnItemClickListener { parent, _, position, _ ->
            val itemSelected = parent.getItemAtPosition(position) as String
            Toast.makeText(this, "Selected Shift: $itemSelected", Toast.LENGTH_SHORT).show()

            Log.d("ShiftSelection", "Selected Shift: $itemSelected")

        }


        serchBtn.setOnClickListener {

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

            // Retrieve the admin status
            val isAdmin = SharedPreferencesHelper.getIsAdmin(this)

            // Get the search criteria from the input fields
            val employeeCode = empCodeInput.text.toString()
            val employeeName = empNameInput.text.toString()
            val zone = zoneInput.text.toString()
            val category = ctgrInput.text.toString()
            val supervisorName = sprInput.text.toString()

            // Check if all input fields are empty (for admin user, allow empty fields, for non-admin, supervisor name should be mandatory)
            if (isAdmin) {
                // If is_admin is true, all fields can be used for search
                if (employeeCode.isEmpty() && employeeName.isEmpty() && zone.isEmpty() && category.isEmpty() && supervisorName.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Please enter at least one search criterion.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }
            } else {
                // If is_admin is false, only supervisor field should be non-empty
                if (supervisorName.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Please enter a supervisor name to search.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }
            }

            // Call the API to search employee data
            searchEmployeeData(
                accessToken,
                employeeCode = if (employeeCode.isNotEmpty()) employeeCode else null,
                employeeName = if (employeeName.isNotEmpty()) employeeName else null,
                zone = if (zone.isNotEmpty()) zone else null,
                category = if (category.isNotEmpty()) category else null,
                supervisorName = if (supervisorName.isNotEmpty()) supervisorName else null,
                isAdmin = isAdmin
            )
        }

        saveBtn.setOnClickListener {
        saveProgressBar.visibility = View.VISIBLE
            // Collect all rows data
            val employeeDataList = collectTableData()

            if (employeeDataList.isNotEmpty()) {
                // Call the API to save the data
                saveEmployeeData(employeeDataList)
            } else {
                Toast.makeText(this, "No data to save.", Toast.LENGTH_SHORT).show()
                saveProgressBar.visibility = View.GONE

            }
//            updateRowData()
        }

        backButton.setOnClickListener {
            showFormAndClearTable()
        }

        // Retrieve the supervisor name passed via Intent
        val supervisorName = intent.getStringExtra("SUPERVISOR_NAME")

        // Retrieve the admin status from SharedPreferences or Intent
        val isAdmin = SharedPreferencesHelper.getIsAdmin(this)  // or get the value directly from Intent

        // Check if the user is an admin or not
        if (isAdmin) {
            // If is_admin is true, clear the field and make it editable
            sprInput.text = Editable.Factory.getInstance().newEditable("") // Ensure the field is cleared
            sprInput.isFocusableInTouchMode = true
            sprInput.isEnabled = true
        } else {
            // If is_admin is false, set the supervisor name and make it non-editable
            supervisorName?.let {
                sprInput.text = Editable.Factory.getInstance().newEditable(it)
            }
            sprInput.isFocusableInTouchMode = false
            sprInput.isEnabled = false
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

    // Function to search for employee data based on various criteria
    private fun searchEmployeeData(
        accessToken: String,
        employeeCode: String? = null,
        employeeName: String? = null,
        zone: String? = null,
        category: String? = null,
        supervisorName: String? = null,
        isAdmin: Boolean
    ) {
        // Hide the form layout and show the progress bar during the search
        hideForm() // Hide form initially
        searchProgressBar.visibility = View.VISIBLE // Show progress bar

        // Create an instance of your API service
        val apiService = RetrofitClient.getEmployeeSearchApi()

        // If not admin, only pass supervisor name
        val searchRequest = if (isAdmin) {
            EmployeeSearchRequest(
                employee_code = employeeCode,
                employee_name = employeeName,
                zone = zone,
                category = category,
                supervisor_name = supervisorName
            )
        } else {
            EmployeeSearchRequest(
                employee_code = null,
                employee_name = null,
                zone = null,
                category = null,
                supervisor_name = supervisorName // Only use supervisor_name for non-admin
            )
        }

        // Call the API to search for employee data
        apiService.searchEmployeeData("Bearer $accessToken", searchRequest)
            .enqueue(object : Callback<List<EmployeeDataRequest>> {
                override fun onResponse(
                    call: Call<List<EmployeeDataRequest>>,
                    response: Response<List<EmployeeDataRequest>>
                ) {
                    searchProgressBar.visibility = View.GONE // Hide progress bar after response

                    if (response.isSuccessful) {
                        val employeeDataList = response.body()

                        if (!employeeDataList.isNullOrEmpty()) {

                            // Pass the list directly to addDataToTable
                            addDataToTable(employeeList = employeeDataList)

                            // Show the table view and hide the form view
                            showCardView()
                            Toast.makeText(this@TimesheetActivity, "Employee data retrieved successfully.", Toast.LENGTH_SHORT).show()

                        } else {
                            // If no data is found, show a message and redisplay the form
                            hideTable()
                            showForm()
                            Toast.makeText(this@TimesheetActivity, "No employee found with the given criteria.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Handle API error and show the form again
                        showForm() // Show the form layout
                        Toast.makeText(
                            this@TimesheetActivity,
                            "Error fetching employee data: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<EmployeeDataRequest>>, t: Throwable) {
                    searchProgressBar.visibility = View.GONE // Hide progress bar on failure
                    showForm() // Show the form layout on network failure
                    Toast.makeText(
                        this@TimesheetActivity,
                        "Network error: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


    // Helper functions to toggle visibility
    private fun hideForm() {
        cardView1.visibility = View.GONE

    }

    private fun showForm() {
        cardView1.visibility = View.VISIBLE

    }

    private fun addDataToTable(employeeList: List<EmployeeDataRequest>?) {

        val sortedEmployeeList = employeeList?.sortedBy { it.employee_name }

        sortedEmployeeList?.forEach { employee ->
            val fixedRow = TableRow(this)
            val scrollableRow = TableRow(this)

            // Create and add TextViews for fixed fields to the TableRow
            val dateTextView = createTextView(employee.date) // Create Employee Code TextView
            dateTextView.visibility = View.GONE // Hide Employee Code column
            fixedRow.addView(dateTextView)

            val zoneView = createTextView(employee.zone) // Create Employee Code TextView
            zoneView.visibility = View.GONE // Hide Employee Code column
            fixedRow.addView(zoneView)

            // Hide Employee Code and Employee Name columns (fixed columns)
            val employeeCodeTextView = createTextView(employee.employee_code) // Create Employee Code TextView
            employeeCodeTextView.visibility = View.GONE // Hide Employee Code column
            fixedRow.addView(employeeCodeTextView)

            val employeeNameTextView = createTextView(employee.employee_name) // Create Employee Name TextView
            fixedRow.addView(employeeNameTextView)

            // Add TextViews for other non-editable fields like department, category, supervisor
            val departmentTextView = createTextView(employee.department)
            departmentTextView.visibility = View.GONE // Hide Department column
            scrollableRow.addView(departmentTextView)

            val categoryTextView = createTextView(employee.category)
            categoryTextView.visibility = View.GONE // Hide Category column
            scrollableRow.addView(categoryTextView)

            val supervisorTextView = createTextView(employee.supervisor_name)
            supervisorTextView.visibility = View.GONE // Hide Supervisor column
            scrollableRow.addView(supervisorTextView)


            // Get the selected shift value from the AutoCompleteTextView
            val selectedShift = shiftInput.text.toString()

            // Inside the addDataToTable method
            val shiftSpinner = Spinner(this)

            // Define the shift options
            val shiftOptions = listOf("A", "B", "C", "G")

            // Create an ArrayAdapter to bind the shift options to the Spinner
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, shiftOptions)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            shiftSpinner.adapter = adapter

            // Set the background for the Spinner
            shiftSpinner.setBackgroundResource(R.drawable.table_column) // Set a custom drawable as background

            // Optionally, you can set other properties like padding or layout
            shiftSpinner.setPadding(10, 10, 10, 10) // Example padding (optional)

            // Set the height to 120px
            val layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, // Width can be MATCH_PARENT or any other suitable value
                120 // Height set to 120px
            )
            shiftSpinner.layoutParams = layoutParams

            shiftSpinner.gravity = Gravity.CENTER

            // Set the selected item in the Spinner based on the shift selected in the form
            val initialPosition = shiftOptions.indexOf(selectedShift)
            if (initialPosition >= 0) {
                shiftSpinner.setSelection(initialPosition)
            }

            // Set OnItemSelectedListener
            shiftSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedShiftInTable = parentView?.getItemAtPosition(position) as String
                    Log.d("Spinner", "Selected Shift in Table: $selectedShiftInTable")
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {
                    Log.d("Spinner", "No item selected in Table Spinner.")
                }
            }

            // Add the Spinner to the scrollableRow
            scrollableRow.addView(shiftSpinner)



            // Replace createEditText for SK, SSK, and USK with Spinners

            val skSpinner = createSpinner(listOf("select","1", "0"))
            scrollableRow.addView(skSpinner)

            val skOtEditText = createEditText().apply { gravity = Gravity.CENTER } // Keep OT as EditText
            scrollableRow.addView(skOtEditText)

            val sskSpinner = createSpinner(listOf("select ","1", "0"))
            scrollableRow.addView(sskSpinner)

            val sskOtEditText = createEditText().apply { gravity = Gravity.CENTER }
            scrollableRow.addView(sskOtEditText)

            val uskSpinner = createSpinner(listOf("select","1", "0"))
            scrollableRow.addView(uskSpinner)

            val uskOtEditText = createEditText().apply { gravity = Gravity.CENTER }
            scrollableRow.addView(uskOtEditText)

            // Add CheckBox for ATTND column
            val attendanceCheckBox = CheckBox(this)
            attendanceCheckBox.setBackgroundResource(R.drawable.checkbox_selector)
            // Set height for the CheckBox similar to other fields (e.g., 120px)
            val checkboxParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, // Width based on the content (CheckBox's size)
                120 // Set height to match the TableRow height
            )


            attendanceCheckBox.layoutParams = checkboxParams

            attendanceCheckBox.gravity = Gravity.CENTER


            scrollableRow.addView(attendanceCheckBox)

            // Update checkbox state based on Spinner values
            val updateAttendanceCheckBox = {
                val skValue = skSpinner.selectedItem.toString()
                val sskValue = sskSpinner.selectedItem.toString()
                val uskValue = uskSpinner.selectedItem.toString()
                attendanceCheckBox.isChecked = skValue == "1" || sskValue == "1" || uskValue == "1"
            }

            // Set initial checkbox state
            updateAttendanceCheckBox()

            // Set listeners for Spinners to update checkbox when values change
            skSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    updateAttendanceCheckBox()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            sskSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    updateAttendanceCheckBox()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            uskSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    updateAttendanceCheckBox()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            // Add the TableRow to the TableLayout
            fixedTable.addView(fixedRow)
            scrollableTable.addView(scrollableRow)
        }
    }

    private fun createSpinner(options: List<String>): Spinner {
        val spinner = Spinner(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.setBackgroundResource(R.drawable.table_column) // Set a custom drawable as background

        val layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT, // Width can be MATCH_PARENT or WRAP_CONTENT
            120 // Height set to 120px
        )
        spinner.layoutParams = layoutParams
        spinner.gravity = Gravity.CENTER

        return spinner
    }


    // Function to create a TextView for table cells with fixed height
    private fun createTextView(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.setPadding(15, 15, 15, 15)
        textView.setBackgroundResource(R.drawable.table_column)
        textView.setTextColor(resources.getColor(R.color.black)) // Adjust color as needed
        textView.textSize = 16f

        // Set the height of the TextView to match the EditText height
        val height = 120 // Same height as in createEditText function
        val params = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT, // Width can be match_parent
            height // Set the height here
        )
        textView.layoutParams = params

        return textView
    }

    private fun createEditText(): EditText {
        val editText = EditText(this)
        editText.layoutParams = TableRow.LayoutParams(
            150, // Set width here if needed
            120 // Set the height here, e.g., 120 pixels
        )
        editText.setPadding(15, 15, 15, 15)
        editText.setBackgroundResource(R.drawable.table_column)
        editText.setTextColor(resources.getColor(R.color.black))
        editText.textSize = 16f
        editText.maxLines = 1
        editText.inputType = InputType.TYPE_CLASS_NUMBER

        return editText
    }

    // Function to collect all table data including fixed and scrollable rows
    private fun collectTableData(): List<EmployeeData> {
        val employeeDataList = mutableListOf<EmployeeData>()

        // Assuming zone is set from the main form
        val zone = zoneInput.text.toString().trim()

        if (zone.isEmpty()) {
            Toast.makeText(this, "Zone cannot be empty!", Toast.LENGTH_SHORT).show()
            return employeeDataList
        }

        // Loop through both the fixed and scrollable rows
        for (i in 1 until fixedTable.childCount) {  // Start from index 1 to skip the header row
            val fixedRow = fixedTable.getChildAt(i) as TableRow

            // Get fixed column values (Employee Code and Employee Name)
            val employeeCode = (fixedRow.getChildAt(2) as? TextView)?.text.toString().trim()
            Log.d("EmployeeCode", "Retrieved Employee Code: '$employeeCode'")

            val employeeName = (fixedRow.getChildAt(3) as? TextView)?.text.toString().trim()

            fixedRow.getChildAt(0).visibility = View.GONE // Hide department column
            fixedRow.getChildAt(1).visibility = View.GONE // Hide category column
            fixedRow.getChildAt(2).visibility = View.GONE // Hide supervisor column

            // Now handle the corresponding scrollable row
            val scrollableRow = scrollableTable.getChildAt(i) as TableRow

            // Collect scrollable columns: Department, Category, Supervisor, SK, OT, etc.
            val department = (scrollableRow.getChildAt(0) as? TextView)?.text.toString().trim()
            val category = (scrollableRow.getChildAt(1) as? TextView)?.text.toString().trim()
            val supervisor = (scrollableRow.getChildAt(2) as? TextView)?.text.toString().trim()

            scrollableRow.getChildAt(0).visibility = View.GONE // Hide department column
            scrollableRow.getChildAt(1).visibility = View.GONE // Hide category column
            scrollableRow.getChildAt(2).visibility = View.GONE // Hide supervisor column
//            val shift = (scrollableRow.getChildAt(3) as? EditText)?.text.toString().trim()

            val shiftSpinner = scrollableRow.getChildAt(3) as? Spinner
            val shift = shiftSpinner?.selectedItem.toString().trim()

            val skSpinner = scrollableRow.getChildAt(4) as? Spinner
            val sk = skSpinner?.selectedItem.toString().toIntOrNull() ?: 0
            val ot1 = (scrollableRow.getChildAt(5) as? EditText)?.text.toString().toIntOrNull() ?: 0
            val sskSpinner = scrollableRow.getChildAt(6) as? Spinner
            val ssk = sskSpinner?.selectedItem.toString().toIntOrNull() ?: 0
            val ot2 = (scrollableRow.getChildAt(7) as? EditText)?.text.toString().toIntOrNull() ?: 0
            val uskSpinner = scrollableRow.getChildAt(8) as? Spinner
            val usk = uskSpinner?.selectedItem.toString().toIntOrNull() ?: 0
            val ot3 = (scrollableRow.getChildAt(9) as? EditText)?.text.toString().toIntOrNull() ?: 0

            val attendanceCheckbox = scrollableRow.getChildAt(10) as? CheckBox
            val attendance:Boolean = attendanceCheckbox?.isChecked ?: false // Get checkbox state (checked/unchecked)


            // Collect the date input (if available)
            val dateString = dojInput.text.toString().trim()
            Log.d("DateString", "Current date string from input: '$dateString'")

            var formattedDate = ""

            if (dateString.isNotEmpty()) {
                try {
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)
                    formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
                    Log.d("FormattedDate", "Formatted date: '$formattedDate'")
                } catch (e: ParseException) {
                    Log.e("DateError", "Invalid date format: $dateString", e)
                    formattedDate = "Invalid Date"
                }
            } else {
                formattedDate = "Invalid Date"
            }

            // Validate Employee Code
            val validEmployeeCode = if (employeeCode.length <= 10 && employeeCode.all { it.isLetterOrDigit() }) {
                employeeCode
            } else {
                ""
            }

            if (validEmployeeCode.isNotEmpty()) {
                // Create an EmployeeData object for each scrollable row (including the scrollable columns)
                val employeeData = EmployeeData(
                    date_of_work = formattedDate,
                    zone = zone,
                    employee_code = validEmployeeCode,
                    employee_name = employeeName,
                    department = department,
                    category = category,
                    supervisor_name = supervisor,
                    shift = shift,
                    sk = sk,
                    sk_ot = ot1,
                    ssk = ssk,
                    ssk_ot = ot2,
                    usk = usk,
                    usk_ot = ot3,
                    attendance = attendance
                )

                employeeDataList.add(employeeData)
            } else {
                Toast.makeText(this, "Invalid Employee Code: $employeeCode", Toast.LENGTH_SHORT).show()
            }
        }

        return employeeDataList
    }

    private fun saveEmployeeData(employeeDataList: List<EmployeeData>) {
        val accessToken = SharedPreferencesHelper.getAccessToken(this) // Get the access token

        lifecycleScope.launch {
            saveProgressBar.visibility = View.VISIBLE // Show progress bar

            try {
                // Attempt to save employee data with the current access token
                val response = RetrofitClient.timeSheetSaveEmployeeApi.saveEmployeeData("Bearer $accessToken", employeeDataList)

                if (response.isSuccessful) {
                    // Success case
                    Toast.makeText(this@TimesheetActivity, "Data saved successfully!", Toast.LENGTH_SHORT).show()
                    resetForm()
                    // Return to the form and clear the table
                    showFormAndClearTable()

                } else {
                    // Handle error responses
                    val errorResponse = response.errorBody()?.string()

                    // Check if the error contains the specific "attendance already recorded" message
                    if (errorResponse != null && errorResponse.contains("Attendance for this employee has already been recorded for this date.")) {
                        Toast.makeText(this@TimesheetActivity, "Attendance for this employee has already been recorded for this date.", Toast.LENGTH_SHORT).show()
                    } else {
                        // General error message
                        Toast.makeText(this@TimesheetActivity, "Failed to save data: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@TimesheetActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                saveProgressBar.visibility = View.GONE // Hide progress bar
            }
        }
    }

    // Function to return to the form and clear the table data
    private fun showFormAndClearTable() {
        // Show the form layout and hide the table (assuming the form layout is named formLayout and table layout is named tableLayout)
        cardView1.visibility = View.VISIBLE
        cardView2.visibility = View.GONE

        // Clear the TableLayout data but keep the heading row
        clearTableData(fixedTable)   // Clear fixedTable, excluding the heading row
        clearTableData(scrollableTable) // Clear scrollableTable, excluding the heading row
    }

    // Function to clear table data while keeping the header row
    private fun clearTableData(tableLayout: TableLayout) {
        // Only remove rows starting from index 1, to keep the first row (the header)
        for (i in tableLayout.childCount - 1 downTo 1) {
            val row = tableLayout.getChildAt(i)
            if (row is TableRow) {
                tableLayout.removeViewAt(i)
            }
        }
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

    private fun resetForm(){

        zoneInput.text?.clear()
        empCodeInput.text?.clear()
        empNameInput.text?.clear()
        deptInput.text?.clear()
        ctgrInput.text?.clear()
        // Only clear sprInput if the user is an admin
        if (SharedPreferencesHelper.getIsAdmin(this)) {
            sprInput.text?.clear()
        }

    }

    // Method to set the selected shift for all rows in the table
    fun setShiftForAllRows(selectedShift: String) {
        // Loop through all rows (assuming you have a list of rows or a TableLayout reference)
        for (i in 0 until cardView2.childCount) {
            val row = cardView2.getChildAt(i) as TableRow
            val shiftSpinner = row.getChildAt(0) as Spinner  // Assuming the Spinner is the first child of the row

            // Get the position of the selected shift in the adapter
            val position = (shiftSpinner.adapter as ArrayAdapter<String>).getPosition(selectedShift)

            // Set the selected shift in the Spinner
            shiftSpinner.setSelection(position)
        }
    }






}