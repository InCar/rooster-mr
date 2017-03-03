package com.incarcloud.rooster.entity;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "t_car")
public class MobileyeSTD {
    private String vin;
    private Date tm;
    private String sound;
    private String daylight;
    private boolean isStopped;
    private float headway;
    private int headwayWarningLvl;
    private boolean headwayRepeateable;
    private boolean isLdwOff;
    private boolean ldwLeft;
    private boolean ldwRight;
    private boolean fcw;
    private boolean pedsFcw;
    private boolean pedsDz;
    private boolean tamperAlert;
    private boolean tsrEnabled;
    private int tsrLevel;
    private boolean maintenance;
    private boolean failSafe;
    private int errorCode;

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

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getDaylight() {
        return daylight;
    }

    public void setDaylight(String daylight) {
        this.daylight = daylight;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public void setStopped(boolean stopped) {
        isStopped = stopped;
    }

    public float getHeadway() {
        return headway;
    }

    public void setHeadway(float headway) {
        this.headway = headway;
    }

    public int getHeadwayWarningLvl() {
        return headwayWarningLvl;
    }

    public void setHeadwayWarningLvl(int headwayWarningLvl) {
        this.headwayWarningLvl = headwayWarningLvl;
    }

    public boolean isHeadwayRepeateable() {
        return headwayRepeateable;
    }

    public void setHeadwayRepeateable(boolean headwayRepeateable) {
        this.headwayRepeateable = headwayRepeateable;
    }

    public boolean isLdwOff() {
        return isLdwOff;
    }

    public void setLdwOff(boolean ldwOff) {
        isLdwOff = ldwOff;
    }

    public boolean isLdwLeft() {
        return ldwLeft;
    }

    public void setLdwLeft(boolean ldwLeft) {
        this.ldwLeft = ldwLeft;
    }

    public boolean isLdwRight() {
        return ldwRight;
    }

    public void setLdwRight(boolean ldwRight) {
        this.ldwRight = ldwRight;
    }

    public boolean isFcw() {
        return fcw;
    }

    public void setFcw(boolean fcw) {
        this.fcw = fcw;
    }

    public boolean isPedsFcw() {
        return pedsFcw;
    }

    public void setPedsFcw(boolean pedsFcw) {
        this.pedsFcw = pedsFcw;
    }

    public boolean isPedsDz() {
        return pedsDz;
    }

    public void setPedsDz(boolean pedsDz) {
        this.pedsDz = pedsDz;
    }

    public boolean isTamperAlert() {
        return tamperAlert;
    }

    public void setTamperAlert(boolean tamperAlert) {
        this.tamperAlert = tamperAlert;
    }

    public boolean isTsrEnabled() {
        return tsrEnabled;
    }

    public void setTsrEnabled(boolean tsrEnabled) {
        this.tsrEnabled = tsrEnabled;
    }

    public int getTsrLevel() {
        return tsrLevel;
    }

    public void setTsrLevel(int tsrLevel) {
        this.tsrLevel = tsrLevel;
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }

    public boolean isFailSafe() {
        return failSafe;
    }

    public void setFailSafe(boolean failSafe) {
        this.failSafe = failSafe;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
