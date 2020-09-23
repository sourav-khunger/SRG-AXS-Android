package com.unipos.axslite.Database.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.unipos.axslite.Database.Entities.ReasonEntity;
import com.unipos.axslite.Database.Entities.StatusEntity;
import com.unipos.axslite.Database.Repository.ShipmentStatusRepository;

import java.util.List;

public class ShipmentStatusViewModel extends AndroidViewModel {

    private ShipmentStatusRepository shipmentStatusRepository;

    public ShipmentStatusViewModel(@NonNull Application application) {
        super(application);
        shipmentStatusRepository = new ShipmentStatusRepository(application);
    }

    public LiveData<List<StatusEntity>> getStatusList(String shipmentType, int isException){return  shipmentStatusRepository.getStatusList(shipmentType, isException);}
    public LiveData<List<ReasonEntity>> getReasonList(int statusId){return  shipmentStatusRepository.getReasonList(statusId);}

    public void insertStatusEntity (StatusEntity statusEntity){
        shipmentStatusRepository.insert(statusEntity);
    }

    public void insertReasonEntity (ReasonEntity reasonEntity){
        shipmentStatusRepository.insert(reasonEntity);
    }

    public void deleteAll (){
        shipmentStatusRepository.deleteAll();
    }
}
