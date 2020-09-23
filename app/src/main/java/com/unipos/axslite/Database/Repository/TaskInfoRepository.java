package com.unipos.axslite.Database.Repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Query;

import com.unipos.axslite.Database.DAO.TaskInfoDAO;
import com.unipos.axslite.Database.Database;
import com.unipos.axslite.Database.Entities.TaskInfoEntity;
import com.unipos.axslite.POJO.TaskInfoGroupByLocationKey;
import com.unipos.axslite.Utils.Constants;

import java.util.List;

public class TaskInfoRepository {
    private TaskInfoDAO taskInfoDAO;

    public TaskInfoRepository(Application application){
        Database db = Database.getDatabase(application);
        taskInfoDAO = db.taskInfoDAO();
    }

    public LiveData<List<TaskInfoEntity>> getTaskInfos(){
        return taskInfoDAO.getTaskInfoList();
    }
    public List<TaskInfoEntity> getTaskInfos1(){
        return taskInfoDAO.getTaskInfoList1();
    }

    public TaskInfoEntity getTaskInfoWithId(String taskid){
        return taskInfoDAO.getTaskInfo(taskid);
    }

    public LiveData<List<TaskInfoGroupByLocationKey>> getTaskInfoGroupByLocationKeys(){
        return taskInfoDAO.getTaskInfoGroupByLocationKeys();
    }

    public LiveData<List<TaskInfoGroupByLocationKey>> getTaskInfoSearchByWorkStatusGroupByLocationKeys(String workStatus){
        return taskInfoDAO.getTaskInfoSearchByWorkStatusGroupByLocationKeys(workStatus);
    }
    public LiveData<List<TaskInfoEntity>> getTaskInfoByLocationKey(String locationKey){
        Log.d("repository", "getTaskInfoByLocationKey: " + locationKey);
        return taskInfoDAO.getTaskInfoByLocationKey(locationKey);
    }
    public void insert (final TaskInfoEntity ... taskInfoEntities){
        Database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                taskInfoDAO.insert(taskInfoEntities);
            }
        });
    }

    public void update (final TaskInfoEntity... taskInfoEntities){
        Database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                taskInfoDAO.updateTaskInfo(taskInfoEntities);
            }
        });

    }

    public void updateLocation (final String locationKey, final String arrivalTime){
        Database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                taskInfoDAO.updateLocation(locationKey, arrivalTime);
            }
        });

    }

    public void delete (final TaskInfoEntity ... taskInfoEntities){
        Database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                taskInfoDAO.deleteTaskInfos(taskInfoEntities);
            }
        });
    }

    public void deleteAll (){
        Database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                taskInfoDAO.deleteAll();
            }
        });
    }

    public List<TaskInfoEntity> getTaskInfoByRecordStatus(int recordStatus){
        return taskInfoDAO.getTaskInfoByRecordStatus(recordStatus);
    }

    public List<TaskInfoEntity> getTaskInfoCompleted(String locationKey){
        return taskInfoDAO.getTaskInfoCompleted(locationKey, Constants.TASK_INFO_WORK_STATUS_COMPLETED);
    }

    public boolean isEmptyTask(){
        return taskInfoDAO.getTaskInfoLimit1().isEmpty();
    }
}
