package com.incarcloud.rooster.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "t_mobileye_info")
public class MobileyeInfo {
    @EmbeddedId
    private MobileyePK pk;

    public MobileyePK getPk() {
        return pk;
    }

    public void setPk(MobileyePK pk) {
        this.pk = pk;
    }

    public boolean isBrakes() {
        return brakes;
    }

    public void setBrakes(boolean brakes) {
        this.brakes = brakes;
    }

    public boolean isBrake_left() {
        return brake_left;
    }

    public void setBrake_left(boolean brake_left) {
        this.brake_left = brake_left;
    }

    public boolean isBrake_right() {
        return brake_right;
    }

    public void setBrake_right(boolean brake_right) {
        this.brake_right = brake_right;
    }

    public boolean isWipers() {
        return wipers;
    }

    public void setWipers(boolean wipers) {
        this.wipers = wipers;
    }

    public boolean isBeam_low() {
        return beam_low;
    }

    public void setBeam_low(boolean beam_low) {
        this.beam_low = beam_low;
    }

    public boolean isBeam_high() {
        return beam_high;
    }

    public void setBeam_high(boolean beam_high) {
        this.beam_high = beam_high;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    private boolean brakes;
    private boolean brake_left;
    private boolean brake_right;
    private boolean wipers;
    private boolean beam_low;
    private boolean beam_high;
    private int speed;
}
