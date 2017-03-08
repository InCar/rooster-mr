package com.incarcloud.rooster.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "t_mobileye_tsrd")
public class MobileyeTSRD {
    @EmbeddedId
    private MobileyeTsrPK pk;

    private String flag;
    private int speed;
    private String flag2;

    public MobileyeTsrPK getPk() {
        return pk;
    }

    public void setPk(MobileyeTsrPK pk) {
        this.pk = pk;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getFlag2() {
        return flag2;
    }

    public void setFlag2(String flag2) {
        this.flag2 = flag2;
    }
}
