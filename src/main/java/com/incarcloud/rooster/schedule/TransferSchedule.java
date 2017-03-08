package com.incarcloud.rooster.schedule;

import com.incarcloud.rooster.utils.DateUtil;
import com.incarcloud.rooster.vehicle.VehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static Logger s_logger = LoggerFactory.getLogger(TransferSchedule.class);

    @Scheduled(fixedRate = 1000 * 60 * 5)   // 从程序启动开始，每5分钟执行一次
    public void transferAllVehicle()
    {
        Date dateEnd = new Date();
        Date dateBegin = DateUtil.plusDays(dateEnd,-1);

        s_logger.info("\n-----schedule: {} -> {} -----", dateBegin, dateEnd);
        vehicleService.transferAllVehicle(dateBegin,dateEnd);
        s_logger.info("----- schedule end -----");
    }
}
