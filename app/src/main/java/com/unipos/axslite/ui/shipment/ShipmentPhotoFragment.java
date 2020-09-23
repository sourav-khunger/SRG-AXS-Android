package com.unipos.axslite.ui.shipment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;
import com.unipos.axslite.Activity.CameraKitActivity;
import com.unipos.axslite.Activity.PreviewImgActivity;
import com.unipos.axslite.Activity.ShipmentActivity;
import com.unipos.axslite.POJO.LoginResponse;
import com.unipos.axslite.R;
import com.unipos.axslite.Utils.Constants;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShipmentPhotoFragment extends Fragment {

    private CameraView cameraView;
    private Button captureBtn;
    // private ShipmentPhotoViewModel mViewModel;
    View root;

    public static ShipmentPhotoFragment newInstance() {
        return new ShipmentPhotoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_camkit, container, false);


        //mViewModel = ViewModelProviders.of(this).get(ShipmentPhotoViewModel.class);

        cameraView = (CameraView) root.findViewById(R.id.camera);
        captureBtn = (Button) root.findViewById(R.id.captureBtn);

        //cameraView.setJpegQuality(50);
        cameraView.setJpegQuality(20);
        cameraView.setFocus(CameraKit.Constants.FOCUS_TAP);
        cameraView.setFlash(CameraKit.Constants.FLASH_AUTO);
        cameraView.setMethod(CameraKit.Constants.METHOD_STILL);

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                Bitmap bmap = cameraKitImage.getBitmap();
                ShipmentPhotoPreviewFragment.bitmap = bmap;

                Navigation.findNavController(root).navigate(R.id.action_shipmentPhotoFragment_to_shipmentPhotoPreviewFragment);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cameraView.captureImage();
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cameraView.start();
    }
}