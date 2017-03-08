package com.incarcloud.rooster.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Date;

@Embeddable
public class MobileyeTsrPK implements Serializable {
    private String vin;
    private Date tm;
    private int sn;

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Date getTm() {
        return tm;
    }

    public void setTm(Date tm) {
        this.tm = tm;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public boolean equals(Object target){
        if(target.getClass() != this.getClass()) return false;

        MobileyeTsrPK targetMPK = (MobileyeTsrPK)target;

        if(vin == null && tm == null)
            return (targetMPK.vin == null && targetMPK.tm == null && sn == targetMPK.sn);
        else if(vin == null)
            return tm.equals(targetMPK.tm) && sn == targetMPK.sn;
        else if(tm == null)
            return vin.equals(targetMPK.vin) && sn == targetMPK.sn;
        else
            return vin.equals(targetMPK.vin) && tm.equals(targetMPK.tm) && sn == targetMPK.sn;
    }

    public int hashCode(){
        return String.format("%s%s%d", vin==null?"0":vin, tm==null?"0":tm.toString(), sn).hashCode();
    }
}
