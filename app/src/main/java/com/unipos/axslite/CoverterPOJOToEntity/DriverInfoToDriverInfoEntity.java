package com.unipos.axslite.CoverterPOJOToEntity;

import com.unipos.axslite.Database.Entities.DriverInfoEntity;
import com.unipos.axslite.POJO.DriverInfo;

public class DriverInfoToDriverInfoEntity {

    public static DriverInfoEntity convertDriverInfoToEntity(DriverInfo driverInfo){
        DriverInfoEntity driverInfoEntity = new DriverInfoEntity(driverInfo.getImei(),driverInfo.getFirstName(),driverInfo.getLastName(),driverInfo.getOnDuty(),
                driverInfo.getCompanyId(),driverInfo.getCompanyName(),driverInfo.getEnableGPS(), driverInfo.getListOfAllowedCompanyInfo());
        return driverInfoEntity;
    }
}
