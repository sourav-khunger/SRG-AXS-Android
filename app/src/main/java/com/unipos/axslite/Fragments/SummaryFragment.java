package com.unipos.axslite.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.phappytech.library.CustomSeekBar;
import com.phappytech.library.ProgressSegment;
import com.unipos.axslite.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SummaryFragment extends Fragment {

    public static final String SUMMARY_FRAGMENT_TITLE = "Summary";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<ProgressSegment> progressSegments = new ArrayList<>();

    public SummaryFragment() {
        // Required empty public constructor
    }

    public static SummaryFragment newInstance(String param1, String param2) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        CustomSeekBar customSeekBar = view.findViewById(R.id.custom);
        getSummaryData(0);
        customSeekBar.setProgressSegments(progressSegments);
        return view;
    }

    void getSummaryData(int i) {
        int[] colors = {Color.GRAY, Color.RED, Color.WHITE};
//        for (int i = 0; i < 3; i++) {
            ProgressSegment progressSegment = new ProgressSegment(Parcel.obtain());
            progressSegment.name = "progress" + i;
            if (i == 0) {
                progressSegment.progress = (i + 1) * 10;
            }
            if (i == 1) {
//                progressSegment.progress = (i + 0) * 10;
            }
            if (i == 2) {
                progressSegment.progress = (i + 12) * 10;
            }
            progressSegment.color = colors[i];
            progressSegments.add(progressSegment);
//        }
    }
}