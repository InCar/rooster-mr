package com.incarcloud.rooster.telemetry;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.*;
import com.incarcloud.rooster.entity.ObdLocation;
import com.incarcloud.rooster.repository.ObdLocationRepository;
import com.incarcloud.rooster.utils.DateUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;


/**
 * Created by Incar on 2017/3/2.
 */
@Service
public class TelemetryService {

    @Autowired
    ObdLocationRepository obdLocationRepository;

    private static String TABLE_NAME = "telemetry";
    private static String PRIMARY_KEY_NAME = "key";

    /**
     * 转存单个车辆位置数据
     * @param client 表格存储客户端
     * @param startPkValue 主键范围起始
     * @param endPkValue 主键范围结束
     */
    public void transferPosTelemetry(SyncClient client, String startPkValue, String endPkValue)
    {
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
                System.out.println("Telemetry Row: " + row);
                String key = row.getPrimaryKey().getPrimaryKeyColumn(PRIMARY_KEY_NAME).getValue().asString();
                String data = row.getLatestColumn("data").getValue().asString();
                System.out.println("Telemetry Data: " + data);
                transferOnePosTelemetry(key,data);   // 转存单条位置数据
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
     * 转存单条位置数据
     * @param data 位置数据
     */
    public void transferOnePosTelemetry(String key, String data)
    {
        JSONObject json = new JSONObject(data);
        boolean valid = json.getBoolean("valid");
        if(valid)
        {
            String vin = key.substring(4,21);
            String obdCode = vin;
            String longitude = String.valueOf(json.getDouble("lon"));
            String latitude = String.valueOf(json.getDouble("lat"));
            String time = key.substring(key.length()-14,key.length());
            Timestamp timestamp = new Timestamp(DateUtil.parseStrToDate(time,"yyyyMMddHHmmss").getTime());

            ObdLocation obdLocation = new ObdLocation(obdCode,vin,longitude,latitude,timestamp);
            obdLocationRepository.save(obdLocation);
        }
    }

}
