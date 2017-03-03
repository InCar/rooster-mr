package com.incarcloud.rooster.telemetry;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.*;
import com.incarcloud.rooster.entity.MobileyeSTD;
import com.incarcloud.rooster.entity.ObdLocation;
import com.incarcloud.rooster.repository.MobileyeSTDRepository;
import com.incarcloud.rooster.repository.ObdLocationRepository;
import com.incarcloud.rooster.utils.DateUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;


/**
 * Created by Incar on 2017/3/2.
 */
@Service
public class TelemetryService {

    @Autowired
    ObdLocationRepository obdLocationRepository;

    @Autowired
    MobileyeSTDRepository mobileyeSTDRepository;

    private static String TABLE_NAME = "telemetry";
    private static String PRIMARY_KEY_NAME = "key";

    /**
     * 转存单个车辆位置数据
     * @param client 表格存储客户端
     * @param startPkValue 主键范围起始
     * @param endPkValue 主键范围结束
     */
    public void transferTelemetry(SyncClient client, String startPkValue, String endPkValue, TelemetryFlag flag)
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
                String key = row.getPrimaryKey().getPrimaryKeyColumn(PRIMARY_KEY_NAME).getValue().asString();
                String data = row.getLatestColumn("data").getValue().asString();
                System.out.println("Telemetry Data: " + data);
                if(flag == TelemetryFlag.Position)
                    transferOnePos(key,data);   // 转存单条位置数据
                else if(flag == TelemetryFlag.Mobileye)
                    transferOneMobileye(key, data); // Mobileye
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
    public void transferOnePos(String key, String data)
    {
        JSONObject json = new JSONObject(data);
        boolean valid = json.getBoolean("valid");
        if(valid)
        {
            String vin = key.substring(4,21);
            String obdCode = vin;
            String longitude = "E" + String.valueOf(json.getDouble("lon"));
            String latitude = "N" + String.valueOf(json.getDouble("lat"));
            String time = key.substring(key.length()-14,key.length());
            long millSeconds = DateUtil.parseStrToDate(time,"yyyyMMddHHmmss").getTime();
            int tripId = (int)(millSeconds/86400000D);
            Timestamp timestamp = new Timestamp(millSeconds);

            ObdLocation obdLocation = new ObdLocation(obdCode,tripId,vin,longitude,latitude,timestamp);
            ObdLocation returnObdLocation = obdLocationRepository.save(obdLocation);
            System.out.println(returnObdLocation);
        }
    }

    public void deleteByVinAndTime(String vin, Date dateBegin, Date dateEnd)
    {
        obdLocationRepository.deleteByVinAndTime(vin, dateBegin, dateEnd);
        mobileyeSTDRepository.deleteByVinAndTime(vin, dateBegin, dateEnd);
    }


    public void transferOneMobileye(String key, String data){
        // { "sound":"Silent","daylight":"Day","stopped":false,
        //  "headway":{"seconds":-1,"level":0,"repeatable":false},
        //  "LDW":{"isOff":false,"left":false,"right":false},
        //  "TamperAlert":false,"FCW":false,"PedsFCW":false,"PedsDZ":false,
        //  "TSR":{"enabled":false,"level":0},
        //  "maintenance":false,"failsafe":false,"error":0 } -> t_mobileye_std

        JSONObject json = new JSONObject(data);
        JSONObject jsonHeadway = json.getJSONObject("headway");
        JSONObject jsonLDW = json.getJSONObject("LDW");
        JSONObject jsonTSR = json.getJSONObject("TSR");

        MobileyeSTD entry = new MobileyeSTD();
        entry.setVin(key.substring(4, 21));
        entry.setTm(DateUtil.parseStrToDate(key.substring(key.length()-14,key.length()), "yyyyMMddHHmmss"));
        entry.setSound(json.getString("sound"));
        entry.setDaylight(json.getString("daylight"));
        entry.setStopped(json.getBoolean("stopped"));
        entry.setHeadway((float)jsonHeadway.getDouble("seconds"));
        entry.setHeadwayWarningLvl(jsonHeadway.getInt("level"));
        entry.setHeadwayRepeateable(jsonHeadway.getBoolean("repeatable"));
        entry.setLdwOff(jsonLDW.getBoolean("isOff"));
        entry.setLdwLeft(jsonLDW.getBoolean("left"));
        entry.setLdwRight(jsonLDW.getBoolean("right"));
        entry.setFcw(json.getBoolean("FCW"));
        entry.setPedsFcw(json.getBoolean("PedsFCW"));
        entry.setPedsDz(json.getBoolean("PedsDZ"));
        entry.setTamperAlert(json.getBoolean("TamperAlert"));
        entry.setTsrEnabled(jsonTSR.getBoolean("enabled"));
        entry.setTsrLevel(jsonTSR.getInt("level"));
        entry.setMaintenance(json.getBoolean("maintenance"));
        entry.setFailSafe(json.getBoolean("failsafe"));
        entry.setErrorCode(json.getInt("error"));

        mobileyeSTDRepository.save(entry);
    }
}
