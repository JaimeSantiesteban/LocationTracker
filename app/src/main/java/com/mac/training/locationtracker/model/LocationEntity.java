package com.mac.training.locationtracker.model;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by User on 9/12/2016.
 */
@Entity
public class LocationEntity {

    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private Double altitude;
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
    @NotNull
    private Long time;

    public LocationEntity() {
    }

    @Generated(hash = 1740178792)
    public LocationEntity(Long id, @NotNull Double altitude,
            @NotNull Double latitude, @NotNull Double longitude, @NotNull Long time) {
        this.id = id;
        this.altitude = altitude;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
