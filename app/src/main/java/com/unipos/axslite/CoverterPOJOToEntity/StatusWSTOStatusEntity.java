package com.unipos.axslite.CoverterPOJOToEntity;


import com.unipos.axslite.Database.Entities.StatusEntity;
import com.unipos.axslite.POJO.ReasonWS;
import com.unipos.axslite.POJO.StatusWS;

public class StatusWSTOStatusEntity {
    public static StatusEntity convert(StatusWS statusWS) {
        return new StatusEntity(statusWS.getStatusId(),statusWS.getStatusName(),statusWS.getShipmentType(),statusWS.getStatusRule(),statusWS.getIsException());
    }
}
