package com.walkfun.common.lib;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: LeonLu
 * Date: 3/5/13
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommonUtils {

    private static final String[] DATE_PATTERNS = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "dd/MM/yyyy HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS"};

    public static String checkString(String param, String paramName) {
        checkNull(param, paramName);

        if (param.trim().length() == 0) {
            throw new IllegalArgumentException(String.format("Parameter '%s' should not be empty.", paramName));
        }

        return param;
    }

    public static <T> T checkNull(T param, String paramName) {
        if (param == null) {
            throw new IllegalArgumentException(String.format("Parameter '%s' should not be null.", paramName));
        }

        return param;
    }

    public static String leftPadInt(int value) {
        return StringUtils.leftPad(String.valueOf(value), 3, "0");
    }

    public static Date parseDateDefaultToNull(String date) {
        if (StringUtils.isEmpty(date))
            return null;

        return parseDate(date);
    }

    public static Integer parseIntegerToNull(String value) {
        if (StringUtils.isEmpty(value))
            return null;

        return Integer.valueOf(value);
    }

    public static Double parseDoubleToNull(String value) {
        if (StringUtils.isEmpty(value))
            return null;

        return Double.valueOf(value);
    }

    public static Date parseDate(String date) {
        Assert.hasText(date, "date");

        try {
            return DateUtils.parseDate(date, DATE_PATTERNS);
        } catch (ParseException ex) {
            throw new IllegalArgumentException(String.format("Date format should be one of %s", Arrays.toString(DATE_PATTERNS)));
        }
    }

    public static interface IGetKey<K, V> {
        K getKey(V obj);
    }

    public static interface IGetValue<V, O> {
        V getValue(O obj);
    }

    public static <K, V> Map<K, List<V>> toListMap(List<V> list, IGetKey<K, V> keyMapper) {
        if (list == null) {
            return new HashMap<K, List<V>>();
        }

        Map<K, List<V>> result = new HashMap<K, List<V>>();
        for (V item : list) {
            K key = keyMapper.getKey(item);

            // exists
            if (result.get(key) != null) {
                result.get(key).add(item);
            }
            // not exists
            else {
                List<V> itemList = new ArrayList<V>();
                itemList.add(item);
                result.put(key, itemList);
            }
        }
        return result;
    }

    public static String getDeviceId(String deviceId) {
        if (deviceId != null) {
            deviceId = deviceId.replaceAll(" ", "").replaceAll("<", "").replaceAll(">", "");
        }
        if(deviceId != null && deviceId.length() == 64){
            return deviceId;
        }
        return null;
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int hoursBetween(Date smdate, Date bdate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            smdate = sdf.parse(sdf.format(smdate));

            bdate = sdf.parse(sdf.format(bdate));
            Calendar cal = Calendar.getInstance();
            cal.setTime(smdate);
            long time1 = cal.getTimeInMillis();
            cal.setTime(bdate);
            long time2 = cal.getTimeInMillis();
            long between_hours = (time2 - time1) / (1000 * 3600);
            return Integer.parseInt(String.valueOf(between_hours));
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return 0;
    }
}
