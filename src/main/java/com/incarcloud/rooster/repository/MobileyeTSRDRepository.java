package com.incarcloud.rooster.repository;

import com.incarcloud.rooster.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public interface MobileyeTSRDRepository extends JpaRepository<MobileyeTSRD,Integer> {

    @Modifying
    @Transactional
    @Query("delete from MobileyeTSRD where vin = ?1 and tm >= ?2 and tm <= ?3")
    void deleteByVinAndTime(String vin, Date dateBegin, Date dateEnd);
}
