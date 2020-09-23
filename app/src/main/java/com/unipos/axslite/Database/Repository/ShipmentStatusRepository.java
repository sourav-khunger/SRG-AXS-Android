package com.unipos.axslite.Database.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.unipos.axslite.Database.DAO.ReasonDAO;
import com.unipos.axslite.Database.DAO.StatusDAO;
import com.unipos.axslite.Database.Database;
import com.unipos.axslite.Database.Entities.DriverInfoEntity;
import com.unipos.axslite.Database.Entities.ReasonEntity;
import com.unipos.axslite.Database.Entities.StatusEntity;
import com.unipos.axslite.Database.Entities.TaskInfoEntity;

import java.util.List;

public class ShipmentStatusRepository {

    Database database;
    private StatusDAO statusDAO;
    private ReasonDAO reasonDAO;
    private LiveData<List<StatusEntity>> statusList;
    private LiveData<List<ReasonEntity>> reasonList;

    public  ShipmentStatusRepository(Application application) {
        database = Database.getDatabase(application);
        statusDAO = database.statusDAO();
        reasonDAO = database.reasonDAO();
    }

    public LiveData<List<StatusEntity>> getStatusList(String shipmentType, int isException) {
        statusList = statusDAO.getStatusList(shipmentType, isException);
        return statusList;
    }

    public LiveData<List<ReasonEntity>> getReasonList(int statusId) {
        reasonList = reasonDAO.getReasonList(statusId);
        return reasonList;
    }

    public ReasonEntity getReason(int reasonId) {
        return reasonDAO.getReason(reasonId);
    }

    public StatusEntity getStatus(int statusId) {
        return statusDAO.getStatus(statusId);
    }

    public void insert (final StatusEntity statusEntity){
        Database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                statusDAO.insert(statusEntity);
            }
        });
    }

    public void insert (final ReasonEntity reasonEntity){
        Database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                reasonDAO.insert(reasonEntity);
            }
        });
    }

    public void deleteAll (){
        Database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                reasonDAO.deleteAllReasons();
                statusDAO.deleteAllStatuses();
            }
        });
    }
}