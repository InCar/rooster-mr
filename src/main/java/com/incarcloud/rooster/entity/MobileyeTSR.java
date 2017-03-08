package com.incarcloud.rooster.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "t_mobileye_tsr")
public class MobileyeTSR {
    @EmbeddedId
    private MobileyeTsrPK pk;

    private String flag;
    private int speed;
    private String flag2;
    private float x;
    private float y;
    private float z;
    private String filter;

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

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
