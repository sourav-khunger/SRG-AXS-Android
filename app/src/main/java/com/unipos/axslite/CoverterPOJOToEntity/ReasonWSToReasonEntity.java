package com.unipos.axslite.CoverterPOJOToEntity;

import com.unipos.axslite.Database.Entities.ReasonEntity;
import com.unipos.axslite.POJO.ReasonWS;

public class ReasonWSToReasonEntity {

    public static ReasonEntity convert(ReasonWS reasonWS) {
        return new ReasonEntity(reasonWS.getReasonId(), reasonWS.getStatusId(), reasonWS.getReasonName(), reasonWS.getReasonRule());
    }
}
