package com.mac.training.locationtracker.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mac.training.locationtracker.R;
import com.mac.training.locationtracker.manager.DatabaseManager;
import com.mac.training.locationtracker.model.LocationEntity;

import java.io.IOException;
import java.util.List;

/**
 * Created by User on 9/12/2016.
 */
// SupportMapFragment ackwards compatibility before API 12
public class MapFrag extends MapFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback {

    private static final String LOG = MapFrag.class.getName();
    // getting the user's location for initializing the map camera
    private static GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    // used in the sample code for switching between different map display types
    private final int[] MAP_TYPES = {GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};
    private int curMapTypeIndex = 0;

    private GoogleMap map;
    private static Context context;

    /*****
     * Listeners implementation
     ******/

    /*monitor the state of the GoogleApiClient, which is used in this application
    for getting the user's current location.*/
    /*Also map
    Config*/
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mCurrentLocation = LocationServices
                .FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        initCamera(mCurrentLocation);
    }

    /*monitor the state of the GoogleApiClient, which is used in this application
    for getting the user's current location.*/
    @Override
    public void onConnectionSuspended(int i) {

    }

    /*monitor the state of the GoogleApiClient, which is used in this application
     for getting the user's current location.*/
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Create a default location if the Google API Client fails. Placing location at Googleplex
        mCurrentLocation = new Location("");
        mCurrentLocation.setLatitude(37.422535);
        mCurrentLocation.setLongitude(-122.084804);
        initCamera(mCurrentLocation);
    }

    /*is triggered when the user clicks on the info window that pops up over a
    marker on the map.*/
    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getActivity(), "Clicked on marker", Toast.LENGTH_SHORT).show();
    }

    /*triggered when the user either taps or holds down on a portion of the map.*/
    @Override
    public void onMapClick(LatLng latLng) {
        MarkerOptions options = new MarkerOptions().position(latLng);
        options.title(getAddressFromLatLng(latLng));

        options.icon(BitmapDescriptorFactory.defaultMarker());
        map.addMarker(options);

    }

    /*triggered when the user either taps or holds down on a portion of the map.*/
    @Override
    public void onMapLongClick(LatLng latLng) {
        MarkerOptions options = new MarkerOptions().position(latLng);
        options.title(getAddressFromLatLng(latLng));

        options.icon(BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher)));

        map.addMarker(options);
    }

    /*called when the user clicks on a marker on the map, which typically also
    displays the info window for that marker.*/
    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    /*****
     Listeners implementation
     ******/

    /*****
     * Map initialization
     ******/

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        setHasOptionsMenu(true);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        /*initListeners();*/
    }

    private void initListeners() {
        map.setOnMarkerClickListener(this);
        map.setOnMapLongClickListener(this);
        map.setOnInfoWindowClickListener(this);
        map.setOnMapClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        initListeners();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /*****
     Map initialization
     ******/

    /*****
     * Configuring the Map
     ******/
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void initCamera(Location location) {
        CameraPosition position = CameraPosition.builder()
                .target(new LatLng(location.getLatitude(),
                        location.getLongitude()))
                .zoom(16f)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();

        map.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), null);

        map.setMapType(MAP_TYPES[curMapTypeIndex]);
        map.setTrafficEnabled(false);
        if (ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
    }
    /*****
     Configuring the Map
     ******/

    /*****
     * Util Methods
     ******/
    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getActivity());

        String address = "";
        try {
            address = geocoder
                    .getFromLocation(latLng.latitude, latLng.longitude, 1)
                    .get(0).getAddressLine(0);
        } catch (IOException e) {
        }

        return address;
    }

    private void drawCircle(LatLng location) {
        CircleOptions options = new CircleOptions();
        options.center(location);
        //Radius in meters
        options.radius(10);
        options.fillColor(R.color.fill_color);
        options.strokeColor(R.color.stroke_color);
        options.strokeWidth(10);
        map.addCircle(options);
    }

    private void drawPolygon(LatLng startingLocation) {
        LatLng point2 = new LatLng(startingLocation.latitude + .001,
                startingLocation.longitude);
        LatLng point3 = new LatLng(startingLocation.latitude,
                startingLocation.longitude + .001);

        PolygonOptions options = new PolygonOptions();
        options.add(startingLocation, point2, point3);

        options.fillColor(R.color.fill_color);
        options.strokeColor(R.color.stroke_color);
        options.strokeWidth(10);
        map.addPolygon(options);
    }

    private void drawOverlay(LatLng location, int width, int height) {
        GroundOverlayOptions options = new GroundOverlayOptions();
        options.position(location, width, height);

        options.image(BitmapDescriptorFactory
                .fromBitmap(BitmapFactory
                        .decodeResource(getResources(),
                                R.mipmap.ic_launcher)));
        map.addGroundOverlay(options);
    }

    private void toggleTraffic() {
        map.setTrafficEnabled(!map.isTrafficEnabled());
    }

    private void cycleMapType() {
        if (curMapTypeIndex < MAP_TYPES.length - 1) {
            curMapTypeIndex++;
        } else {
            curMapTypeIndex = 0;
        }

        map.setMapType(MAP_TYPES[curMapTypeIndex]);
    }

    /*****
     * Util Methods
     ******/


    // Remove listeners
    private void removeListeners() {
        if (map != null) {
            map.setOnMarkerClickListener(null);
            map.setOnMapLongClickListener(null);
            map.setOnInfoWindowClickListener(null);
            map.setOnMapClickListener(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeListeners();
    }
    // Remove listeners

    //more functionality
    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear: {
                map.clear();
                return true;
            }
            case R.id.action_circle: {
                drawCircle(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                return true;
            }
            case R.id.action_polygon: {
                drawPolygon(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                return true;
            }
            case R.id.action_overlay: {
                drawOverlay(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 250, 250);
                return true;
            }
            case R.id.action_traffic: {
                toggleTraffic();
                return true;
            }
            case R.id.action_cycle_map_type: {
                cycleMapType();
                return true;
            }
            case R.id.action_print_trace: {
                printTrace();
                return true;
            }
            case R.id.action_where_am_i: {
                setCurrentLocation();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void setCurrentLocation() {
        Location current = getCurrentLocation();
        if (current != null) {
            CameraPosition position = CameraPosition.builder()
                    .target(new LatLng(current.getLatitude(),
                            current.getLongitude()))
                    .zoom(16f)
                    .bearing(0.0f)
                    .tilt(0.0f)
                    .build();
            map.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), null);
        }
    }

    //


    public static final Location getCurrentLocation() {
        Location location = null;
        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        location = LocationServices
                .FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        return location;
    }

    private void printTrace() {


        List<LocationEntity> locations =
                DatabaseManager.getInstance(getActivity()).listLocations();
        CircleOptions options = null;
        if (locations != null && !locations.isEmpty()) {
            for (int i = 0; i < locations.size(); i++) {
                LocationEntity locationCurr = locations.get(i);
                LocationEntity locationNext = locations.get(i);
                try {
                    locationNext = locations.get(i + 1);
                } catch (IndexOutOfBoundsException ioobe) {
                    Log.d(LOG, "It doesnt have more locations");
                }
                options = new CircleOptions();
                options.center(new LatLng(locationCurr.getLatitude(), locationCurr.getLongitude()));

                //Radius in meters
                options.radius(20);
                options.fillColor(R.color.fill_color_trace);
                options.strokeColor(R.color.stroke_color_trace);
                options.strokeWidth(10);
                map.addCircle(options);
                //polyline
                if (!locationCurr.equals(locationNext)) {
                    Polyline line = map.addPolyline(new PolylineOptions()
                            .add(new LatLng(locationCurr.getLatitude(), locationCurr.getLongitude()),
                                    new LatLng(locationNext.getLatitude(), locationNext.getLongitude()))
                            .width(5)
                            .color(Color.RED));
                }
            }
        }
    }

}

