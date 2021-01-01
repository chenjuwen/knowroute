package com.heasy.knowroute.event;

import com.baidu.mapapi.model.LatLng;
import com.heasy.knowroute.core.event.AbstractEvent;

import java.util.List;

/**
 * Created by Administrator on 2020/11/7.
 */
public class RouteTrackEvent extends AbstractEvent {
    private List<LatLng> points;

    public RouteTrackEvent(Object source, List<LatLng> points){
        super(source);
        this.points = points;
    }

    public List<LatLng> getPoints() {
        return points;
    }
}
