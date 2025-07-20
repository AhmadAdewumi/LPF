package com.ahmad.ProductFinder.projection;

import org.locationtech.jts.geom.Point;

import java.util.Set;

public interface StoreProjection {
    Long getId();

    String getName();

    String getDescription();

    boolean getIs_active();

    Double getLatitude();

    Double getLongitude();

    Double getDistance();

    String getStreet();

    String getCity();

    String getState();

    String getCountry();

    String getPostal_code();

    Point getLocation();

    Double getDistance_in_metres();

    Set<String> getTags();
}
