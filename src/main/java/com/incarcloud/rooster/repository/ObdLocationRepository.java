package com.incarcloud.rooster.repository;

import com.incarcloud.rooster.entity.ObdLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * Created by Incar on 2017/3/2.
 */
public interface ObdLocationRepository extends JpaRepository<ObdLocation,Integer> {

    @Modifying
    @Query("delete from ObdLocation o where o.vin = ?1 and o.locationTime >= ?2 and o.locationTime <= ?3")
    void deleteByVinAndTime(String vin, Date dateBegin, Date dateEnd);
}
