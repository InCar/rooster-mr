package com.incarcloud.rooster.entity;

import javax.persistence.*;
import java.io.*;
import java.util.*;

@Embeddable
public class MobileyePK implements Serializable {
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

    protected String vin;
    protected Date tm;

    public boolean equals(Object target){
        if(target.getClass() != this.getClass()) return false;

        MobileyePK targetMPK = (MobileyePK)target;

        if(vin == null && tm == null)
            return (targetMPK.vin == null && targetMPK.tm == null);
        else if(vin == null)
            return tm.equals(targetMPK.tm);
        else if(tm == null)
            return vin.equals(targetMPK.vin);
        else
            return vin.equals(targetMPK.vin) && tm.equals(targetMPK.tm);
    }

    public int hashCode(){
        return String.format("%s%s", vin==null?"0":vin, tm==null?"0":tm.toString()).hashCode();
    }
}
