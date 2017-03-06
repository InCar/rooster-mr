package com.incarcloud.rooster.schedule;

import com.incarcloud.rooster.utils.DateUtil;
import com.incarcloud.rooster.vehicle.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Incar on 2017/2/5.
 */
@Component
@Configurable
@EnableScheduling
public class TransferSchedule {

    @Autowired
    VehicleService vehicleService;

    @Scheduled(fixedRate = 1000 * 60 * 5)   // 从程序启动开始，每5分钟执行一次
    public void transferAllVehicle()
    {
        Date dateEnd = new Date();
        Date dateBegin = DateUtil.plusDays(dateEnd,-1);
        vehicleService.transferAllVehicle(dateBegin,dateEnd);
    }
}
