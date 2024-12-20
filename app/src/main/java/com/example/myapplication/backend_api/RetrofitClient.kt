package com.example.myapplication.backend_api

import com.example.myapplication.interface_api.ApiService
import com.example.myapplication.interface_api.AttendanceApiService
import com.example.myapplication.interface_api.AuthService
import com.example.myapplication.interface_api.ChangePasswordService
import com.example.myapplication.interface_api.EmployeeApi
import com.example.myapplication.interface_api.EmployeeDelete
import com.example.myapplication.interface_api.EmployeeSearchApi
import com.example.myapplication.interface_api.EmployeeUpdate
import com.example.myapplication.interface_api.JobSetReportApi
import com.example.myapplication.interface_api.JobSheetDetailsApi
import com.example.myapplication.interface_api.LogoutService
import com.example.myapplication.interface_api.OtpService
import com.example.myapplication.interface_api.RegisterApi
import com.example.myapplication.interface_api.ReportApiService
import com.example.myapplication.interface_api.ReportDataService
import com.example.myapplication.interface_api.ResetPasswordService
import com.example.myapplication.interface_api.TimeSheetEmployeeApi
import com.example.myapplication.interface_api.TimeSheetEmployeeSearchApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://timesheet003.pythonanywhere.com/api/user/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    // Instance for Login API
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // Instance for Registration API
    val registerApi: RegisterApi by lazy {
        retrofit.create(RegisterApi::class.java)
    }

    val otpService: OtpService by lazy {
        retrofit.create(OtpService::class.java)
    }

    val resetPasswordService: ResetPasswordService by lazy {
        retrofit.create(ResetPasswordService::class.java)
    }

    val timeSheetSaveEmployeeApi: TimeSheetEmployeeApi by lazy {
        retrofit.create(TimeSheetEmployeeApi::class.java)
    }

    //add Employee details Form
    fun getEmployeeApi(): EmployeeApi {
        return retrofit.create(EmployeeApi::class.java)
    }

    fun getEmployeeDeleteApi(): EmployeeDelete {
        return retrofit.create(EmployeeDelete::class.java)
    }

    fun getEmployeeUpdateApi(): EmployeeUpdate {
        return retrofit.create(EmployeeUpdate::class.java)
    }
    //------------------------------------------------------

    fun getEmployeeSearchApi(): EmployeeSearchApi {
        return retrofit.create(EmployeeSearchApi::class.java)
    }

    //it will search employee data in update timesheet
    fun timeSheetEmployeeSearchApi(): TimeSheetEmployeeSearchApi {
        return retrofit.create(TimeSheetEmployeeSearchApi::class.java)
    }
//to save updated timesheet
    val attendanceApiService: AttendanceApiService by lazy {
        retrofit.create(AttendanceApiService::class.java)
    }
    //logout service
    fun getLogoutService(): LogoutService {
        return retrofit.create(LogoutService::class.java)
    }
    //change password
    fun changePasswordService(): ChangePasswordService{
        return retrofit.create(ChangePasswordService::class.java)
    }
    //--------------------------------------------------------
    //report-month
    val reportDataApi = retrofit.create(ReportDataService::class.java)


    //only report
    fun reportApiService(): ReportApiService {
        return retrofit.create(ReportApiService::class.java)
    }

    //JOB SHEET DETAILS
    val jobSheetDetailsApi: JobSheetDetailsApi by lazy {
        retrofit.create(JobSheetDetailsApi::class.java)
    }

    //JOB SET REPORT
    fun getJobSetReportApi(): JobSetReportApi = retrofit.create(JobSetReportApi::class.java)
}