package com.unipos.axslite.CoverterPOJOToEntity;

import com.unipos.axslite.Database.Entities.RunInfoEntity;
import com.unipos.axslite.Database.Entities.TaskInfoEntity;
import com.unipos.axslite.POJO.RunInfo;
import com.unipos.axslite.POJO.TaskInfo;

public class RunInfoToRunInfoEntity {

    public static RunInfoEntity convertRunInfoToRunInfoEntity(RunInfo runInfo) {
        RunInfoEntity runInfoEntity = new RunInfoEntity(runInfo.getBatchId(), runInfo.getParcelCounts(), runInfo.getRouteStarted(), runInfo.getRunNo());
        return runInfoEntity;

    }

}