package com.incarcloud.rooster.vehicle;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.*;
import com.incarcloud.rooster.entity.Car;
import com.incarcloud.rooster.repository.CarRepository;
import com.incarcloud.rooster.telemetry.TelemetryFlag;
import com.incarcloud.rooster.telemetry.TelemetryService;
import com.incarcloud.rooster.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Incar on 2017/3/2.
 */
@Service
public class VehicleService {

    @Autowired
    TelemetryService telemetryService;

    @Autowired
    CarRepository carRepository;

    @Value("${aliyun.accessKeyId}")
    String accessKeyId;

    @Value("${aliyun.accessKeySecret}")
    String accessKeySecret;

    @Value("${aliyun.OTS.endpoint}")
    String otsEndPoint;

    @Value("${aliyun.OTS.instance}")
    String otsInstance;

    private static String TABLE_NAME = "vehicle";
    private static String PRIMARY_KEY_NAME = "key";
    private static String TELEMETRY_POS_TYPE = "TriAdas.Pos####";

    private static Logger s_logger = LoggerFactory.getLogger(VehicleService.class);

    /**
     * 转存车辆位置数据
     * @param dateBegin 日期范围起始
     * @param dateEnd 日期范围结束
     */
    public void transferAllVehicle(Date dateBegin, Date dateEnd)
    {
        SyncClient client = new SyncClient(otsEndPoint, accessKeyId, accessKeySecret, otsInstance);

        String startPkValue = "0000";
        String endPkValue = "zzzz";

        RangeRowQueryCriteria rangeRowQueryCriteria = new RangeRowQueryCriteria(TABLE_NAME);
        // 设置起始主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKeyBuilder.addPrimaryKeyColumn(PRIMARY_KEY_NAME, PrimaryKeyValue.fromString(startPkValue));
        rangeRowQueryCriteria.setInclusiveStartPrimaryKey(primaryKeyBuilder.build());
        // 设置结束主键
        primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKeyBuilder.addPrimaryKeyColumn(PRIMARY_KEY_NAME, PrimaryKeyValue.fromString(endPkValue));
        rangeRowQueryCriteria.setExclusiveEndPrimaryKey(primaryKeyBuilder.build());
        rangeRowQueryCriteria.setMaxVersions(1);
        while (true) {
            GetRangeResponse getRangeResponse = client.getRange(new GetRangeRequest(rangeRowQueryCriteria));
            for (Row row : getRangeResponse.getRows()) {
                String key = row.getPrimaryKey().getPrimaryKeyColumn(PRIMARY_KEY_NAME).getValue().asString();
                s_logger.info("Vehicle Key: {}", key);

                transferVehicle(key);                                     // 转存单个车辆信息
                deleteVehicleData(key,dateBegin,dateEnd);                 // 删除单个车辆数据
                transferVehiclePos(client,key,dateBegin,dateEnd);         // 转存单个车辆位置数据
                transferVehicleMobileye(client, key, dateBegin, dateEnd); // 转存单个车辆Mobileye数据

            }
            // 若nextStartPrimaryKey不为null, 则继续读取.
            if (getRangeResponse.getNextStartPrimaryKey() != null) {
                rangeRowQueryCriteria.setInclusiveStartPrimaryKey(getRangeResponse.getNextStartPrimaryKey());
            } else {
                break;
            }
        }
    }

    /**
     * 转存车辆信息
     * @param key 车辆key
     */
    public void transferVehicle(String key)
    {
        int s4Id = 961;
        String vin = key.substring(4,21);
        String license = vin;

        Car checkResult = carRepository.findByLicense(license);
        if(checkResult==null)
        {
            Car car = new Car(s4Id,license,vin);
            carRepository.save(car);
        }
    }

    /**
     * 删除整天的数据
     * @param key
     */
    public void deleteVehicleData(String key, Date dateBegin, Date dateEnd)
    {
        String vin = key.substring(4,21);
        telemetryService.deleteByVinAndTime(vin,dateBegin,dateEnd);
    }

    /**
     * 转存单个车辆位置数据
     * @param key 车辆key
     * @param dateBegin 日期范围起始
     * @param dateEnd 日期范围结束
     */
    public void transferVehiclePos(SyncClient client, String key, Date dateBegin, Date dateEnd)
    {
        String startPkValue = key + TELEMETRY_POS_TYPE + DateUtil.getDateStr(dateBegin,"yyyyMMddHHmmss");
        String endPkValue = key + TELEMETRY_POS_TYPE + DateUtil.getDateStr(dateEnd,"yyyyMMddHHmmss");
        telemetryService.transferTelemetry(client,startPkValue,endPkValue, TelemetryFlag.Position);
    }

    public void transferVehicleMobileye(SyncClient client, String key, Date dateBegin, Date dateEnd){
        final String TELEMETRY_MOBILEYE = "TriAdas.MobiEye";

        String startPkValue = key + TELEMETRY_MOBILEYE + DateUtil.getDateStr(dateBegin,"yyyyMMddHHmmss");
        String endPkValue = key + TELEMETRY_MOBILEYE + DateUtil.getDateStr(dateEnd,"yyyyMMddHHmmss");
        telemetryService.transferTelemetry(client,startPkValue,endPkValue, TelemetryFlag.Mobileye);
    }
}
