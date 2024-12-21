package com.algorithmicintegration.lyy.entity.util;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class TimeUtil {
    public static Timestamp getTimestampInDBFormat() {
        try {
            return new Timestamp(new Date(System.currentTimeMillis()).getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long getCurrentTimestamp() {
        return new Date(System.currentTimeMillis()).getTime();
    }

    /**
     * 时间格式的转换
     * @param inputTime 输入数据格式 例如 2018-08-30T02:55:50Z
     * @return 转换后的数据格式 2018-08-30 02:55:50
     */
    public String convertTime(String inputTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = inputFormat.parse(inputTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
