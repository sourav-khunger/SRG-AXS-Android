package com.unipos.axslite.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.unipos.axslite.Activity.ShowListOfTaskGroupByLocationKeyActivity;
import com.unipos.axslite.Adapter.ToDoTaskListAdapter;
import com.unipos.axslite.Database.Entities.TaskInfoEntity;
import com.unipos.axslite.Database.Repository.TaskInfoRepository;
import com.unipos.axslite.Database.ViewModel.TaskInfoViewModel;
import com.unipos.axslite.POJO.LoginResponse;
import com.unipos.axslite.POJO.TaskInfoGroupByLocationKey;
import com.unipos.axslite.R;
import com.unipos.axslite.Utils.Constants;
import com.unipos.axslite.here.turn.HereTurnNavActivity;

import java.util.ArrayList;
import java.util.List;


public class ToDoTaskListFragment extends Fragment {

    public static final String TO_DO_TASK_LIST_FRAGMENT_TITLE = "To Do";
    List<TaskInfoEntity> listOfTaskInfo = new ArrayList<TaskInfoEntity>();
    List<TaskInfoGroupByLocationKey> listOfTaskInfoGroupByLocationKeys = new ArrayList<TaskInfoGroupByLocationKey>();
    private String TAG = "ToDoTaskListFragment";
    private TaskInfoViewModel taskInfoViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ToDoTaskListAdapter adapter;
    private TaskInfoRepository mTaskInfoRepository;
    List<TaskInfoEntity> taskInfoEntities;

    public ToDoTaskListFragment() {

    }

    public static ToDoTaskListFragment newInstance(String param1, String param2) {
        ToDoTaskListFragment fragment = new ToDoTaskListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_do_task_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.to_do_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mTaskInfoRepository = new TaskInfoRepository(getActivity().getApplication());
        for (int i = 0; i < listOfTaskInfoGroupByLocationKeys.size(); i++) {


        }
        taskInfoEntities = mTaskInfoRepository.getTaskInfos1();
        adapter = new ToDoTaskListAdapter(listOfTaskInfoGroupByLocationKeys, getActivity().getApplication(), taskInfoEntities);

        adapter.setOnItemClickListener(new ToDoTaskListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                TaskInfoGroupByLocationKey taskInfoGroupByLocationKey = listOfTaskInfoGroupByLocationKeys.get(position);
                String taskInfoString = new Gson().toJson(taskInfoGroupByLocationKey);
                TaskInfoEntity taskInfo = taskInfoEntities.get(position);
                String taskInfoStr = new Gson().toJson(taskInfo);
                Log.e(TAG, "taskInfo: " + taskInfoStr);
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                        .putString(Constants.SELECTED_LOCATION, taskInfoString)
                        .apply();
                /*PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                        .putString(Constants.SELECTED_TASK, taskInfoString)
                        .apply();*/

                if (taskInfoGroupByLocationKey.getArrivalTime() == null || taskInfoGroupByLocationKey.getArrivalTime().equals("")) {
                    Intent intent = new Intent(getActivity(), HereTurnNavActivity.class);
                    intent.putExtra("task", taskInfoStr);
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(getActivity(), ShowListOfTaskGroupByLocationKeyActivity.class);
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);
        taskInfoViewModel = new ViewModelProvider(this).get(TaskInfoViewModel.class);
        try {
            String jsonLoginResponse = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Constants.PREF_KEY_LOGIN_RESPONSE, "");
            Log.d(TAG, "onCreateView: " + jsonLoginResponse);
            LoginResponse loginResponse = new Gson().fromJson(jsonLoginResponse, LoginResponse.class);

            /*taskInfoViewModel.getTaskInfoList().observe(getActivity(), new Observer<List<TaskInfoEntity>>() {
                @Override
                public void onChanged(List<TaskInfoEntity> taskInfoEntities) {
                    listOfTaskInfo.clear();
                    listOfTaskInfo.addAll(taskInfoEntities);

                }
            });*/

            taskInfoViewModel.getTaskInfoGroupByLocationKeys().observe(getActivity(), new Observer<List<TaskInfoGroupByLocationKey>>() {
                @Override
                public void onChanged(List<TaskInfoGroupByLocationKey> taskInfoGroupByLocationKeys) {
                    listOfTaskInfoGroupByLocationKeys.clear();
                    listOfTaskInfoGroupByLocationKeys.addAll(taskInfoGroupByLocationKeys);
//                    listOfTaskInfoGroupByLocationKeys.addAll(taskInfoViewModel.getTaskInfoGroupByLocationKeys().getValue().);
//                    Log.d(TAG, "onChanged: " + taskInfoViewModel.getTaskInfoGroupByLocationKeys().getValue().size());
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }
}