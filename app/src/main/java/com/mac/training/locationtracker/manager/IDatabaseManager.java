package com.mac.training.locationtracker.manager;

import com.mac.training.locationtracker.model.LocationEntity;

import java.util.ArrayList;

/**
 * Created by User on 9/12/2016.
 */
public interface IDatabaseManager {
    /**
     * Closing available connections
     */
    void closeDbConnections();

    /**
     * Delete all tables and content from our database
     */
    void dropDatabase();

    LocationEntity insertLocations(LocationEntity locationEntity);

    ArrayList<LocationEntity> listLocations();
}
