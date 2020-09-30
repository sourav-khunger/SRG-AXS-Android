package com.unipos.axslite.ui.shipment;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.unipos.axslite.Activity.ShipmentActivity;
import com.unipos.axslite.Database.Entities.ReasonEntity;
import com.unipos.axslite.Database.Entities.StatusEntity;
import com.unipos.axslite.Database.Entities.TaskInfoEntity;
import com.unipos.axslite.Database.Repository.ShipmentStatusRepository;
import com.unipos.axslite.Database.Repository.TaskInfoRepository;
import com.unipos.axslite.R;
import com.unipos.axslite.Utils.Constants;
import com.unipos.axslite.ui.shipment.ViewModel.ShipmentDeliveredToViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ShipmentDeliveredToFragment extends Fragment {

    private ShipmentDeliveredToViewModel mViewModel;
    private Button finishBtn;

    TextView deliverred_to_name_text, deliverred_to_address_text, ShipmentHist;

    public static ShipmentDeliveredToFragment newInstance() {
        return new ShipmentDeliveredToFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.shipment_delivered_to_fragment, container, false);

        finishBtn = root.findViewById(R.id.finish_btn);
        deliverred_to_name_text = root.findViewById(R.id.deliverred_to_name_text);
        deliverred_to_address_text = root.findViewById(R.id.deliverred_to_address_text);
        ShipmentHist = root.findViewById(R.id.ShipmentHist);


        TaskInfoEntity thetask = ShipmentActivity.selectedTask;
        deliverred_to_name_text.setText(thetask.getName());
        deliverred_to_address_text.setText(thetask.getAddress());

        ShipmentStatusRepository shipmentStatusRepository = new ShipmentStatusRepository(getActivity().getApplication());

        StatusEntity status = shipmentStatusRepository.getStatus(ShipmentActivity.selectedTask.getStatusId());
        ReasonEntity reason = shipmentStatusRepository.getReason(ShipmentActivity.selectedTask.getReasonId());

        String shAllInfo = "";
        if (status != null) shAllInfo += "\n" + status.getStatusName();
        if (reason != null) shAllInfo += "\n" + reason.getReasonName();

        String shipHistTask = shAllInfo + "\nProbil # " + thetask.getBarcode();
        shipHistTask += "\nReff # " + thetask.getReffNo();
        shipHistTask += "\nQty Entered : " + thetask.getQtyEntered();
        shipHistTask += "\nComments \n " + thetask.getDriverComment();

        ShipmentHist.setText(shipHistTask);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }

    private void save() {
        Log.d("finish", ShipmentActivity.selectedTask.toString());

        TaskInfoRepository mTaskInfoRepository = new TaskInfoRepository((Application) getActivity().getApplicationContext());
        Date date = new Date();
        String curDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        String status = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(Constants.DELIVERY_STATUS, "");
        if (status.equals("")) {
            ShipmentActivity.selectedTask.setWorkStatus("pending");

        } else {
            ShipmentActivity.selectedTask.setWorkStatus(status);
        }
        ShipmentActivity.selectedTask.setCod(0);
        ShipmentActivity.selectedTask.setCompleteTime(curDateTime);
        ShipmentActivity.selectedTask.setRecordStatus(2);
        mTaskInfoRepository.update(ShipmentActivity.selectedTask);
        Intent intent = new Intent(Constants.COMPLETED_CHECK_RECVER);
        getActivity().sendBroadcast(intent);
        getActivity().finish();
    }
}