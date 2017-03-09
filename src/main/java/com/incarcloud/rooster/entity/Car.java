package com.incarcloud.rooster.entity;

import javax.persistence.*;

/**
 * Created by Incar on 2017/3/2.
 */
@Entity
@Table(name = "t_car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "s4_id")
    private int s4Id;
    private String license;
    private String obdCode;
    private String vinCode;

    public Car() {
    }

    public Car(int s4Id, String license, String obdCode) {
        this.s4Id = s4Id;
        this.license = license;
        this.obdCode = obdCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getS4Id() {
        return s4Id;
    }

    public void setS4Id(int s4Id) {
        this.s4Id = s4Id;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getObdCode() {
        return obdCode;
    }

    public void setObdCode(String obdCode) {
        this.obdCode = obdCode;
    }

    public String getVinCode() {
        return vinCode;
    }

    public void setVinCode(String vinCode) {
        this.vinCode = vinCode;
    }
}
