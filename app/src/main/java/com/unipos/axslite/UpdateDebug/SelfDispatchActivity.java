package com.unipos.axslite.UpdateDebug;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.unipos.axslite.Adapter.SelfDispatchListAdapter;
import com.unipos.axslite.ApiService.ApiService;
import com.unipos.axslite.ApiService.ApiUtils;
import com.unipos.axslite.Database.Entities.TaskInfoEntity;
import com.unipos.axslite.Database.ViewModel.TaskInfoViewModel;
import com.unipos.axslite.POJO.LoginResponse;
import com.unipos.axslite.POJO.TaskInfo;
import com.unipos.axslite.POJO.TaskInfoResponse;
import com.unipos.axslite.R;
import com.unipos.axslite.Utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelfDispatchActivity extends AppCompatActivity {
    ImageView scanButton;
    RecyclerView listScannedPackages;
    SelfDispatchListAdapter selfDispatchListAdapter;
    private List<TaskInfo> taskInfoEntityList;
    ApiService apiService;
    private String TAG = "SELFDISPATCH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_dispatch);
        scanButton = findViewById(R.id.scanButton);

        apiService = ApiUtils.getAPIService();
        listScannedPackages = findViewById(R.id.listScannedPackages);
        listScannedPackages.setLayoutManager(new LinearLayoutManager(this));
        listScannedPackages.setItemAnimator(new DefaultItemAnimator());
        listScannedPackages.setHasFixedSize(true);
        taskInfoEntityList = new ArrayList<TaskInfo>();
        String taskInfoGroupByLocation = PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.SELECTED_LOCATION, "");

        pullData();
    }

    private void pullData() {
        //mTaskInfoRepository.deleteAll();

        String jsonLoginResponse = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.PREF_KEY_LOGIN_RESPONSE, "");
        LoginResponse loginResponse = new Gson().fromJson(jsonLoginResponse, LoginResponse.class);
        String token = loginResponse.getToken();
        Date date = new Date();
        String curDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        String selectedDate = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.PREF_KEY_SELECTED_DATE, curDate);
        String compId = "" + loginResponse.getDriverInfo().getCompanyId();
        // compId = "34";

        apiService.getTaskList(compId, selectedDate, Constants.AUTHORIZATION_TOKEN + token).enqueue(new Callback<TaskInfoResponse>() {
            @Override
            public void onResponse(Call<TaskInfoResponse> call, Response<TaskInfoResponse> response) {
                Log.d(TAG, "onResponse: ");
                if (response.code() == 200) {
                    Log.e(TAG, "size: AccessLITE " + response.body().getListOfTaskInfo().size());
                    taskInfoEntityList = response.body().getListOfTaskInfo();
                    listScannedPackages.setAdapter(new SelfDispatchListAdapter(getApplicationContext(), taskInfoEntityList));

                } else {
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<TaskInfoResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}