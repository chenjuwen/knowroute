package com.heasy.knowroute.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.heasy.knowroute.utils.DatetimeUtil;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * bean对象转成json字符串时，对日期字段值进行格式化处理
 */
public class DateJsonValueProcessor implements JsonValueProcessor {
	private DateFormat dateFormat;
	
	public DateJsonValueProcessor(String datePattern) {
        try {
            dateFormat = new SimpleDateFormat(datePattern);
        } catch (Exception e) {
            dateFormat = new SimpleDateFormat(DatetimeUtil.DEFAULT_PATTERN_DT);
        }
    }

	@Override
	public Object processObjectValue(String paramKey, Object paramValue, JsonConfig jsonConfig) {
		if(paramValue == null) {
			return "";
		}
		
		if(paramValue instanceof Date) {
	        return dateFormat.format((Date) paramValue);
		}else {
			return paramValue.toString();
		}
	}
	
	@Override
	public Object processArrayValue(Object paramObject, JsonConfig jsonConfig) {
		if(paramObject == null) {
			return "";
		}
		
		String[] obj = {};
        if (paramObject instanceof Date[]) {
            Date[] dates = (Date[]) paramObject;
            obj = new String[dates.length];
            for (int i = 0; i < dates.length; i++) {
                obj[i] = dateFormat.format(dates[i]);
            }
        }
        return obj;
	}

}
