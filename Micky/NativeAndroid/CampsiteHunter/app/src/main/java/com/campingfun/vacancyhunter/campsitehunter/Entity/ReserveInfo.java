package com.campingfun.vacancyhunter.campsitehunter.Entity;

import java.util.Date;

/**
 * Created by wayliu on 9/2/2014.
 */
public class ReserveInfo {
    @com.google.gson.annotations.SerializedName("handle")
    private String mHandle;

    public String getHandle() {
        return mHandle;
    }

    public final void setHandle(String handle) {
        mHandle = handle;
    }

    public String Id;
    public String ContractId;
    public String ParkId;
    public String ContractType;
    public Date DateInterested;
    public String Name;
}
