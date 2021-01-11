package com.heasy.knowroute.event;

import com.heasy.knowroute.bean.PointBean;
import com.heasy.knowroute.core.event.AbstractEvent;

import java.util.List;

/**
 * Created by Administrator on 2020/11/7.
 */
public class RouteTrackEvent extends AbstractEvent {
    private List<PointBean> dataList;

    public RouteTrackEvent(Object source, List<PointBean> dataList){
        super(source);
        this.dataList = dataList;
    }

    public List<PointBean> getList() {
        return dataList;
    }
}
