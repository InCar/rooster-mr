package com.incarcloud.rooster.vehicle;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.*;
import com.incarcloud.rooster.telemetry.TelemetryService;
import com.incarcloud.rooster.utils.DateUtil;
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


    /**
     * 转存车辆位置数据
     * @param dateBegin 日期范围起始
     * @param dateEnd 日期范围结束
     */
    public void transferAllVehiclePos(Date dateBegin, Date dateEnd)
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
                System.out.println("Vehicle Row: " + row);
                String key = row.getPrimaryKey().getPrimaryKeyColumn(PRIMARY_KEY_NAME).getValue().asString();
                System.out.println("Vehicle Key: " + key);
                transferVehiclePos(client,key,dateBegin,dateEnd);    // 转存单个车辆位置数据
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
     * 转存单个车辆位置数据
     * @param key 车辆key
     * @param dateBegin 日期范围起始
     * @param dateEnd 日期范围结束
     */
    public void transferVehiclePos(SyncClient client, String key, Date dateBegin, Date dateEnd)
    {
        String startPkValue = key + TELEMETRY_POS_TYPE + DateUtil.getDateStr(dateBegin,"yyyyMMddHHmmss");
        String endPkValue = key + TELEMETRY_POS_TYPE + DateUtil.getDateStr(dateEnd,"yyyyMMddHHmmss");
        telemetryService.transferPosTelemetry(client,startPkValue,endPkValue);
    }
}
