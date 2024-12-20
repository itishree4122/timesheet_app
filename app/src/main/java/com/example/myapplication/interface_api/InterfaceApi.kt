package com.example.myapplication.interface_api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


// Data class for the access token response

data class RefreshTokenRequest(
    val refresh_token: String
)

data class AccessTokenResponse(
    val access_token: String
)

interface AuthService {
    @POST("token/refresh/")
  suspend fun refreshAccessToken(@Body request: RefreshTokenRequest): Response<AccessTokenResponse>
}

// Data class to handle request payload
data class LoginRequest(
    val user_name: String,
    val password: String
)

data class User(
    val name: String,       // Matches "name" in JSON
    val user_name: String,      // Matches "email" in JSON
    val is_admin: Boolean   // Matches "is_admin" in JSON
)

data class Token(

    val refresh: String,
    val access: String,
)

data class LoginResponse(
    val msg: String,        // Matches "msg" in JSON
    val user: User,        // Matches "user" in JSON, which is of type User
    val Token: Token

)


data class RegisterRequest(
    val name: String,
    val user_name: String,
    val password: String,
    val password2: String,
    val role: String
)

data class OtpRequest(
    val email: String
)

data class ResetPassword(
    val email: String,
    val otp: String,
    val new_password: String
)

data class ChangePassword(
    val password: String,
    val password2: String
)

interface ChangePasswordService {
    @POST("changepassword/")
    fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePassword): Call<Void>
}

// Data class for employee data request
data class EmployeeDataRequest(
    val category: String,
    val date: String,
    val department: String,
    val shift: String,
    val employee_code: String,
    val employee_name: String,
    val supervisor_name: String,
    val zone: String

    )
// API interface for saving employee data
interface EmployeeApi {
    @POST("employees/create/")
    fun saveEmployeeData(
        @Header("Authorization") token: String, // Pass the access token in the header
        @Body employeeDataRequest: EmployeeDataRequest
    ): Call<Void> // Void since we don't expect any response body
}
//employee search
interface EmployeeSearchApi {
    @POST("employee/search/")
    fun searchEmployeeData(
        @Header("Authorization") token: String, // Pass the access token in the header
        @Body searchRequest: EmployeeSearchRequest
    ): Call<List<EmployeeDataRequest>>
}
//for search field
data class EmployeeSearchRequest(
    val employee_code: String? = null,
    val employee_name: String? = null,
    val zone: String? = null,
    val category: String? = null,
    val supervisor_name: String? = null
)

//for delete
interface EmployeeDelete {
    @DELETE("employees/{employeeCode}/")
    suspend fun deleteEmployee(
        @Header("Authorization") token: String,
        @Path("employeeCode") employeeCode: String
    ): Response<Void>
}

//for update employee
interface EmployeeUpdate {
    @PUT("employees/{employeeCode}/")
    suspend fun updateEmployee(
        @Header("Authorization") token: String,
        @Path("employeeCode") employeeCode: String,
        @Body employeeDataRequest: EmployeeDataRequest
    ): Response<Void>
}
//for timesheet save employee
data class EmployeeData(
    val date_of_work: String,
    val zone: String,
    val employee_code: String,
    val employee_name: String,
    val department: String,
    val category: String,
    val supervisor_name: String,
    val shift: String,
    val sk: Int,
    val sk_ot: Int,
    val ssk: Int,
    val ssk_ot: Int,
    val usk: Int,
    val usk_ot: Int,
    val attendance: Boolean
)

//time sheet employee save
interface TimeSheetEmployeeApi {
    @POST("attendance/")
    suspend fun saveEmployeeData(
        @Header("Authorization") accessToken: String,
        @Body employeeDataList: List<EmployeeData>
    ): Response<Unit>
}

//time sheet employee search
interface TimeSheetEmployeeSearchApi {
    @POST("attendance/search/")
    fun searchTimeSheetEmployeeData(
        @Header("Authorization") token: String, // Pass the access token in the header
        @Body searchRequest: EmployeeSearchRequest
    ): Call<List<EmployeeData>>
}

//saving update timesheet
data class AttendanceResponse(
    val msg: String,
    val data: EmployeeData
)


interface AttendanceApiService {
    @PUT("attendance/update/{employeeCode}/{date}/")
    suspend fun updateAttendance(
        @Header("Authorization") token: String,
        @Path("employeeCode") employeeCode: String,
        @Path("date") date:String,
        @Body attendanceData: EmployeeData
    ): Response<AttendanceResponse>
}

// Data class to represent an individual employee record
data class EmployeeRecord(
    val zone: String,
    val employee_code: String,
    val employee_name: String,
    val department: String,
    val category: String,
    val supervisor_name: String,
    val sk: Int,
    val sk_ot: Int,
    val ssk: Int,
    val ssk_ot: Int,
    val usk: Int,
    val usk_ot: Int,
    val attendance: Int
)

// Data class to represent the overall report response
data class ReportResponse(
    val month: String,
    val records: List<EmployeeRecord>
)

data class ReportData(
    val from_date: String,
    val to_date: String,
    val supervisor_name: String
)

interface ReportDataService {
    @POST("month/")
    suspend fun reportDataApiService(
        @Header("Authorization") token: String,
        @Body reportData: ReportData
    ): Response<ReportResponse> // Wrap the return type with Response
}

//report data for api report
data class AttendanceRecord(

    val date_of_work: String,
    val zone: String,
    val employee_code: String,
    val employee_name: String,
    val department: String,
    val category: String,
    val supervisor_name: String,
    val shift: String,
    val sk: Int,
    val sk_ot: Int,
    val ssk: Int,
    val ssk_ot: Int,
    val usk: Int,
    val usk_ot: Int,
    val attendance: Boolean
)

data class AttendanceReport(
    val from_date: String,
    val to_date: String,
    val supervisor_name: String,
    val records: List<AttendanceRecord>
)

data class AttendanceReportRequest(
    val from_date: String,
    val to_date: String,
    val supervisor_name: String
)

interface ReportApiService {
    @POST("report/")
    fun getAttendanceReport(
        @Header("Authorization") token: String,
        @Body request: AttendanceReportRequest
    ): Call<List<AttendanceRecord>> // Change to a list of AttendanceRecord
}


// Interface for registration API service
interface RegisterApi {
    @POST("register/")
    fun registerUser(@Body registerRequest: RegisterRequest): Call<Response<Unit>> // No response body
}

interface ApiService {

    @POST("login/")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>
}

interface OtpService {
    @POST("sendOtp/")
    fun sendOtp(@Body request: OtpRequest): Call<Void>
}

interface ResetPasswordService {
    @POST("reset-password/")
    fun resetPassword(@Body request: ResetPassword): Call<Void>
}

interface LogoutService {
    @POST("logout/")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<Unit>
}

// JOB SHEET DETAILS
data class JobSheetDetails(
    val date: String,
    val zone: String,
    val supervisor_name: String,
    val low_stub: Int,
    val anode_covering: Int,
    val side_making: Int,
    val hole: Int,
    val house_keeping: Int,
    val supply: Int,

)

data class JobSheetDetailsResponse(
    val date: String,
    val zone: String,
    val supervisor_name: String,
    val low_stub: Int,
    val anode_covering: Int,
    val side_making: Int,
    val hole: Int,
    val house_keeping: Int,
    val supply: Int,
    val skilled: Int,
    val semi_skilled: Int,
    val unskilled: Int
)


interface JobSheetDetailsApi {
    @POST("jobsheet/")
    suspend fun submitJobSheetDetails(
        @Header("Authorization") token: String,
        @Body jobSheetDetails: JobSheetDetails
    ): Response<JobSheetDetailsResponse> // Use JobSheetDetailsResponse
}

//JOB SET REPORT
// Updated Request Class
data class JobSetReportRequest(
    val supervisor_name: String,
    val from_date: String,
    val to_date: String
)

// Updated Response Class
data class JobSetReportResponse(
    val attendance_records: List<JobSetReportRecord>,
    val job_set_summary: List<JobSetReportSummary>
)

// Updated AttendanceRecord Class
data class JobSetReportRecord(
    val date: String,
    val total_sk: Int,
    val total_ssk: Int,
    val total_usk: Int
)

// Updated JobSetSummary Class
data class JobSetReportSummary(
    val date: String,
    val supervisor_name: String,
    val skilled: Double,
    val semi_skilled: Double,
    val unskilled: Double
)
interface JobSetReportApi {
    @POST("jobsheet_timesheet/")
    fun getJobSetReportData(
        @Header("Authorization") accessToken: String,
        @Body request: JobSetReportRequest
    ): Call<JobSetReportResponse>
}





