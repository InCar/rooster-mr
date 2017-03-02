package com.incarcloud.rooster.controller;

import com.incarcloud.rooster.utils.DateUtil;
import com.incarcloud.rooster.vehicle.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Created by Incar on 2017/3/2.
 */
@RestController
@RequestMapping("/api/vehicle")
public class VehicleController {

    @Autowired
    VehicleService vehicleService;

    /**
     * 车辆位置信息转存
     * @param dateBeginStr 时间范围开始（时间格式：yyyyMMdd）
     * @param dateEndStr 时间范围结束（时间格式：yyyyMMdd）
     */
    @RequestMapping(value = "/transfer", method = RequestMethod.GET)
    public void transferAllVehiclePos(@RequestParam(value = "dateBegin", required = true) String dateBeginStr,
                                      @RequestParam(value = "dateEnd", required = true) String dateEndStr)
    {
        Date dateBegin = DateUtil.parseStrToDate(dateBeginStr,"yyyyMMdd");
        Date dateEnd = DateUtil.parseStrToDate(dateEndStr,"yyyyMMdd");
        vehicleService.transferAllVehiclePos(dateBegin,dateEnd);
    }
}
