package com.unipos.axslite.UpdateDebug.RouteMarker;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.unipos.axslite.R;

import java.util.ArrayList;

public class RouteFragment extends Fragment {

    Button showRoute;
    Spinner routeSpinner;
    ArrayList<String> routeSelectionList = new ArrayList<>();

    public RouteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_route, container, false);
        initUI(view);
        showRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MapRouteActivity.class));
            }
        });
        return view;
    }

    private void initUI(View view) {
        showRoute = view.findViewById(R.id.showRoute);
    }
}