package com.incarcloud.rooster.repository;

import com.incarcloud.rooster.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Incar on 2017/3/2.
 */
public interface CarRepository extends JpaRepository<Car,Integer> {

    Car findByLicense(String licence);
}
