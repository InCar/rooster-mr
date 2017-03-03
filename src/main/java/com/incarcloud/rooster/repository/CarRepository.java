package com.incarcloud.rooster.repository;

import com.incarcloud.rooster.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * Created by Incar on 2017/3/2.
 */
public interface CarRepository extends JpaRepository<Car,Integer> {

    Car findByLicense(String licence);
}
