package com.unipos.axslite.UpdateDebug;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.unipos.axslite.Activity.ChooseCompanyActivity;
import com.unipos.axslite.Activity.ScreenSlidePagerActivity;
import com.unipos.axslite.R;


public class ReturnScanFragment extends Fragment {

    ImageView scanButton;
    Button viewComButton,loginlogoutButton;

    public ReturnScanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_return_scan, container, false);
        scanButton = view.findViewById(R.id.scanButton);
        loginlogoutButton = view.findViewById(R.id.loginlogoutButton);
        viewComButton = view.findViewById(R.id.viewComButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ScannerActivity.class));
            }
        });
        loginlogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ChooseCompanyActivity.class));
            }
        });
        return view;
    }
}