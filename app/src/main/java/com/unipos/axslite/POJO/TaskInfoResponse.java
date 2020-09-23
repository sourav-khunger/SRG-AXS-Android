package com.unipos.axslite.POJO;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class TaskInfoResponse implements Serializable {

    @SerializedName(value = "status")
    private String status;

    @SerializedName(value = "taskList")
    private ArrayList<TaskInfo> listOfTaskInfo;

    public TaskInfoResponse() {

    }

    public TaskInfoResponse(String status, ArrayList<TaskInfo> listOfTaskInfo) {
        this.status = status;
        this.listOfTaskInfo = listOfTaskInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<TaskInfo> getListOfTaskInfo() {
        return listOfTaskInfo;
    }

    public void setListOfTaskInfo(ArrayList<TaskInfo> listOfTaskInfo) {
        this.listOfTaskInfo = listOfTaskInfo;
    }
}
