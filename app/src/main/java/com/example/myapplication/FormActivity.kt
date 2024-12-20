//package com.example.myapplication
//
//import android.annotation.SuppressLint
//import android.app.AlertDialog
//import android.app.DatePickerDialog
//import android.content.ContentValues
//import android.graphics.Color
//import android.os.Bundle
//import android.os.Environment
//import android.provider.MediaStore
//import android.text.SpannableString
//import android.text.style.ForegroundColorSpan
//import android.text.style.RelativeSizeSpan
//import android.util.Log
//import android.view.Gravity
//import android.view.Menu
//import android.view.MenuItem
//import android.view.View
//import android.widget.ArrayAdapter
//import android.widget.AutoCompleteTextView
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.ProgressBar
//import android.widget.TableLayout
//import android.widget.TableRow
//import android.widget.TextView
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.annotation.DrawableRes
//import androidx.appcompat.app.AppCompatActivity
//import androidx.cardview.widget.CardView
//import androidx.core.content.ContextCompat
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.lifecycle.lifecycleScope
//import com.example.myapplication.backend_api.RetrofitClient
//import com.example.myapplication.backend_api.SharedPreferencesHelper
//import com.example.myapplication.interface_api.EmployeeDataRequest
//import com.example.myapplication.interface_api.EmployeeSearchRequest
//import com.google.android.material.textfield.TextInputEditText
//import com.google.android.material.textfield.TextInputLayout
//import com.itextpdf.kernel.pdf.PdfWriter
//import com.itextpdf.layout.Document
//import com.itextpdf.layout.element.Paragraph
//import com.itextpdf.layout.element.Table
//import com.itextpdf.layout.properties.TextAlignment
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import org.apache.poi.ss.usermodel.Workbook
//import org.apache.poi.xssf.usermodel.XSSFSheet
//import org.apache.poi.xssf.usermodel.XSSFWorkbook
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.io.IOException
//import java.net.ProtocolException
//import java.text.SimpleDateFormat
//import java.util.Calendar
//import java.util.Locale
//
//
//class FormActivity : AppCompatActivity() {
//
////    private lateinit var fixedTable: TableLayout
////    private lateinit var scrollableTable: TableLayout
////
////    private lateinit var dojLayout: TextInputLayout
////    private lateinit var zoneLayout: TextInputLayout
////    private lateinit var empCodeLayout: TextInputLayout
////    private lateinit var empNameLayout: TextInputLayout
////    private lateinit var deptLayout: TextInputLayout
////    private lateinit var ctgrLayout: TextInputLayout
////    private lateinit var sprLayout: TextInputLayout
////
////    private lateinit var dojInput: TextInputEditText
////    private lateinit var zoneInput: AutoCompleteTextView
////    private lateinit var empCodeInput: TextInputEditText
////    private lateinit var empNameInput: TextInputEditText
////    private lateinit var deptInput: TextInputEditText
////    private lateinit var ctgrInput: TextInputEditText
////    private lateinit var sprInput: TextInputEditText
////
////    private lateinit var saveBtn: Button
////    private lateinit var serchBtn: Button
////    private lateinit var cardView2: CardView
////
////    private var editingFixedRow: TableRow? = null
////    private var editingScrollableRow: TableRow? = null
////
////    private val existingCode = mutableSetOf<String>()
////
////    private var selectedRowIndex: Int = -1
////    private lateinit var updtButton: Button
////
////    private lateinit var saveProgressBar: ProgressBar
////    private lateinit var searchProgressBar: ProgressBar
////    private lateinit var updateProgressBar: ProgressBar
//
//    @SuppressLint("MissingInflatedId")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_form)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//
//        window.statusBarColor = ContextCompat.getColor(this, R.color.purple)
//
//        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
//
//        dojLayout = findViewById(R.id.dateOfJoin)
//        zoneLayout = findViewById(R.id.zone)
//        empCodeLayout = findViewById(R.id.employeeCode)
//        empNameLayout = findViewById(R.id.employeeName)
//        deptLayout = findViewById(R.id.department)
//        ctgrLayout = findViewById(R.id.Category)
//        sprLayout = findViewById(R.id.supervisor)
//
//        dojInput = findViewById(R.id.dateOfJoin2)
//        zoneInput = findViewById(R.id.zone2)
//        empCodeInput = findViewById(R.id.employeeCode2)
//        empNameInput = findViewById(R.id.employeeName2)
//        deptInput = findViewById(R.id.department2)
//        ctgrInput = findViewById(R.id.Category2)
//        sprInput = findViewById(R.id.supervisor2)
//
//
//        saveBtn = findViewById(R.id.save)
//        serchBtn = findViewById(R.id.search)
//        updtButton = findViewById(R.id.update)
//
//
//        cardView2 = findViewById(R.id.cardView2)
//
//        fixedTable = findViewById(R.id.fixedTable)
//        scrollableTable = findViewById(R.id.scrollableTable)
//
//        saveProgressBar = findViewById(R.id.saveProgressBar)
//        searchProgressBar = findViewById(R.id.searchProgressBar)
//        updateProgressBar = findViewById(R.id.updateProgressBar)
//
//        // Disable keyboard for the EditText
//        dojInput.isFocusable = false
//        dojInput.isFocusableInTouchMode = false
//
//
//        // Set current date
//        val calendar = Calendar.getInstance()
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        dojInput.setText(dateFormat.format(calendar.time))
//
//        dojInput.setOnClickListener {
//            val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
//                calendar.set(Calendar.YEAR, year)
//                calendar.set(Calendar.MONTH, month)
//                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//                dojInput.setText(dateFormat.format(calendar.time))
//            }
//
//            val dateDialog = DatePickerDialog(
//                this,
//                dateListener,
//                calendar.get(Calendar.YEAR),
//                calendar.get(Calendar.MONTH),
//                calendar.get(Calendar.DAY_OF_MONTH)
//            )
//
//            dateDialog.show()
//        }
//
//        // Define the dropdown items
//        val items = listOf( "zone-1", "zone-2", "zone-3", "zone-4")
//
//// Create an ArrayAdapter with your list of items
//        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)
//
//// Attach the adapter to the AutoCompleteTextView
//        zoneInput.setAdapter(adapter)
//
//// Set the dropdown to automatically show when the user clicks inside the field
//        zoneInput.setOnClickListener {
//            zoneInput.showDropDown()
//        }
//
//// Handle item selection from the dropdown menu
//        zoneInput.setOnItemClickListener { parent, _, position, _ ->
//            val itemSelected = parent.getItemAtPosition(position) as String
//            Toast.makeText(this, "Selected Zone: $itemSelected", Toast.LENGTH_SHORT).show()
//        }
//
//            //calling saveEmployeeData
//        saveBtn.setOnClickListener {
//            // Retrieve the access token
//            val accessToken = SharedPreferencesHelper.getAccessToken(this)
//            Log.d("SaveButton", "Retrieved Access Token: $accessToken") // Log for debugging
//
//            if (accessToken != null) {
//
//                // Get data from user input fields
//                val dateOfJoin = dojInput.text.toString().trim()
//                val zone = zoneInput.text.toString().trim()
//                val employeeCode = empCodeInput.text.toString().trim()
//                val employeeName = empNameInput.text.toString().trim()
//                val department = deptInput.text.toString().trim()
//                val category = ctgrInput.text.toString().trim()
//                val supervisor = sprInput.text.toString().trim()
//
//
//                // Collect missing fields
//                val missingFields = mutableListOf<String>()
//                if (dateOfJoin.isEmpty()) missingFields.add("Date of Join")
//                if (zone.isEmpty()) missingFields.add("Zone")
//                if (employeeCode.isEmpty()) missingFields.add("Employee Code")
//                if (employeeName.isEmpty()) missingFields.add("Employee Name")
//                if (department.isEmpty()) missingFields.add("Department")
//                if (category.isEmpty()) missingFields.add("Category")
//                if (supervisor.isEmpty()) missingFields.add("Supervisor")
//
//                // Display a message with the specific missing fields
//                if (missingFields.isNotEmpty()) {
//
//                    val message =
//                        "Please fill out the following fields: ${missingFields.joinToString(", ")
//                        }"
//                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//
//                    return@setOnClickListener
//                    searchProgressBar.visibility = View.GONE
//
//                }
//
//                val employeeDataRequest = EmployeeDataRequest(
//                    category = category,
//                    date = dateOfJoin,
//                    department = department,
//                    shift = shift,
//                    employee_code = employeeCode,
//                    employee_name = employeeName,
//                    supervisor_name = supervisor,
//                    zone = zone
//                )
//
//                // Call the API to save employee data using the access token
//                saveEmployeeData(accessToken, employeeDataRequest) { isSuccess ->
//                    saveProgressBar.visibility = View.GONE
//
//                    if (isSuccess) {
//                        // If successful, show the table and add the data
//                        showCardView()
//                        addDataToTable(
//                            dateOfJoin,
//                            zone,
//                            employeeCode,
//                            employeeName,
//                            department,
//                            category,
//                            supervisor
//                        )
//                    }
//                }
//            } else {
//                Toast.makeText(
//                    this,
//                    "Token not found. Please log in again.",
//                    Toast.LENGTH_SHORT
//                ).show()
//
//            }
//        }
//
//        //calling searchEmployeeData
//        serchBtn.setOnClickListener {
//            searchProgressBar.visibility = View.VISIBLE
//            // Retrieve access token
//            val accessToken = SharedPreferencesHelper.getAccessToken(this)
//
//            if (accessToken == null) {
//                Toast.makeText(this, "Access token not found. Please log in again.", Toast.LENGTH_SHORT).show()
//                searchProgressBar.visibility = View.GONE
//                return@setOnClickListener
//            }
//
//            // Get the search criteria from the input fields
//            val employeeCode = empCodeInput.text.toString().takeIf { it.isNotEmpty() }
//            val employeeName = empNameInput.text.toString().takeIf { it.isNotEmpty() }
//            val zone = zoneInput.text.toString().takeIf { it.isNotEmpty() }
//            val category = ctgrInput.text.toString().takeIf { it.isNotEmpty() }
//            val supervisorName = sprInput.text.toString().takeIf { it.isNotEmpty() }
//
//            // Check if all input fields are empty
//            if (employeeCode == null && employeeName == null && zone == null && category == null && supervisorName == null) {
//                Toast.makeText(this, "Please enter at least one search criterion.", Toast.LENGTH_SHORT).show()
//                searchProgressBar.visibility = View.GONE
//                return@setOnClickListener
//            }
//
//            // Call the API to search employee data
//            searchEmployeeData(
//                accessToken,
//                employeeCode = employeeCode,
//                employeeName = employeeName,
//                zone = zone,
//                category = category,
//                supervisorName = supervisorName,
//                callback = { isSuccess ->
//                    if (!isSuccess) {
//                        // Handle failure scenario here
//                        Toast.makeText(this, "Failed to retrieve employee data.", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            )
//        }
//
//
////update employee data
//        updtButton.setOnClickListener {
//
//            updateProgressBar.visibility = View.VISIBLE
//            // Retrieve the access token from SharedPreferences
//            val accessToken = SharedPreferencesHelper.getAccessToken(this)
//
//
//            // Check if accessToken is null
//            if (accessToken == null) {
//                Toast.makeText(this, "Token is missing. Please log in again.", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener // Exit the listener if token is null
//            }
//
//            val employeeCode = empCodeInput.text.toString()
//            val employeeDataRequest = EmployeeDataRequest(
//                date = dojInput.text.toString(),
//                zone = zoneInput.text.toString(),
//                employee_code = employeeCode,
//                employee_name = empNameInput.text.toString(),
//                department = deptInput.text.toString(),
//                category = ctgrInput.text.toString(),
//                supervisor_name = sprInput.text.toString()
//            )
//
//            // Launch a coroutine to update employee data
//            lifecycleScope.launch {
//                val success = updateEmployeeData(accessToken, employeeCode, employeeDataRequest)
//                if (success) {
//                    // Update the row in the table with the new data
//                    updateTableRow(employeeCode, employeeDataRequest)
//
//                    // Optionally clear the form
//                    clearForm()
//                }
//            }
//        }
//
//    }
//
//    //Creating Download options
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.download_option, menu)
//
//        // Find your menu item
//        val downloadItem1 = menu?.findItem(R.id.download1)
//        val downloadItem2 = menu?.findItem(R.id.download2)
//
//        // Create a SpannableString to customize the title
//        val styledTitle1 = SpannableString("Download as PDF")
//        styledTitle1.setSpan(ForegroundColorSpan(Color.BLACK), 0, styledTitle1.length, 0)
//        styledTitle1.setSpan(RelativeSizeSpan(1.1f), 0, styledTitle1.length, 0)
//
//        val styledTitle2 = SpannableString("Download in XCEL")
//        styledTitle2.setSpan(ForegroundColorSpan(Color.BLACK), 0, styledTitle2.length, 0)
//        styledTitle2.setSpan(RelativeSizeSpan(1.1f), 0, styledTitle2.length, 0)
//
//        downloadItem1?.title = styledTitle1
//        downloadItem2?.title = styledTitle2
//
//        return true
//    }
//
//
//    //update employee
//    private fun updateTableRow(employeeCode: String, employeeDataRequest: EmployeeDataRequest) {
//        // Find the index of the row to update based on employeeCode
//        for (i in 0 until scrollableTable.childCount) {
//            val row = scrollableTable.getChildAt(i) as TableRow
//            val empCodeView = row.getChildAt(0) as TextView
//
//            if (empCodeView.text.toString() == employeeCode) {
//                // Update the TextViews in both fixedRow and scrollableRow
//                (fixedTable.getChildAt(i) as TableRow).apply {
//                    (getChildAt(0) as TextView).text = employeeDataRequest.date
//                    (getChildAt(1) as TextView).text = employeeDataRequest.zone
//                }
//                (row).apply {
//                    (getChildAt(0) as TextView).text = employeeDataRequest.employee_code
//                    (getChildAt(1) as TextView).text = employeeDataRequest.employee_name
//                    (getChildAt(2) as TextView).text = employeeDataRequest.department
//                    (getChildAt(3) as TextView).text = employeeDataRequest.category
//                    (getChildAt(4) as TextView).text = employeeDataRequest.supervisor_name
//                }
//                break
//            }
//        }
//    }
//    private suspend fun updateEmployeeData(
//        accessToken: String,
//        employeeCode: String,
//        employeeDataRequest: EmployeeDataRequest
//    ): Boolean {
//        updateProgressBar.visibility = View.VISIBLE
//        val employeeUpdateApi = RetrofitClient.getEmployeeUpdateApi()
//
//        return try {
//            // Make the API call
//            val response = employeeUpdateApi.updateEmployee("Bearer $accessToken", employeeCode, employeeDataRequest)
//
//            // Check if the response is successful
//            if (response.isSuccessful) {
//                updateProgressBar.visibility = View.GONE
//                Toast.makeText(this@FormActivity, "Employee data updated successfully", Toast.LENGTH_SHORT).show()
//                true // Notify success
//            } else {
//                updateProgressBar.visibility = View.GONE
//                Toast.makeText(this@FormActivity, "Failed to update employee data: ${response.message()}", Toast.LENGTH_SHORT).show()
//                false // Notify failure
//            }
//        } catch (e: Exception) {
//            updateProgressBar.visibility = View.GONE
//            Toast.makeText(this@FormActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//            false // Notify failure
//        }
//    }
//
//
//
//    //search employee data
//    private fun searchEmployeeData(
//        accessToken: String,
//        employeeCode: String? = null,
//        employeeName: String? = null,
//        zone: String? = null,
//        category: String? = null,
//        supervisorName: String? = null,
//        callback: ((Boolean) -> Unit)? = null // Optional callback for success/failure notification
//    ) {
//        searchProgressBar.visibility = View.VISIBLE
//        // Create an instance of your API service
//        val apiService = RetrofitClient.getEmployeeSearchApi()
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
//        apiService.searchEmployeeData("Bearer $accessToken", searchRequest).enqueue(object : Callback<List<EmployeeDataRequest>> {
//            override fun onResponse(call: Call<List<EmployeeDataRequest>>, response: Response<List<EmployeeDataRequest>>) {
//                searchProgressBar.visibility = View.GONE // Hide the progress bar
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
//                                dateOfJoin = employee.date,
//                                zone = employee.zone,
//                                employeeCode = employee.employee_code,
//                                employeeName = employee.employee_name,
//                                department = employee.department,
//                                category = employee.category,
//                                supervisor = employee.supervisor_name
//                            )
//                        }
//
//                        // Show a success toast message
//                        Toast.makeText(this@FormActivity, "Employee data retrieved successfully.", Toast.LENGTH_SHORT).show()
//                        showCardView()
//                    } else {
//                        Toast.makeText(this@FormActivity, "No employee found with the given criteria.", Toast.LENGTH_SHORT).show()
//                    }
//                    callback?.invoke(true) // Notify success
//                } else {
//                    // Handle error case
//                    callback?.invoke(false) // Notify failure
//                    Toast.makeText(this@FormActivity, "Error fetching employee data: ${response.message()}", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<List<EmployeeDataRequest>>, t: Throwable) {
//                searchProgressBar.visibility = View.GONE
//                callback?.invoke(false) // Notify failure
//                Toast.makeText(this@FormActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//
//    private fun hideTable() {
//        Log.d("FormActivity", "Attempting to hide CardView")
//        cardView2.visibility = View.GONE
//        Log.d("FormActivity", "CardView visibility set to GONE")
//    }
//
//    private fun saveEmployeeData(accessToken: String?, employeeDataRequest: EmployeeDataRequest, callback: (Boolean) -> Unit) {
//        saveProgressBar.visibility = View.VISIBLE
//        val employeeApi = RetrofitClient.getEmployeeApi()
//
//        if (accessToken.isNullOrEmpty()) {
//            Toast.makeText(this, "Access token is invalid. Please log in again.", Toast.LENGTH_SHORT).show()
//            callback(false)
//            return
//        }
//
//        // Make the API call
//        employeeApi.saveEmployeeData("Bearer $accessToken", employeeDataRequest)
//            .enqueue(object : Callback<Void> {
//                override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                    saveProgressBar.visibility = View.GONE
//                    if (response.isSuccessful) {
//                        Toast.makeText(this@FormActivity, "Employee data saved successfully", Toast.LENGTH_SHORT).show()
//                        callback(true) // Notify success
//                    }  else {
//                        Toast.makeText(this@FormActivity, "Failed to save employee data: ${response.message()}", Toast.LENGTH_SHORT).show()
//                        callback(false) // Notify failure
//                    }
//                }
//
//                override fun onFailure(call: Call<Void>, t: Throwable) {
//                    saveProgressBar.visibility = View.GONE
//                    Toast.makeText(this@FormActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
//                    callback(false) // Notify failure
//                }
//            })
//    }
//
//
//    suspend fun deleteEmployeeFromDatabase(
//        accessToken: String,
//        employeeCode: String,
//        callback: (Boolean, String?) -> Unit
//    ) {
//        val employeeDeleteApi = RetrofitClient.getEmployeeDeleteApi()
//
//        try {
//            // Prepare the token with the "Bearer" prefix
//            val tokenWithPrefix = "Bearer $accessToken"
//
//            // Log the Authorization header and employee code for debugging
//            Log.d("deleteEmployee", "Authorization: $tokenWithPrefix, Employee Code: $employeeCode")
//
//            // Make the API call
//            val response = employeeDeleteApi.deleteEmployee(tokenWithPrefix, employeeCode)
//
//            if (response.isSuccessful) {
//                // Successful deletion, regardless of response content
//                callback(true, "Employee deleted successfully")
//            } else {
//                // Error handling with detailed message
//                val errorBody = response.errorBody()?.string()
//                val errorMessage = errorBody ?: "Error: ${response.message()}"
//                Log.e("deleteEmployee", "Failed to delete employee. Error: $errorMessage")
//                callback(false, errorMessage)
//            }
//        } catch (e: ProtocolException) {
//            // Handle the specific ProtocolException for 204 responses with content
//            Log.w("deleteEmployee", "Handled ProtocolException: ${e.localizedMessage}")
//            callback(true, "Employee deleted successfully") // Assume successful deletion
//        } catch (e: Exception) {
//            // Generic exception handling
//            Log.e("deleteEmployee", "Exception during deletion: ${e.localizedMessage}", e)
//            callback(false, "Exception: ${e.localizedMessage ?: "Unknown error occurred"}")
//        }
//    }
//
//
//    //table data will be visible
//    private fun showCardView() {
//        cardView2.visibility = View.VISIBLE
//    }
//
//    // Method to add data to the table after successful save
//    private fun addDataToTable(
//        dateOfJoin: String,
//        zone: String,
//        employeeCode: String,
//        employeeName: String,
//        department: String,
//        category: String,
//        supervisor: String
//    ) {
//        val fixedRow = TableRow(this)
//        val scrollableRow = TableRow(this)
//
//        // Create and add TextViews to the TableRow
//        fixedRow.addView(createTextView(dateOfJoin))
//        fixedRow.addView(createTextView(zone))
//        scrollableRow.addView(createTextView(employeeCode))
//        scrollableRow.addView(createTextView(employeeName))
//        scrollableRow.addView(createTextView(department))
//        scrollableRow.addView(createTextView(category))
//        scrollableRow.addView(createTextView(supervisor))
//
//        val editIcon = createImageView(R.drawable.edit) { v ->
//            // Find the parent TableRow by traversing up the view hierarchy
//            val row = findParentTableRow(v)
//            if (row != null) {
//                // Show confirmation dialog
//                showEditConfirmationDialog { confirmed ->
//                    if (confirmed) {
//                        // Proceed with editing the row
//                        editRow(fixedRow, scrollableRow) // Use your edit logic here
//                    }
//                }
//            }
//        }
//
//        val deleteIcon = createImageView(R.drawable.delete) { view ->
//            val scrollableRow = findParentTableRow(view) ?: return@createImageView
//            val rowIndex = scrollableTable.indexOfChild(scrollableRow)
//
//            if (rowIndex != -1) {
//                AlertDialog.Builder(this).apply {
//                    setTitle("Confirm Deletion")
//                    setMessage("Are you sure you want to delete this record permanently?")
//
//                    setPositiveButton("Yes") { _, _ ->
//                        val employeeCode = (scrollableRow.getChildAt(0) as? TextView)?.text.toString()
//
//                        if (employeeCode.isBlank()) {
//                            showToast("Invalid employee code.")
//                            return@setPositiveButton
//                        }
//
//                        // Retrieve the access token from SharedPreferences
//                        val accessToken = SharedPreferencesHelper.getAccessToken(this@FormActivity)
//
//                        if (!accessToken.isNullOrEmpty()) {
//                            // Log before calling the delete function
//                            Log.d("FormActivity", "Attempting to delete employee with code: $employeeCode")
//
//                            // Start a coroutine to call the suspend function
//                            CoroutineScope(Dispatchers.Main).launch {
//                                deleteEmployeeFromDatabase(accessToken, employeeCode) { isDeleted, message ->
//                                    if (isDeleted) {
//                                        scrollableTable.removeViewAt(rowIndex)
//                                        fixedTable.removeViewAt(rowIndex)
//
//                                        Log.d("FormActivity", "Row deleted at index: $rowIndex")
//
//                                        if (scrollableTable.childCount == 0 && fixedTable.childCount == 0) {
//                                            hideTable()
//                                        }
//
//                                        showToast("Employee record deleted successfully.")
//                                    } else {
//                                        showToast(message ?: "Failed to delete employee record.")
//                                    }
//                                }
//                            }
//                        } else {
//                            showToast("Access token not found. Please log in again.")
//                        }
//                    }
//
//                    setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
//
//                    create().show()
//                }
//            } else {
//                Log.d("FormActivity", "Error deleting row. Row not found.")
//            }
//        }
//
//
//        val actionLayout = LinearLayout(this)
//        actionLayout.orientation = LinearLayout.HORIZONTAL
//        actionLayout.gravity = Gravity.CENTER
//        actionLayout.addView(editIcon)
//        actionLayout.addView(deleteIcon)
//
//        scrollableRow.addView(actionLayout)
//
//        // Add click listeners to each row to track selection
//        fixedRow.setOnClickListener { selectRow(scrollableRow) }
//        scrollableRow.setOnClickListener { selectRow(scrollableRow) }
//        // Add the TableRow to the TableLayout
//        fixedTable.addView(fixedRow)
//        scrollableTable.addView(scrollableRow)
//
//        // Add employee code to the set of existing codes
//        existingCode.add(employeeCode)
//
//        // Optionally clear the form
//        clearForm()
//    }
//
//    private fun showToast(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }
//
//
//    //adding more table rows
//    private fun createTextView(text: String): TextView {
//        val textView = TextView(this)
//        textView.text = text
//        textView.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,150)
//        textView.setPadding(15, 15, 15, 15)
//        textView.setBackgroundResource(R.drawable.table_column)
//        textView.setTextColor(resources.getColor(R.color.white))
//        textView.gravity = Gravity.CENTER
//        textView.textSize = 16f
//        return textView
//    }
//    //to add action icons in action column
//    private fun createImageView(
//        @DrawableRes drawableId: Int,
//        onClickListener: View.OnClickListener
//    ): ImageView {
//        val imageView = ImageView(this)
//        imageView.setImageResource(drawableId)
//
//        // Adjust the layout parameters to ensure the image is centered and spaced
//        val layoutParams = TableRow.LayoutParams(60, 60) // Width and height for the icons
//        layoutParams.gravity = Gravity.CENTER // Center the icon vertically and horizontally
//
//        // Set margins to create space between the icons (left, top, right, bottom)
//        layoutParams.setMargins(10, 30, 10, 10) // Adjust as necessary to create the desired space
//
//        imageView.layoutParams = layoutParams
//        imageView.setOnClickListener(onClickListener)
//
//        return imageView
//    }
//
//    //to edit rows in table
//    private fun editRow(fixedRow: TableRow, scrollableRow: TableRow) {
//        // Set the row references to the rows being edited
//        editingFixedRow = fixedRow
//        editingScrollableRow = scrollableRow
//
//        // Populate the form fields with the row's data
//        val dojView = fixedRow.getChildAt(0) as? TextView
//        val zoneView = fixedRow.getChildAt(1) as? TextView
//        val empCodeView = scrollableRow.getChildAt(0) as? TextView
//        val empNameView = scrollableRow.getChildAt(1) as? TextView
//        val deptView = scrollableRow.getChildAt(2) as? TextView
//        val ctgrView = scrollableRow.getChildAt(3) as? TextView
//        val sprView = scrollableRow.getChildAt(4) as? TextView
//
//        // Ensure views are not null before populating the form
//        dojInput.setText(dojView?.text ?: "")
//        zoneInput.setText(zoneView?.text ?: "")
//        empCodeInput.setText(empCodeView?.text ?: "")
//        empNameInput.setText(empNameView?.text ?: "")
//        deptInput.setText(deptView?.text ?: "")
//        ctgrInput.setText(ctgrView?.text ?: "")
//        sprInput.setText(sprView?.text ?: "")
//
//        // Show the update button (if hidden)
//       updtButton.visibility = View.VISIBLE
//    }
//    private fun findParentTableRow(view: View): TableRow? {
//        var parent = view.parent
//        while (parent != null && parent !is TableRow) {
//            parent = parent.parent
//        }
//        Log.d("FormActivity", "Found TableRow: $parent")
//        return parent as? TableRow
//    }
//
//
//    // Function to delete employee, handles token refresh internally
//
//    // Function to select a row and track its index
//    private fun selectRow(row: TableRow) {
//        // Get the index of the selected row
//        selectedRowIndex = scrollableTable.indexOfChild(row)
//
//        // Clear previous highlights
//        for (i in 0 until scrollableTable.childCount) {
//            val childRow = scrollableTable.getChildAt(i) as TableRow
//            childRow.setBackgroundColor(Color.TRANSPARENT) // Reset background color
//        }
//
//        // Highlight the selected row
//        row.setBackgroundColor(Color.LTGRAY) // Highlight the selected row
//    }
//
//    // Function to show confirmation dialog
//    private fun showEditConfirmationDialog(onConfirmed: (Boolean) -> Unit) {
//        // Create an AlertDialog Builder
//        val builder = AlertDialog.Builder(this)
//
//        // Set dialog properties
//        builder.setTitle("Confirm Edit")
//            .setMessage("Are you sure you want to edit this record?")
//            .setPositiveButton("Yes") { dialog, _ ->
//                onConfirmed(true) // User confirmed
//                dialog.dismiss() // Dismiss the dialog
//            }
//            .setNegativeButton("No") { dialog, _ ->
//                onConfirmed(false) // User did not confirm
//                dialog.dismiss() // Dismiss the dialog
//            }
//
//        // Create and show the dialog
//        val dialog = builder.create()
//        dialog.show()
//    }
//
//    //it will clear the form
//    private fun clearForm() {
//
//        zoneInput.text?.clear()
//        empCodeInput.text?.clear()
//        empNameInput.text?.clear()
//        deptInput.text?.clear()
//        ctgrInput.text?.clear()
//        sprInput.text?.clear()
//    }
//
//    //download pdf and excell
//    // Handle menu item selection
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.download1 -> {
//                // Download the table as PDF
//                downloadTableAsPdf()
//                true
//            }
//            R.id.download2 -> {
//                // Download the table as Excel
//                downloadTableAsExcel()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//    // Download Table as PDF
//    private fun downloadTableAsPdf() {
//        // Create a PDF file in the Documents directory
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, "EmployeeData_${System.currentTimeMillis()}.pdf")
//            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
//            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
//        }
//
//        val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
//
//        uri?.let {
//            try {
//                // Create a PdfWriter with the output stream
//                val pdfWriter = PdfWriter(contentResolver.openOutputStream(it)!!)
//                val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(pdfWriter)
//                val document = Document(pdfDocument)
//
//                // Create a table with a fixed number of columns
//                val table = Table(floatArrayOf(1f, 1f, 1f, 1f, 1f, 1f, 1f))
//
//                // Add data rows from both tables
//                for (i in 0 until fixedTable.childCount) {
//                    val fixedRow = fixedTable.getChildAt(i) as TableRow
//                    val scrollableRow = scrollableTable.getChildAt(i) as TableRow
//
//                    // Add data from fixedRow
//                    for (j in 0 until fixedRow.childCount) {
//                        val view = fixedRow.getChildAt(j)
//                        if (view is TextView) {
//                            val cellText = view.text.toString()
//                            table.addCell(Paragraph(cellText).setTextAlignment(TextAlignment.CENTER))
//                        }
//                    }
//
//                    // Add data from scrollableRow (excluding action buttons)
//                    for (j in 0 until scrollableRow.childCount - 1) {
//                        val view = scrollableRow.getChildAt(j)
//                        if (view is TextView) {
//                            val cellText = view.text.toString()
//                            table.addCell(Paragraph(cellText).setTextAlignment(TextAlignment.CENTER))
//                        }
//                    }
//                }
//
//                // Add table to the document
//                document.add(table)
//                document.close()
//
//                Toast.makeText(this, "PDF downloaded successfully", Toast.LENGTH_SHORT).show()
//            } catch (e: IOException) {
//                e.printStackTrace()
//                Toast.makeText(this, "Error creating PDF file: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
//            }
//        } ?: run {
//            Toast.makeText(this, "Error creating PDF file", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    // Download Table as Excel
//    private fun downloadTableAsExcel() {
//        val workbook: Workbook = XSSFWorkbook()
//        val sheet: XSSFSheet = workbook.createSheet("Employee Data") as XSSFSheet
//
//        // Populate rows with data from both tables
//        for (i in 0 until fixedTable.childCount) {
//            val fixedRow = fixedTable.getChildAt(i) as TableRow
//            val scrollableRow = scrollableTable.getChildAt(i) as TableRow
//            val dataRow = sheet.createRow(i + 1) // Start from the second row
//
//            // Add data from fixedRow
//            for (j in 0 until fixedRow.childCount) {
//                val view = fixedRow.getChildAt(j)
//                if (view is TextView) {
//                    val cellText = view.text.toString()
//                    dataRow.createCell(j).setCellValue(cellText) // Add data from fixedRow to Excel
//                }
//            }
//
//            // Add data from scrollableRow (excluding action buttons)
//            for (j in 0 until scrollableRow.childCount - 1) { // Exclude last column for Action buttons
//                val view = scrollableRow.getChildAt(j)
//                if (view is TextView) {
//                    val cellText = view.text.toString()
//                    dataRow.createCell(j + fixedRow.childCount).setCellValue(cellText) // Adjust index for Excel
//                }
//            }
//        }
//
//        // Create a unique filename with a timestamp
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, "EmployeeData_${System.currentTimeMillis()}.xlsx")
//            put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
//            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
//        }
//
//        val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
//
//        uri?.let {
//            try {
//                val outputStream = contentResolver.openOutputStream(it)
//                workbook.write(outputStream)
//                outputStream?.close()
//                workbook.close()
//                Toast.makeText(this, "Excel file downloaded successfully", Toast.LENGTH_SHORT).show()
//            } catch (e: IOException) {
//                e.printStackTrace()
//                Toast.makeText(this, "Error downloading Excel file: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
//            }
//        } ?: run {
//            Toast.makeText(this, "Error creating Excel file", Toast.LENGTH_SHORT).show()
//        }
//
//    }
//    private fun clearTable() {
//        // Assuming the header is always the first row
//        val headerRowCount = 1
//
//        // Remove all child views except for the header row
//        while (fixedTable.childCount > headerRowCount) {
//            fixedTable.removeViewAt(headerRowCount)
//        }
//        while (scrollableTable.childCount > headerRowCount) {
//            scrollableTable.removeViewAt(headerRowCount)
//        }
//    }
//}
//
