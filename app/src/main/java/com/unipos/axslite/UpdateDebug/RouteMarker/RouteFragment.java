package com.unipos.axslite.UpdateDebug.RouteMarker;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.unipos.axslite.Database.Entities.TaskInfoEntity;
import com.unipos.axslite.Database.Repository.TaskInfoRepository;
import com.unipos.axslite.R;

import java.util.ArrayList;
import java.util.List;

public class RouteFragment extends Fragment {

    Button showRoute, filterTaskList;
    Spinner routeSpinner;
    ArrayList<String> routeSelectionList = new ArrayList<>();
    ArrayList<String> stringArrayList = new ArrayList<>();
    private TaskInfoRepository mTaskInfoRepository;
    List<TaskInfoEntity> taskInfoEntities;

    public RouteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_route, container, false);
        initUI(view);

        mTaskInfoRepository = new TaskInfoRepository(getActivity().getApplication());
        taskInfoEntities = mTaskInfoRepository.getTaskInfos1();

        stringArrayList.add("    Run No. 1  ");
        routeSelectionList.add("    Show All   ");
        routeSelectionList.add("    Show DC   ");
        for (int i = 0; i < taskInfoEntities.size(); i++) {

            routeSelectionList.add(" " + (i + 1) + ". " + taskInfoEntities.get(i).getName());
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, stringArrayList);
        routeSpinner.setAdapter(arrayAdapter);
        showRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapRouteActivity.class);
                intent.putStringArrayListExtra("list", routeSelectionList);
//                Bundle bundle = new Bundle();
//                bundle.
                startActivity(intent);
            }
        });
        filterTaskList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        return view;
    }

    private void initUI(View view) {
        routeSpinner = view.findViewById(R.id.routeSpinner);
        showRoute = view.findViewById(R.id.showRoute);
        filterTaskList = view.findViewById(R.id.filterTaskList);
    }
}