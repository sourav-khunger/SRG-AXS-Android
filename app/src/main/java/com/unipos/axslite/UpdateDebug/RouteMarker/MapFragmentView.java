/*
 * Copyright (c) 2011-2020 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.unipos.axslite.UpdateDebug.RouteMarker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;
import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPolygon;
import com.here.android.mpa.common.GeoPolyline;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.ViewRect;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapCircle;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapPolygon;
import com.here.android.mpa.mapping.MapPolyline;
import com.here.android.mpa.mapping.AndroidXMapFragment;
import com.unipos.axslite.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class encapsulates the properties and functionality of the Map view.
 */
public class MapFragmentView {
    private static final String TAG = MapFragmentView.class.getSimpleName();

    private static final int ADD_MARKER_MENU_ID = 0;
    private static final int REMOVE_MARKER_MENU_ID = 1;
    private static final int ADD_POLYGON_MENU_ID = 2;
    private static final int REMOVE_POLYGON_MENU_ID = 3;
    private static final int ADD_POLYLINE_MENU_ID = 4;
    private static final int REMOVE_POLYLINE_MENU_ID = 5;
    private static final int ADD_CIRCLE_MENU_ID = 6;
    private static final int REMOVE_CIRCLE_MENU_ID = 7;
    private static final int NAVIGATE_TO_MENU_ID = 8;

    private AndroidXMapFragment m_mapFragment;
    private AppCompatActivity m_activity;
    private Map m_map;
    private int mapMarkerCount = 0;

    private final LinkedList<MapPolygon> m_polygons = new LinkedList<>();
    private final LinkedList<MapPolyline> m_polylines = new LinkedList<>();
    private final LinkedList<MapCircle> m_circles = new LinkedList<>();
    private final LinkedList<MapMarker> m_map_markers = new LinkedList<>();
    MapPolyline addMapPolyline;
//    private Scen mapScene;

    /**
     * Initial UI button on map fragment view. It includes several buttons to add/remove map objects
     * such as MapPolygon, MapPolyline, MapCircle and MapMarker.
     *
     * @param activity
     */
    public MapFragmentView(AppCompatActivity activity) {
        m_activity = activity;
        initMapFragment();
    }

    private AndroidXMapFragment getMapFragment() {
        return (AndroidXMapFragment) m_activity.getSupportFragmentManager().findFragmentById(R.id.mapfragment1);
    }

    private void initMapFragment() {
        /* Locate the mapFragment UI element */
        m_mapFragment = getMapFragment();

        // This will use external storage to save map cache data, it is also possible to set
        // private app's path
        String path = new File(m_activity.getExternalFilesDir(null), ".here-map-data")
                .getAbsolutePath();
        // This method will throw IllegalArgumentException if provided path is not writable
        com.here.android.mpa.common.MapSettings.setDiskCacheRootPath(path);

        if (m_mapFragment != null) {
            /* Initialize the AndroidXMapFragment, results will be given via the called back. */
            m_mapFragment.init(new OnEngineInitListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onEngineInitializationCompleted(Error error) {

                    if (error == Error.NONE) {
                        /*
                         * If no error returned from map fragment initialization, the map will be
                         * rendered on screen at this moment.Further actions on map can be provided
                         * by calling Map APIs.
                         */
                        m_map = m_mapFragment.getMap();

                        /*
                         * Set the map center to the 4350 Still Creek Dr Burnaby BC (no animation).
                         */
                        m_map.setCenter(new GeoCoordinate(29.9753092, 77.571972),
                                Map.Animation.BOW);

                        /* Set the zoom level to the average between min and max zoom level. */
                        m_map.setZoomLevel(14);

                        m_activity.supportInvalidateOptionsMenu();
//                        m_map =
                        MapPolyline mapPolyline = createPolyline();
                        m_map.addMapObject(mapPolyline);
                        /*
                         * Set up a handler for handling MapMarker drag events.
                         */
                        m_mapFragment.setMapMarkerDragListener(new OnDragListenerHandler());

                    } else {
                        Log.e(this.getClass().toString(), "onEngineInitializationCompleted: " +
                                "ERROR=" + error.getDetails(), error.getThrowable());
                        new AlertDialog.Builder(m_activity).setMessage(
                                "Error : " + error.name() + "\n\n" + error.getDetails())
                                .setTitle(R.string.engine_init_error)
                                .setNegativeButton(android.R.string.cancel,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                m_activity.finish();
                                            }
                                        }).create().show();
                    }
                }
            });
        }
    }

    boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ADD_MARKER_MENU_ID:
                addMapMarkerObject();
                break;
            case REMOVE_MARKER_MENU_ID:
                if (!m_map_markers.isEmpty()) {
                    m_map.removeMapObject(m_map_markers.removeLast());
                }
                break;
            case ADD_POLYGON_MENU_ID:
                addPolygonObject();
                break;
            case REMOVE_POLYGON_MENU_ID:
                if (!m_polygons.isEmpty()) {
                    m_map.removeMapObject(m_polygons.removeLast());
                }
                break;
            case ADD_POLYLINE_MENU_ID:
                addPolylineObject();
                break;
            case REMOVE_POLYLINE_MENU_ID:
                if (!m_polylines.isEmpty()) {
                    m_map.removeMapObject(m_polylines.removeLast());
                }
                break;
            case ADD_CIRCLE_MENU_ID:
                addCircleObject();
                break;
            case REMOVE_CIRCLE_MENU_ID:
                if (!m_circles.isEmpty()) {
                    m_map.removeMapObject(m_circles.removeLast());
                }
                break;

            case NAVIGATE_TO_MENU_ID:
                if (!m_map_markers.isEmpty()) {
                    int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            100, m_activity.getResources().getDisplayMetrics());
                    navigateToMapMarkers(m_map_markers, padding);
                } else {
                    Toast.makeText(m_activity, "There is no any map markers added on the map",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }

        return true;
    }

    boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, ADD_MARKER_MENU_ID, ADD_MARKER_MENU_ID, "Add Marker");
        menu.add(0, REMOVE_MARKER_MENU_ID, REMOVE_MARKER_MENU_ID, "Remove Marker");
        menu.add(0, ADD_POLYGON_MENU_ID, ADD_POLYGON_MENU_ID, "Add Polygon");
        menu.add(0, REMOVE_POLYGON_MENU_ID, REMOVE_POLYGON_MENU_ID, "Remove polygon");
        menu.add(0, ADD_POLYLINE_MENU_ID, ADD_POLYLINE_MENU_ID, "Add polyline");
        menu.add(0, REMOVE_POLYLINE_MENU_ID, REMOVE_POLYLINE_MENU_ID, "Remove polyline");
        menu.add(0, ADD_CIRCLE_MENU_ID, ADD_CIRCLE_MENU_ID, "Add circle");
        menu.add(0, REMOVE_CIRCLE_MENU_ID, REMOVE_CIRCLE_MENU_ID, "Remove circle");
        menu.add(0, NAVIGATE_TO_MENU_ID, NAVIGATE_TO_MENU_ID, "Navigate to added markers");

        return true;
    }

    /**
     * Create a MapPolygon and add the MapPolygon to active map view.
     */
    private void addPolygonObject() {
        // create an bounding box centered at current cent
        GeoBoundingBox boundingBox = new GeoBoundingBox(m_map.getCenter(), 1000, 1000);
        // add boundingbox's four vertices to list of Geocoordinates.
        List<GeoCoordinate> coordinates = new ArrayList<GeoCoordinate>();
        coordinates.add(boundingBox.getTopLeft());
        coordinates.add(new GeoCoordinate(boundingBox.getTopLeft().getLatitude(),
                boundingBox.getBottomRight().getLongitude(),
                boundingBox.getTopLeft().getAltitude()));
        coordinates.add(boundingBox.getBottomRight());
        coordinates.add(new GeoCoordinate(boundingBox.getBottomRight().getLatitude(),
                boundingBox.getTopLeft().getLongitude(), boundingBox.getTopLeft().getAltitude()));
        // create GeoPolygon with list of GeoCoordinates.
        GeoPolygon geoPolygon = new GeoPolygon(coordinates);
        // create MapPolygon with GeoPolygon.
        MapPolygon polygon = new MapPolygon(geoPolygon);
        // set line color, fill color and line width
        polygon.setLineColor(Color.RED);
        polygon.setFillColor(Color.GRAY);
        polygon.setLineWidth(12);
        // add MapPolygon to map.
        m_map.addMapObject(polygon);

        m_polygons.add(polygon);
    }

    /**
     * Create a MapPolyline and add the MapPolyline to active map view.
     */
    private void addPolylineObject() {
        // create boundingBox centered at current location
        GeoBoundingBox boundingBox = new GeoBoundingBox(m_map.getCenter(), 1000, 1000);
        // add boundingBox's top left and bottom right vertices to list of GeoCoordinates
        List<GeoCoordinate> coordinates = new ArrayList<GeoCoordinate>();
        coordinates.add(boundingBox.getTopLeft());
        coordinates.add(boundingBox.getBottomRight());
        // create GeoPolyline with list of GeoCoordinates
        GeoPolyline geoPolyline = new GeoPolyline(coordinates);
        MapPolyline polyline = new MapPolyline(geoPolyline);
        polyline.setLineColor(Color.BLUE);
        polyline.setLineWidth(12);
        // add GeoPolyline to current active map
        m_map.addMapObject(polyline);

        m_polylines.add(polyline);
    }


    /**
     * create a MapCircle and add the MapCircle to active map view.
     */
    private void addCircleObject() {
        // create a MapCircle centered at current location with radius 400
        MapCircle circle = new MapCircle(400.0, m_map.getCenter());
        circle.setLineColor(Color.BLUE);
        circle.setFillColor(Color.GRAY);
        circle.setLineWidth(12);
        m_map.addMapObject(circle);

        m_circles.add(circle);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private MapPolyline createPolyline() {
        ArrayList<GeoCoordinate> coordinates = new ArrayList<>();
        coordinates.add(new GeoCoordinate(29.9753092, 77.571972));
        coordinates.add(new GeoCoordinate(29.9685473, 77.5644495));
        coordinates.add(new GeoCoordinate(29.9623858, 77.5483282));
        coordinates.add(new GeoCoordinate(29.9701266, 77.5698228));

        GeoPolyline geoPolyline;
        try {
            geoPolyline = new GeoPolyline(coordinates);
        } catch (ExceptionInInitializerError e) {
            // Less than two vertices.
            e.printStackTrace();
            return null;
        }

        int widthInPixels = 20;
//        Color lineColor = new Color((short) 0x00, (short) 0x90, (short) 0x8A, (short) 0xA0);
        MapPolyline mapPolyline = new MapPolyline(geoPolyline);
        mapPolyline.setLineColor(generateColor());
        mapPolyline.setLineWidth(widthInPixels);

        return mapPolyline;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    int generateColor() {
        Random random = new Random();
        return Color.argb(1.0f, random.nextFloat(), random.nextFloat(), random.nextFloat());
    }

    /**
     * create a MapMarker and add the MapMarker to active map view.
     */
    private void addMapMarkerObject() {
        // create an image from cafe.png.
        Image marker_img = new Image();
        try {
            marker_img.setImageResource(R.drawable.gps_position);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // create a MapMarker centered at current location with png image.
        MapMarker marker = new MapMarker(m_map.getCenter(), marker_img);
        /*
         * Set MapMarker draggable.
         * How to move to?
         * In order to activate dragging of the MapMarker you have to do a long press on
         * the MapMarker then move it to a new position and release the MapMarker.
         */
        marker.setDraggable(true);
        marker.setTitle("MapMarker id: " + mapMarkerCount++);
        // add a MapMarker to current active map.
        m_map.addMapObject(marker);

        m_map_markers.add(marker);
    }

    private void navigateToMapMarkers(List<MapMarker> markers, int padding) {
        // find max and min latitudes and longitudes in order to calculate
        // geo bounding box so then we can map.zoomTo(geoBox, ...) to it.
        double minLat = 90.0d;
        double minLon = 180.0d;
        double maxLat = -90.0d;
        double maxLon = -180.0d;

        for (MapMarker marker : markers) {
            GeoCoordinate coordinate = marker.getCoordinate();
            double latitude = coordinate.getLatitude();
            double longitude = coordinate.getLongitude();
            minLat = Math.min(minLat, latitude);
            minLon = Math.min(minLon, longitude);
            maxLat = Math.max(maxLat, latitude);
            maxLon = Math.max(maxLon, longitude);
        }

        GeoBoundingBox box = new GeoBoundingBox(new GeoCoordinate(maxLat, minLon),
                new GeoCoordinate(minLat, maxLon));

        ViewRect viewRect = new ViewRect(padding, padding, m_map.getWidth() - padding * 2,
                m_map.getHeight() - padding * 2);
        m_map.zoomTo(box, viewRect, Map.Animation.LINEAR, Map.MOVE_PRESERVE_ORIENTATION);
    }

    private class OnDragListenerHandler implements MapMarker.OnDragListener {
        @Override
        public void onMarkerDrag(MapMarker mapMarker) {
            Log.i(TAG, "onMarkerDrag: " + mapMarker.getTitle() + " -> " + mapMarker
                    .getCoordinate());
        }

        @Override
        public void onMarkerDragEnd(MapMarker mapMarker) {
            Log.i(TAG, "onMarkerDragEnd: " + mapMarker.getTitle() + " -> " + mapMarker
                    .getCoordinate());
        }

        @Override
        public void onMarkerDragStart(MapMarker mapMarker) {
            Log.i(TAG, "onMarkerDragStart: " + mapMarker.getTitle() + " -> " + mapMarker
                    .getCoordinate());
        }
    }
}