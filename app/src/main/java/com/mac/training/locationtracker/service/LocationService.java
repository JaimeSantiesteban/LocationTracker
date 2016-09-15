package com.mac.training.locationtracker.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.mac.training.locationtracker.R;
import com.mac.training.locationtracker.activity.MainActivity;
import com.mac.training.locationtracker.activity.MapFrag;
import com.mac.training.locationtracker.manager.DatabaseManager;
import com.mac.training.locationtracker.model.LocationEntity;

/**
 * Created by User on 9/12/2016.
 */
public class LocationService extends Service {

    private static final String LOG = LocationService.class.getName();
    private Integer NOTIFICATION_ID = 19850313; // Some random integer
    // Worker thread
    private static Thread workerThread;
    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            while (true) {
                getLocation();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Location locator;

    private void getLocation() {
        Log.d(LOG, "Aca ando!!!");
        locator = MapFrag.getCurrentLocation();
        if (locator != null) {
            /**
             * Manages the database for this application..
             */
            LocationEntity locationEntity = new LocationEntity();
            locationEntity.setAltitude(locator.getAltitude());
            locationEntity.setLongitude(locator.getLongitude());
            locationEntity.setLatitude(locator.getLatitude());
            locationEntity.setTime(locator.getTime());

            DatabaseManager.getInstance(this).insertLocations(locationEntity);

        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // start new thread and you your work there
        if (workerThread == null) {
            workerThread = new Thread(runnable);
            workerThread.start();
        }
        LoadNotification loadNotification = new LoadNotification("LocationTracker", "Getting Location...");
        loadNotification.notifyMessage();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        workerThread.interrupt();
        super.onDestroy();
    }

    class LoadNotification {

        private String titleMessage;
        private String textMessage;


        public LoadNotification(String titleMessage, String textMessage) {
            this.titleMessage = titleMessage;
            this.textMessage = textMessage;
        }

        public void notifyMessage() {
            NotificationCompat.Builder builder = getNotificationBuilder(MainActivity.class);
            startForeground(NOTIFICATION_ID, builder.build());

        }

        protected NotificationCompat.Builder getNotificationBuilder(Class clazz) {
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

            builder.setSmallIcon(R.drawable.ic_directions_run_black_24dp);  // icon id of the image

            builder.setContentTitle(this.titleMessage)
                    .setContentText(this.textMessage);

            Intent foregroundIntent = new Intent(getApplicationContext(), clazz);

            foregroundIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, foregroundIntent, 0);

            builder.setContentIntent(contentIntent);
            return builder;
        }

    }

}
