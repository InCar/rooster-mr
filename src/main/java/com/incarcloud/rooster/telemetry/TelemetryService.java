package com.incarcloud.rooster.telemetry;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.*;
import com.incarcloud.rooster.entity.*;
import com.incarcloud.rooster.repository.*;
import com.incarcloud.rooster.utils.DateUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Incar on 2017/3/2.
 */
@Service
public class TelemetryService {

    @Autowired
    ObdLocationRepository obdLocationRepository;

    @Autowired
    MobileyeSTDRepository mobileyeSTDRepository;

    @Autowired
    MobileyeInfoRepository mobileyeInfoRepository;

    @Autowired
    MobileyeTSRRepository mobileyeTSRRepository;

    @Autowired
    MobileyeTSRDRepository mobileyeTSRDRepository;

    private static String TABLE_NAME = "telemetry";
    private static String PRIMARY_KEY_NAME = "key";

    private static Logger s_logger = LoggerFactory.getLogger(TelemetryService.class);
    private static final int PK_TIME_BGN = 36;

    private List<ObdLocation> _listPos = new ArrayList<>();
    private List<MobileyeSTD> _listMeSTD = new ArrayList<>();
    private List<MobileyeInfo> _listMeInfo = new ArrayList<>();
    private List<MobileyeTSR> _listMeTSR = new ArrayList<>();
    private List<MobileyeTSRD> _listMeTSRD = new ArrayList<>();

    void beginTransfer(){
        _listPos.clear();
        _listMeSTD.clear();
        _listMeInfo.clear();
        _listMeTSR.clear();
        _listMeTSRD.clear();
    }

    void endTransfer(){
        if(_listPos.size() > 0){
            obdLocationRepository.save(_listPos);
            s_logger.info("pos {}", _listPos.size());
            _listPos.clear();
        }

        if(_listMeSTD.size() > 0){
            mobileyeSTDRepository.save(_listMeSTD);
            s_logger.info("mobileye std {}", _listMeSTD.size());
            _listMeSTD.clear();
        }

        if(_listMeInfo.size() > 0){
            mobileyeInfoRepository.save(_listMeInfo);
            s_logger.info("mobileye info {}", _listMeInfo.size());
            _listMeInfo.clear();
        }

        if(_listMeTSR.size() > 0){
            mobileyeTSRRepository.save(_listMeTSR);
            s_logger.info("mobileye tsr {}", _listMeTSR.size());
            _listMeTSR.clear();
        }

        if(_listMeTSRD.size() > 0){
            mobileyeTSRDRepository.save(_listMeTSRD);
            s_logger.info("mobileye tsrd {}", _listMeTSRD.size());
            _listMeTSRD.clear();
        }
    }

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
        int count = 0;
        while (true) {
            GetRangeResponse getRangeResponse = client.getRange(new GetRangeRequest(rangeRowQueryCriteria));
            beginTransfer();
            for (Row row : getRangeResponse.getRows()) {
                count++;
                String key = row.getPrimaryKey().getPrimaryKeyColumn(PRIMARY_KEY_NAME).getValue().asString();
                String data = row.getLatestColumn("data").getValue().asString();
                s_logger.debug("Telemetry Data: {}", data);
                if(flag == TelemetryFlag.Position)
                    transferOnePos(key,data);   // 转存单条位置数据
                else if(flag == TelemetryFlag.Mobileye)
                    transferOneMobileye(key, data); // Mobileye
                else if(flag == TelemetryFlag.Info)
                    transferOneMobileyeInfo(key, data);
                else if(flag == TelemetryFlag.TSR)
                    transferOneMobileyeTSR(key, data);
                else if(flag == TelemetryFlag.TSRD)
                    transferOneMobileyeTSRD(key, data);
            }
            endTransfer();
            // 若nextStartPrimaryKey不为null, 则继续读取.
            if (getRangeResponse.getNextStartPrimaryKey() != null) {
                rangeRowQueryCriteria.setInclusiveStartPrimaryKey(getRangeResponse.getNextStartPrimaryKey());
            } else {
                break;
            }
        }
        s_logger.info("{} {} transferred", flag.name(), count);
    }

    /**
     * 转存单条位置数据
     * @param data 位置数据
     */
    public void transferOnePos(String key, String data)
    {
        try {
            JSONObject json = new JSONObject(data);
            boolean valid = json.getBoolean("valid");
            if (valid) {
                String vin = key.substring(4, 21);
                String obdCode = vin;
                String longitude = "E" + String.valueOf(json.getDouble("lon"));
                String latitude = "N" + String.valueOf(json.getDouble("lat"));
                String time = key.substring(PK_TIME_BGN, key.length());
                long millSeconds = DateUtil.parseStrToDate(time, "yyyyMMddHHmmssSSS").getTime();
                int tripId = (int) (millSeconds / 86400000D);
                Timestamp timestamp = new Timestamp(millSeconds);

                ObdLocation obdLocation = new ObdLocation(obdCode, tripId, vin, longitude, latitude, timestamp);
                _listPos.add(obdLocation);
            }
        }
        catch(DataIntegrityViolationException ex){
            // 忽略这个错误,1秒内多个数据
            s_logger.warn("忽略1秒内的多个位置数据 {}", ex.toString());
        }
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
        MobileyePK pk = new MobileyePK();
        pk.setVin(key.substring(4, 21));
        pk.setTm(DateUtil.parseStrToDate(key.substring(PK_TIME_BGN,key.length()), "yyyyMMddHHmmssSSS"));
        entry.setPk(pk);
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

        _listMeSTD.add(entry);
    }

    public void transferOneMobileyeInfo(String key, String data){
        // {"brakes":false,"left":false,"right":false,"wipers":"NA","beam":{"low":"NA","high":"NA"},"speed":"NA"}
        JSONObject json = new JSONObject(data);
        JSONObject jsonBeam = json.getJSONObject("beam");

        MobileyePK pk = new MobileyePK();
        pk.setVin(key.substring(4, 21));
        pk.setTm(DateUtil.parseStrToDate(key.substring(PK_TIME_BGN,key.length()), "yyyyMMddHHmmssSSS"));

        MobileyeInfo entry = new MobileyeInfo();
        entry.setPk(pk);
        entry.setBrakes(json.getBoolean("brakes"));
        entry.setBrake_left(json.getBoolean("left"));
        entry.setBrake_right(json.getBoolean("right"));

        if(!json.getString("wipers").equals("NA"))
            entry.setWipers(json.getBoolean("wipers"));

        if(!jsonBeam.getString("low").equals("NA"))
            entry.setBeam_low(jsonBeam.getBoolean("low"));

        if(!jsonBeam.getString("high").equals("NA"))
            entry.setBeam_high(jsonBeam.getBoolean("high"));

        try {
            entry.setSpeed(json.getInt("speed"));
        }
        catch(JSONException ex){
            if(!json.getString("speed").equals("NA")) {
                s_logger.error("Parse speed error: {}, key: {}, json: {}", ex.getMessage(), key, data);
                throw ex;
            }
        }

        _listMeInfo.add(entry);
    }

    public void transferOneMobileyeTSR(String key, String data){
        // [{"flag":"Regular","speed":10,"flag2":"NA","pos":{"x":0,"y":0,"z":0},"filter":"NA"},
        //  {"flag":"Regular","speed":10,"flag2":"NA","pos":{"x":0,"y":0,"z":0},"filter":"NA"},
        //  {"flag":"Regular","speed":10,"flag2":"NA","pos":{"x":0,"y":0,"z":0},"filter":"NA"},
        //  {"flag":"Regular","speed":10,"flag2":"NA","pos":{"x":0,"y":0,"z":0},"filter":"NA"},
        //  {"flag":"Regular","speed":10,"flag2":"NA","pos":{"x":0,"y":0,"z":0},"filter":"NA"},
        //  {"flag":"Regular","speed":10,"flag2":"NA","pos":{"x":0,"y":0,"z":0},"filter":"NA"},
        //  {"flag":"Regular","speed":10,"flag2":"NA","pos":{"x":0,"y":0,"z":0},"filter":"NA"}]

        String vin = key.substring(4, 21);
        Date tm = DateUtil.parseStrToDate(key.substring(PK_TIME_BGN,key.length()), "yyyyMMddHHmmssSSS");

        JSONArray array = new JSONArray(data);
        int i = 0;
        for(Object obj : array){
            JSONObject json = (JSONObject)obj;
            JSONObject jsonPos = json.getJSONObject("pos");

            MobileyeTSR tsr = new MobileyeTSR();

            MobileyeTsrPK pk = new MobileyeTsrPK();
            pk.setVin(vin);
            pk.setTm(tm);
            pk.setSn(i);
            tsr.setPk(pk);

            tsr.setFlag(json.getString("flag"));
            tsr.setFlag2(json.getString("flag2"));
            tsr.setFilter(json.getString("filter"));
            tsr.setSpeed(json.getInt("speed"));
            tsr.setX((float) jsonPos.getDouble("x"));
            tsr.setY((float) jsonPos.getDouble("y"));
            tsr.setZ((float) jsonPos.getDouble("z"));

            _listMeTSR.add(tsr);

            i++;
        }
    }

    public void transferOneMobileyeTSRD(String key, String data){
        // [{"flag":"Regular","speed":10,"flag2":"NA"},
        //  {"flag":"Regular","speed":10,"flag2":"NA"},
        //  {"flag":"Regular","speed":10,"flag2":"NA"},
        //  {"flag":"Regular","speed":10,"flag2":"NA"}]

        String vin = key.substring(4, 21);
        Date tm = DateUtil.parseStrToDate(key.substring(PK_TIME_BGN,key.length()), "yyyyMMddHHmmssSSS");

        JSONArray array = new JSONArray(data);
        int i = 0;
        for(Object obj : array){
            JSONObject json = (JSONObject)obj;

            MobileyeTSRD tsr = new MobileyeTSRD();

            MobileyeTsrPK pk = new MobileyeTsrPK();
            pk.setVin(vin);
            pk.setTm(tm);
            pk.setSn(i);
            tsr.setPk(pk);

            tsr.setFlag(json.getString("flag"));
            tsr.setFlag2(json.getString("flag2"));
            tsr.setSpeed(json.getInt("speed"));

            _listMeTSRD.add(tsr);

            i++;
        }
    }

    public void deleteByVinAndTime(String vin, Date dateBegin, Date dateEnd, TelemetryFlag flag)
    {
        if(flag == TelemetryFlag.Position)
            obdLocationRepository.deleteByVinAndTime(vin, dateBegin, dateEnd);
        else if(flag == TelemetryFlag.Mobileye)
            mobileyeSTDRepository.deleteByVinAndTime(vin, dateBegin, dateEnd);
        else if(flag == TelemetryFlag.Info)
            mobileyeInfoRepository.deleteByVinAndTime(vin, dateBegin, dateEnd);
        else if(flag == TelemetryFlag.TSR)
            mobileyeTSRRepository.deleteByVinAndTime(vin, dateBegin, dateEnd);
        else if (flag == TelemetryFlag.TSRD)
            mobileyeTSRDRepository.deleteByVinAndTime(vin, dateBegin, dateEnd);
    }
}
