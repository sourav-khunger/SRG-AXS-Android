package com.unipos.axslite.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.here.android.mpa.common.ApplicationContext;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.MapEngine;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.AndroidXMapFragment;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapCircle;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapLabeledMarker;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapOverlayType;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.mapping.MapView;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import com.unipos.axslite.BackgroudService.Workers.LocationService;
import com.unipos.axslite.Database.Entities.TaskInfoEntity;
import com.unipos.axslite.Database.Repository.TaskInfoRepository;
import com.unipos.axslite.POJO.LoginResponse;
import com.unipos.axslite.R;
import com.unipos.axslite.Utils.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements OnEngineInitListener {
    private static final String TAG = MapsFragment.class.getSimpleName();

    private AndroidXMapFragment m_mapFragment;
    private AppCompatActivity m_activity;
    private Map m_map;
    public static final String MAP_FRAGMENT_TITLE = "Map";
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] RUNTIME_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    private MapFragmentView m_mapFragmentView;
    private Route m_route;
    MapRoute mapRoute;
    private GeoCoordinate geoCoordinate;
    List<TaskInfoEntity> taskInfoEntities = new ArrayList<>();
    private TaskInfoRepository mTaskInfoRepository;
    private TextView markerTxt;
    ImageView centerMap;
    MapView mapView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            m_activity = (AppCompatActivity) activity;
            //mCallbacks = (MyNavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: " + e);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationContext appContext = new ApplicationContext(m_activity.getApplicationContext());
        MapEngine.getInstance().init(appContext, this::onEngineInitializationCompleted);
    }

    public Boolean writeToSD(String text, int i) {
        Boolean write_successful = false;
        File root = null;
        try {
            // <span id="IL_AD8" class="IL_AD">check for</span> SDcard
            root = Environment.getExternalStorageDirectory();
            Log.e("SVG CREATE", "path.." + root.getAbsolutePath());

            //check sdcard permission
            if (root.canWrite()) {
                File fileDir = new File(root.getAbsolutePath() + "/AXS Files/Temp/");
                fileDir.mkdirs();

                File file = new File(fileDir, "icon" + i + ".svg");
                FileWriter filewriter = new FileWriter(file);
                BufferedWriter out = new BufferedWriter(filewriter);
                out.write(text);
                out.close();
                write_successful = true;
            }
        } catch (IOException e) {
            Log.e("ERROR:---", "Could not write file to SDCard" + e.getMessage());
            write_successful = false;
        }
        return write_successful;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        initUI(view);
//        m_mapFragment = new AndroidXMapFragment();
//        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mapfragment1, m_mapFragment).commit();

//        initMapFragment();

        return view;
    }

    String createSVG(int i) {

        String sBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\"" +
                " xmlns:xlink=\"http://www.w3.org/1999/xlink\" preserveAspectRatio=\"xMidYMid meet\" viewBox=\"0 0 640 640\" width=\"64\" height=\"64\"><defs><path d=\"M0 0L640 0L640 640L0 640L0 0Z\" id=\"b7WpYcHvrD\"></path><clipPath id=\"clipk2A0EotMI\"><use xlink:href=\"#b7WpYcHvrD\" opacity=\"1\"></use></clipPath><path d=\"M459.88 238.45C459.88 318.94 397.2 384.29 320 384.29C242.8 384.29 180.12 318.94 180.12 238.45C180.12 157.96 242.8 92.62 320 92.62C397.2 92.62 459.88 157.96 459.88 238.45Z\" id=\"b32wp0jpMi\"></path><text id=\"alwzunQkB\" x=\"300\" y=\"230.95\" font-size=\"250\" font-family=\"Roboto\" font-weight=\"bold\" font-style=\"normal\" letter-spacing=\"0\" alignment-baseline=\"before-edge\" transform=\"matrix(1 0 0 1 -36.904761904761926 -133.3333333333334)\" style=\"line-height:100%\" xml:space=\"preserve\" dominant-baseline=\"text-before-edge\"><tspan x=\"280.71\" dy=\"0em\" alignment-baseline=\"before-edge\" dominant-baseline=\"text-before-edge\" text-anchor=\"start\">" + i + "</tspan></text>\n" +
                "</defs><g><g><g><g clip-path=\"url(#clipk2A0EotMI)\" opacity=\"1\"><image xlink:href=\"data:image/png;base64,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\" x=\"0\" y=\"0\" width=\"64\" height=\"64\" transform=\"matrix(10.000000000000002 0 0 9.999999999999996 -4.547473508864641e-13 9.094947017729282e-13)\"></image></g></g><g><use xlink:href=\"#b32wp0jpMi\" opacity=\"1\" fill=\"#1f93da\" fill-opacity=\"1\"></use></g><g id=\"b2X52gOkqZ\"><use xlink:href=\"#alwzunQkB\" opacity=\"1\" fill=\"#ffffff\" fill-opacity=\"1\"></use></g></g></g></svg>";
//        writeFileOnInternalStorage(this, "icon.svg", sBody);
        writeToSD(sBody, i);
        return sBody;
    }

    void initUI(View v) {
//        m_naviControlButton = (Button) m_activity.findViewById(R.id.naviCtrlButton);
        mapView = v.findViewById(R.id.here_map);
        centerMap = v.findViewById(R.id.centerMap);

        mTaskInfoRepository = new TaskInfoRepository(getActivity().getApplication());
        taskInfoEntities = mTaskInfoRepository.getTaskInfos1();

  /*      routeSelectionList.add("    Show All   ");
        routeSelectionList.add("    Show DC   ");
        for (int i = 0; i < taskInfoEntities.size(); i++) {

            routeSelectionList.add(" " + (i + 1) + ". " + taskInfoEntities.get(i).getName());
        }*/
        for (int i = 0; i < taskInfoEntities.size(); i++) {
            createSVG((i + 1));
        }
        centerMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_map.setZoomLevel(13, Map.Animation.BOW);
                m_map.setCenter(PositioningManager.getInstance().getLastKnownPosition().getCoordinate(),
                        Map.Animation.BOW);
            }
        });
        if (m_map != null && !PositioningManager.getInstance().isActive()) {
            PositioningManager.getInstance().start(PositioningManager.LocationMethod.GPS_NETWORK); // use gps plus cell and wifi
        }
    }

    private AndroidXMapFragment getMapFragment() {
        return (AndroidXMapFragment) m_activity.getSupportFragmentManager().findFragmentById(R.id.mapfragment1);
    }


    private MapGesture.OnGestureListener onGestureListener = new MapGesture.OnGestureListener() {
        @Override
        public void onPanStart() {

        }

        @Override
        public void onPanEnd() {

        }

        @Override
        public void onMultiFingerManipulationStart() {

        }

        @Override
        public void onMultiFingerManipulationEnd() {

        }

        @Override
        public boolean onMapObjectsSelected(@NonNull List<ViewObject> list) {
            for (ViewObject viewObject : list) {
                if (viewObject.getBaseType() == ViewObject.Type.USER_OBJECT) {
                    MapObject mapObject = (MapObject) viewObject;

                    if (mapObject.getType() == MapObject.Type.MARKER) {

                        MapMarker window_marker = ((MapMarker) mapObject);
                        m_map.setCenter(window_marker.getCoordinate(), Map.Animation.BOW);
//                        showDialog(window_marker.getTitle());
                        Log.e("MARKER", "Title is................." + window_marker.getTitle());
                        markerTxt.setText(window_marker.getTitle());
                        return false;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean onTapEvent(@NonNull PointF pointF) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(@NonNull PointF pointF) {
            return false;
        }

        @Override
        public void onPinchLocked() {

        }

        @Override
        public boolean onPinchZoomEvent(float v, @NonNull PointF pointF) {
            return false;
        }

        @Override
        public void onRotateLocked() {

        }

        @Override
        public boolean onRotateEvent(float v) {
            return false;
        }

        @Override
        public boolean onTiltEvent(float v) {
            return false;
        }

        @Override
        public boolean onLongPressEvent(@NonNull PointF pointF) {
            return false;
        }

        @Override
        public void onLongPressRelease() {

        }

        @Override
        public boolean onTwoFingerTapEvent(@NonNull PointF pointF) {
            return false;
        }
    };

    /**
     * create a MapCircle and add the MapCircle to active map view.
     */


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createPolyline() {
        ArrayList<GeoCoordinate> coordinates = new ArrayList<>();
        for (int i = 0; i < taskInfoEntities.size(); i++) {
            double longi = Double.parseDouble(taskInfoEntities.get(i).getLongitude());
            double lat = Double.parseDouble(taskInfoEntities.get(i).getLatitude());
            coordinates.add(new GeoCoordinate(lat, longi));
//            Log.e(TAG, "createPolyline: " + coordinates.get(i));
        }
//        coordinates.add(new GeoCoordinate(29.9753092, 77.571972));
//        coordinates.add(new GeoCoordinate(29.9685473, 77.5644495));
//        coordinates.add(new GeoCoordinate(29.9623858, 77.5483282));
//        coordinates.add(new GeoCoordinate(29.9701266, 77.5698228));

        /* Initialize a CoreRouter */
        /*CoreRouter coreRouter = new CoreRouter();

         *//* Initialize a RoutePlan *//*
        RoutePlan routePlan = new RoutePlan();
        RouteOptions routeOptions = new RouteOptions();
        *//* Other transport modes are also available e.g Pedestrian */
        /*
        routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
        */
        /* Disable highway in this route. *//*
        routeOptions.setHighwaysAllowed(false);
        */
        /* Calculate the shortest route available. *//*
        routeOptions.setRouteType(RouteOptions.Type.SHORTEST);
        */
        /* Calculate 1 route. *//*
        routeOptions.setRouteCount(1);
        */
        /* Finally set the route option */

        /*routePlan.setRouteOptions(routeOptions);*/

//The following is needed because of image acceleration in some devices such as samsung
//        Image image = new Image();
//        try {
//            image.setImageResource(R.drawable.pin);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        List<RouteWaypoint> routeWaypoints = new ArrayList<>();
        for (int i = 0; i < coordinates.size(); i++) {
//            SVG svg = SVGParser.getSVGFromString();

            Image image = new Image();

            try {

                image.setImageResource(R.drawable.pin);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            MapLabeledMarker defaultMarker = new MapLabeledMarker(coordinates.get(i), image);
            defaultMarker.setLabelText("eng", "" + (i + 1));
            defaultMarker.setFontScalingFactor(4f);
            defaultMarker.setAnchorPoint(new PointF(image.getWidth() / 2, image.getHeight()));
            defaultMarker.setCoordinate(coordinates.get(i));
            defaultMarker.setTitle(taskInfoEntities.get(i).getName());
            defaultMarker.setVisible(true);
            m_map.addMapObject(defaultMarker);
        }

//        coreRouter.calculateRoute(coordinates, routeOptions, new Router.Listener<List<RouteResult>, RoutingError>() {
//            @Override
//            public void onProgress(int i) {
//
//            }
//
//            @Override
//            public void onCalculateRouteFinished(@Nullable List<RouteResult> routeResults, @NonNull RoutingError routingError) {
//                if (routingError == RoutingError.NONE) {
////                    m_route = routeResults.get(0).getRoute();
////                    mapRoute = new MapRoute(m_route);
////                    m_map.addMapObject(mapRoute);
//                }
//            }
//        });
    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
//        mapView.;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onEngineInitializationCompleted(Error error) {
        if (error == Error.NONE) {
            /*
             * If no error returned from map fragment initialization, the map will be
             * rendered on screen at this moment.Further actions on map can be provided
             * by calling Map APIs.
             */
            m_map = new Map();

            mapView.setMap(m_map);
            double longi, lat;
            if (taskInfoEntities.size() > 0) {

                longi = Double.parseDouble(taskInfoEntities.get(0).getLongitude());
                lat = Double.parseDouble(taskInfoEntities.get(0).getLatitude());
                geoCoordinate = new GeoCoordinate(lat, longi);

            } else {
                geoCoordinate = PositioningManager.getInstance().getLastKnownPosition().getCoordinate();

            }

            m_map.setCenter(geoCoordinate,
                    Map.Animation.BOW);
            m_map.setCenter(geoCoordinate,
                    Map.Animation.BOW);
            /* Set the zoom level to the average between min and max zoom level. */
            m_map.setZoomLevel(13.6);

            m_activity.supportInvalidateOptionsMenu();
            mapView.getMapGesture().addOnGestureListener(onGestureListener, 0, false);
//                        m_map =
            /*MapPolyline mapPolyline =*/
            String jsonLoginResponse = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Constants.PREF_KEY_LOGIN_RESPONSE, "");
            LoginResponse loginResponse = new Gson().fromJson(jsonLoginResponse, LoginResponse.class);
            int isOnduty = loginResponse.getDriverInfo().getOnDuty();

            if (isOnduty == 1) {
                createPolyline();
            } else {

            }
//                        m_map.addMapObject(mapPolyline);
            /*
             * Set up a handler for handling MapMarker drag events.
             */

            mapView.setMapMarkerDragListener(new OnDragListenerHandler());

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


    private class OnDragListenerHandler implements MapMarker.OnDragListener {
        @Override
        public void onMarkerDrag(MapMarker mapMarker) {

            Log.e(TAG, "onMarkerDrag: " + mapMarker.getTitle() + " -> " + mapMarker
                    .getCoordinate());
        }

        @Override
        public void onMarkerDragEnd(MapMarker mapMarker) {
            Log.e(TAG, "onMarkerDragEnd: " + mapMarker.getTitle() + " -> " + mapMarker
                    .getCoordinate());
        }

        @Override
        public void onMarkerDragStart(MapMarker mapMarker) {
            Log.e(TAG, "onMarkerDragStart: " + mapMarker.getTitle() + " -> " + mapMarker
                    .getCoordinate());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                        geoCoordinate = new GeoCoordinate(LocationService.latitude, LocationService.longitude);
                        geoCoordinate = PositioningManager.getInstance().getLastKnownPosition().getCoordinate();
                        m_map.setCenter(geoCoordinate,
                                Map.Animation.BOW);

                        /* Set the zoom level to the average between min and max zoom level. */
                        m_map.setZoomLevel(14);

                        m_activity.supportInvalidateOptionsMenu();
                        m_mapFragment.getMapGesture().addOnGestureListener(onGestureListener, 0, false);
//                        m_map =
                        /*MapPolyline mapPolyline =*/
                        createPolyline();
//                        m_map.addMapObject(mapPolyline);
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

}