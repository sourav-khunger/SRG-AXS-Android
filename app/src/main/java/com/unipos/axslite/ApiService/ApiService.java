package com.unipos.axslite.ApiService;

import com.unipos.axslite.Database.Entities.TaskInfoEntity;
import com.unipos.axslite.POJO.DriverDepartureResponse;
import com.unipos.axslite.POJO.DriverLogDutyResponse;
import com.unipos.axslite.POJO.LoginResponse;
import com.unipos.axslite.POJO.StatusReasonResponse;
import com.unipos.axslite.POJO.TaskInfoResponse;
import com.unipos.axslite.POJO.TaskInfoUpdateResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiService {

    @POST("login")
    @FormUrlEncoded
    Call<LoginResponse> getLoginResponse(@Field("username") String username);

    @POST("complete-task")
    Call<TaskInfoUpdateResponse> updateTaskInfo(@Body TaskInfoEntity taskInfoEntity,
                                                @Header("Authorization") String authorizationHeader);

    @POST("confirm-dc-departure")
    Call<DriverDepartureResponse> comfirmDCdeparture(
            @Field("batchId") String batchId,
            @Field("departureTime") String departureTime,
            @Header("Authorization") String authorizationHeader);

    @POST("driver-logduty")
    @FormUrlEncoded
    Call<DriverLogDutyResponse> driverLogDuty(@Field("companyId") String companyId, @Field("logStatus") String logStatus,
                                              @Header("Authorization") String authorizationHeader);

    @GET("driver-tasklist")
    Call<TaskInfoResponse> getTaskList(@Query("companyId") String companyId, @Query("taskDate") String taskDate,
                                       @Header("Authorization") String authorizationHeader);

    @GET("shipment-statuslist")
    Call<StatusReasonResponse> shipmentStatus(@Query("companyId") String companyId,
                                              @Header("Authorization") String authorizationHeader);
}