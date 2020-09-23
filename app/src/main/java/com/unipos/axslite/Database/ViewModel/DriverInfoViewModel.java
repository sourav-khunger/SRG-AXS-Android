package com.unipos.axslite.Database.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unipos.axslite.Database.Entities.DriverInfoEntity;
import com.unipos.axslite.Database.Repository.DriverInfoRepository;

import java.util.List;

public class DriverInfoViewModel extends AndroidViewModel {

    private DriverInfoRepository driverInfoRepository;
    // private MutableLiveData<List<DriverInfoEntity>> driverInfos;


    public DriverInfoViewModel(@NonNull Application application) {
        super(application);
        driverInfoRepository = new DriverInfoRepository(application);
    }

    public LiveData<List<DriverInfoEntity>> getDriverInfos(){return  driverInfoRepository.getDriverInfos();}

    public void insertDriverInfoEntity (DriverInfoEntity driverInfoEntity){
        driverInfoRepository.insert(driverInfoEntity);
    }
    public void deleteAll (){
        driverInfoRepository.deleteAll();
    }

}
