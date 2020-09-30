package com.unipos.axslite.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.unipos.axslite.Activity.ChooseCompanyActivity;
import com.unipos.axslite.Activity.ScreenSlidePagerActivity;
import com.unipos.axslite.Activity.ShowListOfTaskGroupByLocationKeyActivity;
import com.unipos.axslite.Activity.ShowPackageDetailsActivity;
import com.unipos.axslite.Adapter.RunTaskAdapter;
import com.unipos.axslite.Adapter.RunTaskEntityAdapter;
import com.unipos.axslite.Adapter.SelfDispatchListAdapter;
import com.unipos.axslite.Adapter.ToDoTaskListAdapter;
import com.unipos.axslite.ApiService.ApiService;
import com.unipos.axslite.ApiService.ApiUtils;
import com.unipos.axslite.Database.Entities.RunInfoEntity;
import com.unipos.axslite.Database.Entities.TaskInfoEntity;
import com.unipos.axslite.Database.Repository.TaskInfoRepository;
import com.unipos.axslite.Database.ViewModel.TaskInfoViewModel;
import com.unipos.axslite.POJO.DriverDepartureResponse;
import com.unipos.axslite.POJO.LoginResponse;
import com.unipos.axslite.POJO.RunInfo;
import com.unipos.axslite.POJO.TaskInfo;
import com.unipos.axslite.POJO.TaskInfoGroupByLocationKey;
import com.unipos.axslite.POJO.TaskInfoResponse;
import com.unipos.axslite.R;
import com.unipos.axslite.Utils.ActionBottomSheetDialog;
import com.unipos.axslite.Utils.BottomSheetListView;
import com.unipos.axslite.Utils.Constants;
import com.unipos.axslite.Utils.TimePicker24Hours;
import com.unipos.axslite.here.turn.HereTurnNavActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ToDoTaskListFragment extends Fragment implements ActionBottomSheetDialog.ItemClickListener {

    public static final String TO_DO_TASK_LIST_FRAGMENT_TITLE = "To Do";
    List<TaskInfoEntity> listOfTaskInfo = new ArrayList<TaskInfoEntity>();
    List<RunInfoEntity> listOfRunInfo = new ArrayList<RunInfoEntity>();
    List<RunInfo> runInfoList = new ArrayList<RunInfo>();
    List<TaskInfoGroupByLocationKey> listOfTaskInfoGroupByLocationKeys = new ArrayList<TaskInfoGroupByLocationKey>();
    private String TAG = "ToDoTaskListFragment";
    private TaskInfoViewModel taskInfoViewModel;
    private RecyclerView recyclerView, listViewBtmSheet;
    private RecyclerView.LayoutManager layoutManager;
    private ToDoTaskListAdapter adapter;
    private TaskInfoRepository mTaskInfoRepository;
    List<TaskInfo> taskInfoEntities;
    TextView emptyTxt, selectedRunTxt;
    ApiService apiService;
    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;
    private int isSelected = 0;
    RunTaskAdapter runTaskAdapter;
    RunTaskEntityAdapter runTaskEntityAdapter;
    private TextView bottomList;
    int positionOfRun = 0;
    boolean isPending = false;
    Button confirm_dcButton;
    private String batchId = "";
    Dialog dialog;
    ScreenSlidePagerActivity activity;

    public ToDoTaskListFragment() {

    }

    public interface ItemClickListener {
        void onItemClick(String pos);
    }


    public static ToDoTaskListFragment newInstance(String param1, String param2) {
        ToDoTaskListFragment fragment = new ToDoTaskListFragment();
        return fragment;
    }

    public void getPostionOfRun(int positionOfRun) {
        this.positionOfRun = positionOfRun;
//        bottomList.setText("Selected Run #" + listOfRunInfo.get(positionOfRun).getRunNo());
    }

    ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_do_task_list, container, false);
        confirm_dcButton = view.findViewById(R.id.confirm_dcButton);
        bottomList = view.findViewById(R.id.bottomList);
        selectedRunTxt = view.findViewById(R.id.selectedRunTxt);
        layoutBottomSheet = view.findViewById(R.id.bottom_sheet);
        emptyTxt = view.findViewById(R.id.emptyTxt);
        recyclerView = view.findViewById(R.id.to_do_recycler_view);
        listViewBtmSheet = view.findViewById(R.id.listViewBtmSheet);
        listViewBtmSheet.setHasFixedSize(true);
        recyclerView.setHasFixedSize(true);

        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        layoutManager = new LinearLayoutManager(getActivity());
        listViewBtmSheet.setNestedScrollingEnabled(false);
        listViewBtmSheet.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLayoutManager(layoutManager);
        mTaskInfoRepository = new TaskInfoRepository(getActivity().getApplication());
        apiService = ApiUtils.getAPIService();
//        ActionBottomSheetDialog openBottomSheet = ActionBottomSheetDialog.newInstance();
//        openBottomSheet.setOnClickListner(ToDoTaskListFragment.this::onItemClick);
//        selectedDate.setText();
//        if (listOfTaskInfoGroupByLocationKeys.size() > 0) {
//        taskInfoEntities = mTaskInfoRepository.getTaskInfos1();
        String jsonLoginResponse = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Constants.PREF_KEY_LOGIN_RESPONSE, "");
        LoginResponse loginResponse = new Gson().fromJson(jsonLoginResponse, LoginResponse.class);
        int isOnduty = loginResponse.getDriverInfo().getOnDuty();

        if (isOnduty == 1) {
            pullData();
            getTasks();
        } else {

        }
        selectedRunTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: " + sheetBehavior.getState());
                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                }

            }
        });
        confirm_dcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPending) {
                    Toast.makeText(getContext(), "Complete your pending Tasks first!", Toast.LENGTH_SHORT).show();
                } else {
                    showTimePicker();
//                    confirmDeparture();
                }
            }
        });
        /*bottomList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheet.getList(listOfRunInfo);
                openBottomSheet.show(getFragmentManager(), ActionBottomSheetDialog.TAG);
//                openBottomSheet.dismiss();
            }
        });*/
//        } else {
//        }
        sheetBehavior.setHalfExpandedRatio(0.5F);

        sheetBehavior.setFitToContents(false);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
//                        btnBottomSheet.setText("Close Sheet");
                        Log.e(TAG, "Close Sheet: ");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        Log.e(TAG, "Expand: ");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//
            }
        });
        return view;
    }


    void confirmDeparture(String time) {
        String jsonLoginResponse = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Constants.PREF_KEY_LOGIN_RESPONSE, "");
        LoginResponse loginResponse = new Gson().fromJson(jsonLoginResponse, LoginResponse.class);
        String departureTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " " + time;

        Log.e(TAG, "confirmDeparture: " + departureTime);
        apiService.comfirmDCdeparture(batchId, departureTime, Constants.AUTHORIZATION_TOKEN + loginResponse.getToken()).enqueue(new Callback<DriverDepartureResponse>() {
            @Override
            public void onResponse(Call<DriverDepartureResponse> call, Response<DriverDepartureResponse> response) {
                if (response.body().getStatus().equals("SUCCESS")) {
                    Toast.makeText(getContext(), "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                Log.e(TAG, "onResponse: " + response.body().getMessage());
            }

            @Override
            public void onFailure(Call<DriverDepartureResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.activity = (ScreenSlidePagerActivity) activity;

        } catch (ClassCastException e) {
            throw new RuntimeException(activity.toString()
                    + " must implement ItemClickListener");
        }
    }

    void getTasks() {
        adapter = new ToDoTaskListAdapter(listOfTaskInfoGroupByLocationKeys, getActivity().getApplication()/*, listOfTaskInfo*/);
        adapter.setOnItemClickListener(new ToDoTaskListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                TaskInfoGroupByLocationKey taskInfoGroupByLocationKey = listOfTaskInfoGroupByLocationKeys.get(position);
                String taskInfoString = new Gson().toJson(taskInfoGroupByLocationKey);
                TaskInfoEntity taskInfo = listOfTaskInfo.get(position);
                String taskInfoStr = new Gson().toJson(taskInfo);
                Log.e(TAG, "taskInfo: " + taskInfoStr);
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                        .putString(Constants.SELECTED_LOCATION, taskInfoString)
                        .apply();
                /*PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                        .putString(Constants.SELECTED_TASK, taskInfoString)
                        .apply();*/

                if (taskInfoGroupByLocationKey.getArrivalTime() == null || taskInfoGroupByLocationKey.getArrivalTime().equals("")) {
                    Intent intent = new Intent(getActivity(), ShowPackageDetailsActivity.class);
                    intent.putExtra("task", taskInfoStr);
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(getActivity(), ShowListOfTaskGroupByLocationKeyActivity.class);
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);

        taskInfoViewModel = new ViewModelProvider(getActivity()).get(TaskInfoViewModel.class);
        try {
            String jsonLoginResponse = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Constants.PREF_KEY_LOGIN_RESPONSE, "");
            Log.d(TAG, "onCreateView: " + jsonLoginResponse);
            LoginResponse loginResponse = new Gson().fromJson(jsonLoginResponse, LoginResponse.class);

//            if (taskInfoViewModel.getRunInfoList().hasObservers()) {

            taskInfoViewModel.getRunInfoList().observe(getActivity(), new Observer<List<RunInfoEntity>>() {
                @Override
                public void onChanged(List<RunInfoEntity> runInfoEntities) {
                    listOfRunInfo.clear();
                    listOfRunInfo.addAll(runInfoEntities);
                    Log.e(TAG, "onChanged: LIST " + listOfRunInfo.size());

                    for (RunInfoEntity runInfo : runInfoEntities) {
                        Log.e(TAG, "onChanged: " + runInfo.toString());
                    }
//                    if (isSelected != 0) {

//                    } else {
//                        selectedRunTxt.setText("Selected Run - Run #" + listOfRunInfo.get(0).getRunNo());
                    if (runInfoEntities.size() == 0) {
                        Log.e(TAG, "onChanged: TRY Again!");
                        confirm_dcButton.setVisibility(View.GONE);
                        runTaskEntityAdapter = new RunTaskEntityAdapter(runInfoEntities, getContext(), 0);
                        runTaskEntityAdapter.notifyDataSetChanged();
                        if (runTaskAdapter != null) {
                            runTaskAdapter = new RunTaskAdapter(runInfoList, getContext(), 0);
                            runTaskAdapter.notifyDataSetChanged();
                        }
                    } else {
                        runTaskEntityAdapter = new RunTaskEntityAdapter(runInfoEntities, getContext(), 0);
                        runTaskEntityAdapter.setOnItemClickListener(new RunTaskEntityAdapter.OnItemClickListener() {
                            @Override
                            public void onGetRunPosition(int position) {
                                selectedRunTxt.setText("Selected Run - Run #" + runInfoList.get(position).getRunNo());
                                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                runTaskEntityAdapter.updatePosition(position);
                                runTaskEntityAdapter.notifyDataSetChanged();
                                getTaskByLocationKeys(position);

                                if (runInfoEntities.get(position).getRouteStarted() == 0) {
                                    confirm_dcButton.setVisibility(View.VISIBLE);

                                    for (int i = 0; i < taskInfoEntities.size(); i++) {
                                        if (!taskInfoEntities.get(i).getWorkStatus().equals("pending")) {
                                            isPending = false;
                                            batchId = runInfoEntities.get(position).getBatchId();

                                            break;
                                        }
                                    }
                                }
                                if (runInfoEntities.get(position).getRouteStarted() == 1) {
                                    confirm_dcButton.setVisibility(View.GONE);
                                    for (int i = 0; i < taskInfoEntities.size(); i++) {
                                        if (taskInfoEntities.get(i).getWorkStatus().equals("pending")) {
                                            isPending = true;
                                            batchId = runInfoEntities.get(position).getBatchId();
                                            break;
                                        }
                                        if (!taskInfoEntities.get(i).getWorkStatus().equals("pending")) {
                                            isPending = false;
                                            batchId = runInfoEntities.get(position).getBatchId();
                                        }

                                    }
                                }

                            }
                        });

                        listViewBtmSheet.setAdapter(runTaskEntityAdapter);
                    }
//                    }
                }
            });
//            }
            taskInfoViewModel.getTaskInfoGroupByLocationKeys().observe(activity, new Observer<List<TaskInfoGroupByLocationKey>>() {
                @Override
                public void onChanged(List<TaskInfoGroupByLocationKey> taskInfoGroupByLocationKeys) {
                    listOfTaskInfoGroupByLocationKeys.clear();
                    listOfTaskInfoGroupByLocationKeys.addAll(taskInfoGroupByLocationKeys);
                    if (listOfTaskInfoGroupByLocationKeys.size() == 0) {
                        emptyTxt.setVisibility(View.VISIBLE);
                        confirm_dcButton.setVisibility(View.GONE);
                    } else {
                        emptyTxt.setVisibility(View.GONE);

                    }
                    pullData();
//                    listOfTaskInfoGroupByLocationKeys.addAll(taskInfoViewModel.getTaskInfoGroupByLocationKeys().getValue().);
//                    Log.d(TAG, "onChanged: " + taskInfoViewModel.getTaskInfoGroupByLocationKeys().getValue().size());
                    adapter.notifyDataSetChanged();
                }
            });
            taskInfoViewModel.getTaskInfoList().observe(getActivity(), new Observer<List<TaskInfoEntity>>() {
                @Override
                public void onChanged(List<TaskInfoEntity> taskInfoEntities) {
                    listOfTaskInfo.clear();
                    listOfTaskInfo.addAll(taskInfoEntities);
                    Collections.sort(listOfTaskInfo, new Comparator<TaskInfoEntity>() {
                        public int compare(TaskInfoEntity obj1, TaskInfoEntity obj2) {
                            // ## Ascending order
                            return Integer.valueOf(obj1.getSeqNo()).compareTo(obj2.getSeqNo());// To compare string values
                            // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values

                            // ## Descending order
                            // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                            // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                        }
                    });
                }
            });

//         !!  BACK UP CODE !!


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void showDialog() {
        Dialog dialog = new Dialog(getContext(), R.style.MyDialogTheme);
        dialog.setContentView(R.layout.login_check_dialog);
        Button closeButton = dialog.findViewById(R.id.closeButton);
        Button loginButton = dialog.findViewById(R.id.loginButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ChooseCompanyActivity.class));
            }
        });
        dialog.show();
    }

    private void pullData() {
        //mTaskInfoRepository.deleteAll();

        String jsonLoginResponse = PreferenceManager.getDefaultSharedPreferences(activity).getString(Constants.PREF_KEY_LOGIN_RESPONSE, "");
        LoginResponse loginResponse = new Gson().fromJson(jsonLoginResponse, LoginResponse.class);
        String token = loginResponse.getToken();
       /* Date date = new Date();
        String curDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        String selectedDate = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Constants.PREF_KEY_SELECTED_DATE, curDate);
       */
        String selectedDate = PreferenceManager.getDefaultSharedPreferences(activity).getString(Constants.PREF_KEY_SELECTED_DATE, "");

        String compId = "" + loginResponse.getDriverInfo().getCompanyId();
        // compId = "34";

        apiService.getTaskList(compId, selectedDate, Constants.AUTHORIZATION_TOKEN + token).enqueue(new Callback<TaskInfoResponse>() {
            @Override
            public void onResponse(Call<TaskInfoResponse> call, Response<TaskInfoResponse> response) {
                Log.d(TAG, "onResponse: ");
                if (response.code() == 200) {
                    Log.e(TAG, "size: AccessLITE " + response.body().getListOfTaskInfo().size());
                    taskInfoEntities = response.body().getListOfTaskInfo();
                    runInfoList = response.body().getListOfRunList();

                    if (runInfoList.size() > 0) {
                        getTaskByLocationKeys(0);
                        selectedRunTxt.setText("Selected Run - Run #" + runInfoList.get(0).getRunNo());
                        if (runInfoList.get(0).getRouteStarted() == 0) {
                            confirm_dcButton.setVisibility(View.VISIBLE);

                            for (int i = 0; i < taskInfoEntities.size(); i++) {
                                if (!taskInfoEntities.get(i).getWorkStatus().equals("pending")) {
                                    isPending = false;
                                    batchId = runInfoList.get(0).getBatchId();

                                    break;
                                }
                            }
                        }
                        if (runInfoList.get(0).getRouteStarted() == 1) {
                            confirm_dcButton.setVisibility(View.GONE);
                            for (int i = 0; i < taskInfoEntities.size(); i++) {
                                if (taskInfoEntities.get(i).getWorkStatus().equals("pending")) {
                                    isPending = true;
                                    batchId = runInfoList.get(0).getBatchId();

                                    break;
                                }

                            }
                        }
                    }
                    for (int index = 0; index < runInfoList.size(); index++) {
                        if (runInfoList.get(index).getRouteStarted() == 0) {
//                            confirm_dcButton.setVisibility(View.VISIBLE);

                            for (int i = 0; i < taskInfoEntities.size(); i++) {
                                if (!taskInfoEntities.get(i).getWorkStatus().equals("pending")) {
                                    isPending = false;
                                    batchId = runInfoList.get(0).getBatchId();

                                    break;
                                }
                            }
                        }
                        if (runInfoList.get(index).getRouteStarted() == 1) {
//                            confirm_dcButton.setVisibility(View.GONE);
                            for (int i = 0; i < taskInfoEntities.size(); i++) {
                                if (taskInfoEntities.get(i).getWorkStatus().equals("pending")) {
                                    isPending = true;
                                    batchId = runInfoList.get(0).getBatchId();
                                    break;
                                }

                            }
                        }
                    }
                    runTaskAdapter = new RunTaskAdapter(runInfoList, getContext(), 0);
                    runTaskAdapter.setOnItemClickListener(new RunTaskAdapter.OnItemClickListener() {
                        @Override
                        public void onGetRunPosition(int position) {
                            selectedRunTxt.setText("Selected Run - Run #" + runInfoList.get(position).getRunNo());
                            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            runTaskAdapter.updatePosition(position);
                            runTaskAdapter.notifyDataSetChanged();
                            getTaskByLocationKeys(position);

                            if (runInfoList.get(position).getRouteStarted() == 0) {
                                confirm_dcButton.setVisibility(View.VISIBLE);

                                for (int i = 0; i < taskInfoEntities.size(); i++) {
                                    if (!taskInfoEntities.get(i).getWorkStatus().equals("pending")) {
                                        isPending = false;
                                        batchId = runInfoList.get(position).getBatchId();

                                        break;
                                    }
                                }
                            }
                            if (runInfoList.get(position).getRouteStarted() == 1) {
                                confirm_dcButton.setVisibility(View.GONE);
                                for (int i = 0; i < taskInfoEntities.size(); i++) {
                                    if (taskInfoEntities.get(i).getWorkStatus().equals("pending")) {
                                        isPending = true;
                                        batchId = runInfoList.get(position).getBatchId();
                                        break;
                                    }
                                    if (!taskInfoEntities.get(i).getWorkStatus().equals("pending")) {
                                        isPending = false;
                                        batchId = runInfoList.get(position).getBatchId();
                                    }

                                }
                            }

//                            Back up code
//                            taskInfoViewModel.getTaskInfoGroupByBatchId(runInfoList.get(position).getBatchId()).observe(getActivity(), new Observer<List<TaskInfoGroupByLocationKey>>() {
//                                @Override
//                                public void onChanged(List<TaskInfoGroupByLocationKey> taskInfoGroupByLocationKeys) {
//                                    listOfTaskInfoGroupByLocationKeys.clear();
//                                    listOfTaskInfoGroupByLocationKeys.addAll(taskInfoGroupByLocationKeys);
//                                    Log.e(TAG, "onChanged: " + taskInfoGroupByLocationKeys.size());
//                                    if (listOfTaskInfoGroupByLocationKeys.size() == 0) {
//                                        emptyTxt.setVisibility(View.VISIBLE);
//                                    } else {
//                                        emptyTxt.setVisibility(View.VISIBLE);
//                                    }
////                    listOfTaskInfoGroupByLocationKeys.addAll(taskInfoViewModel.getTaskInfoGroupByLocationKeys().getValue().);
////                    Log.d(TAG, "onChanged: " + taskInfoViewModel.getTaskInfoGroupByLocationKeys().getValue().size());
//                                    adapter.notifyDataSetChanged();
//                                }
//                            });
                        }
                    });

                    listViewBtmSheet.setAdapter(runTaskAdapter);
//                    getTasks();
                } else {
                    Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<TaskInfoResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(getContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void showTimePicker() {
        dialog = new Dialog(getContext(), R.style.MyDialogTheme);
        dialog.setContentView(R.layout.time_picker_dialog);
        Button startDepartureButton = dialog.findViewById(R.id.startDepartureButton);
        TimePicker24Hours picker24Hours = dialog.findViewById(R.id.timePicker24);
        dialog.show();
        final String[] time = {""};
        time[0] = picker24Hours.getHour() + ":" + picker24Hours.getMinute() + ":00";
        Log.e(TAG, "onTime: " + time[0]);

        picker24Hours.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                time[0] = i + ":" + i1 + ":00";
                Log.e(TAG, "onTimeChanged: " + time[0]);

            }
        });
        startDepartureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDeparture(time[0]);
            }
        });
    }

    void getTaskByLocationKeys(int position) {
        taskInfoViewModel.getTaskInfoGroupByBatchId(runInfoList.get(position).getBatchId()).observe(activity, new Observer<List<TaskInfoGroupByLocationKey>>() {
            @Override
            public void onChanged(List<TaskInfoGroupByLocationKey> taskInfoGroupByLocationKeys) {
                listOfTaskInfoGroupByLocationKeys.clear();
                listOfTaskInfoGroupByLocationKeys.addAll(taskInfoGroupByLocationKeys);
                Log.e(TAG, "onChanged: " + taskInfoGroupByLocationKeys.size());
                if (listOfTaskInfoGroupByLocationKeys.size() == 0) {
                    emptyTxt.setVisibility(View.VISIBLE);
                } else {
                    emptyTxt.setVisibility(View.VISIBLE);
                }
//                    listOfTaskInfoGroupByLocationKeys.addAll(taskInfoViewModel.getTaskInfoGroupByLocationKeys().getValue().);
//                    Log.d(TAG, "onChanged: " + taskInfoViewModel.getTaskInfoGroupByLocationKeys().getValue().size());
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(String item, int pos) {
        Log.e(TAG, "onItemClick: ");
        bottomList.setText(item);
        taskInfoViewModel.getTaskInfoGroupByBatchId(listOfRunInfo.get(pos).getBatchId()).observe(getActivity(), new Observer<List<TaskInfoGroupByLocationKey>>() {
            @Override
            public void onChanged(List<TaskInfoGroupByLocationKey> taskInfoGroupByLocationKeys) {
                listOfTaskInfoGroupByLocationKeys.clear();
                listOfTaskInfoGroupByLocationKeys.addAll(taskInfoGroupByLocationKeys);
                if (listOfTaskInfoGroupByLocationKeys.size() == 0) {
                    emptyTxt.setVisibility(View.VISIBLE);
                } else {
                    emptyTxt.setVisibility(View.VISIBLE);
                }
//                    listOfTaskInfoGroupByLocationKeys.addAll(taskInfoViewModel.getTaskInfoGroupByLocationKeys().getValue().);
//                    Log.d(TAG, "onChanged: " + taskInfoViewModel.getTaskInfoGroupByLocationKeys().getValue().size());
                adapter.notifyDataSetChanged();
            }
        });
    }
}