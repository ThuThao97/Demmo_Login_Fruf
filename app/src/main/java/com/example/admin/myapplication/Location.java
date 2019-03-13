package com.example.admin.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.myapplication.Adapter.listviewAdapter;
import com.example.admin.myapplication.model.ListOffice;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Location extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private ArrayList<ListOffice> arrayList;
    private Marker marker;
    private ListView lv_item, lv;
    private ImageView icon_back;
    private android.location.Location myLocation = null;
    private TextView South_Show, Central_Show, Normal_Show;
    private LinearLayout layout_South, layout_Central, layout_Normal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        onSetUpView();
        onClickListener();
        loadGoogleMap();
    }

    private void onSetUpView() {
        lv = findViewById(R.id.lv_item);
        South_Show = findViewById(R.id.South_Show);
        Normal_Show = findViewById(R.id.Normal_Show);
        Central_Show = findViewById(R.id.Central_Show);
        layout_Central = findViewById(R.id.layout_Central);
        layout_Normal = findViewById(R.id.layout_Normal);
        layout_South = findViewById(R.id.layout_South);
        lv_item = findViewById(R.id.lv_item);
        icon_back=findViewById(R.id.back);
        //loadGoogleMap();
    }

    private void loadGoogleMap() {
        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    onMyMapReady(googleMap);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void onMyMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        mMap.setOnMyLocationClickListener(onMyLocationClickListener);
        enableMyLocationIfPermitted();

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(7);
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                if(arrayList.size()>1){
//                    arrayList.clear();
//                    mMap.clear();
//                }
//                arrayList.add(latLng);
//                MarkerOptions options=new MarkerOptions();
//                options.position(latLng);
//                if(arrayList.size()==1){
//                    options.icon()
//                }
//
//            }
//        });
    }

    private void onClickListener() {
        layout_South.setOnClickListener(this);
        layout_Central.setOnClickListener(this);
        layout_Normal.setOnClickListener(this);
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Location.this,MainActivity.class);
                startActivity(intent);
            }
        });
        lv_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mMap.clear();
                    displayMyLocation(myLocation);
                    if (arrayList.get(position).latitude != null && !arrayList.get(position).longtitude.equals("") &&
                            arrayList.get(position).latitude != null && !arrayList.get(position).latitude.equals("")) {
                        LatLng destFrom = new LatLng(Double.parseDouble(arrayList.get(position).latitude), Double.parseDouble(arrayList.get(position).longtitude));
                        if (myLocation != null) {
                            LatLng destTo = new LatLng(Double.parseDouble(myLocation.getLatitude() + ""), Double.parseDouble(myLocation.getLongitude() + ""));
                            showDiretion(destTo, destFrom, "", arrayList.get(position).name);
                        }
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(destFrom)
                                .title(arrayList.get(position).name)
                                .snippet(arrayList.get(position).address));
                        selectedMarker(destFrom, marker);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void selectedMarker(LatLng location, Marker marker) {
        if (location != null && marker != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(location)
                    .zoom(15)
                    .bearing(90)
                    .tilt(40)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            marker.showInfoWindow();
        }
    }

    public void showDiretion(LatLng destTo, LatLng destFrom, String title, String snippet) {
        if (destTo != null && destFrom != null) {
            mMap.clear();
            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(destTo, destFrom);
            //Log.e("Url for Api Diretion: ", url);
            DownloadTask downloadTask = new DownloadTask();
            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        } else {
            Toast.makeText(Location.this, "False Points", Toast.LENGTH_SHORT).show();
        }
    }

    protected Marker createMarker(double longtitude, double latitude, String title) {
        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longtitude))
                .anchor(0.5f, 0.5f)
                .title(title));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_Normal:
                //Toast.makeText(this, "sai rồi nhé", Toast.LENGTH_SHORT).show();
                //xoa marker trên map
                mMap.clear();
                showNormal();
                break;
            case R.id.layout_Central:
                mMap.clear();
                showCentral();
                break;
            case R.id.layout_South:
                mMap.clear();
                showSouth();
                break;
            default:
                break;
        }
    }
    //    South
    private void showSouth() {
        try {
            South_Show.setVisibility(View.VISIBLE);
            Normal_Show.setVisibility(View.INVISIBLE);
            Central_Show.setVisibility(View.INVISIBLE);
            arrayList = new ArrayList<ListOffice>();
            arrayList.add(new ListOffice("13.7713852", "109.2188491", "vitop4", "192 Nguyễn Thái Học-Ngô Mây-TP.Quy Nhơn", "190081490"));
            arrayList.add(new ListOffice("13.7648314", "109.2159472", "vitop4", "181 Nguyễn Thị Minh Khai-Ngô Mây-TP.Quy Nhơn", "190081490"));
            arrayList.add(new ListOffice("13.778334", "109.2288899", "vitop4", "2-4 Mai Xuân Thưởng-Trần Hưng Đaoh-TP.Quy Nhơn", "190081490"));
            listviewAdapter adapter = new listviewAdapter(Location.this, R.layout.item, arrayList);
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            for (int i = 0; i < arrayList.size(); i++) {
                createMarker(Double.parseDouble(arrayList.get(i).latitude), Double.parseDouble(arrayList.get(i).longtitude), arrayList.get(i).name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Central
    private void showCentral() {
        try {
            Central_Show.setVisibility(View.VISIBLE);
            Normal_Show.setVisibility(View.INVISIBLE);
            South_Show.setVisibility(View.INVISIBLE);
            arrayList = new ArrayList<ListOffice>();
            arrayList.add(new ListOffice("13.774426", "109.223440", "vitop4", "Lê Xuân Trữ-Lý Thường Kiệt-TP.Quy Nhơn", "190081490"));
            arrayList.add(new ListOffice("13.773304", "109.219939", "vitop3", "49 Nguyễn Thái Học-Lê Hồng Phong-TP.Quy Nhơn", "190081490"));
            arrayList.add(new ListOffice("13.768853", "109.222934", "vitop2", "65A Hoàng Diệu-Trần Phú-TP.Quy Nhơn", "190081490"));
            listviewAdapter adapter = new listviewAdapter(Location.this, R.layout.item, arrayList);
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            for (int i = 0; i < arrayList.size(); i++) {
                createMarker(Double.parseDouble(arrayList.get(i).latitude), Double.parseDouble(arrayList.get(i).longtitude), arrayList.get(i).name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Normal
    private void showNormal() {
        try {
            Central_Show.setVisibility(View.INVISIBLE);
            Normal_Show.setVisibility(View.VISIBLE);
            South_Show.setVisibility(View.INVISIBLE);
            arrayList = new ArrayList<ListOffice>();
            arrayList.add(new ListOffice("13.761793", "109.221230", "vitop3", "An Dương Vương-Lý Thường Kiệt-TP.Quy Nhơn", "190081490"));
            arrayList.add(new ListOffice("13.769414", "109.223439", "vitop9", "Lý Thường Kiệt-TP.Quy Nhơn", "190081490"));
            arrayList.add(new ListOffice("13.770290", "109.215356", "vitop6", "341-327 Hoàng Văn Thụ-Ngô Mây-TP.Quy Nhơn", "190081490"));
            listviewAdapter adapter = new listviewAdapter(Location.this, R.layout.item, arrayList);
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            for (int i = 0; i < arrayList.size(); i++) {
                createMarker(Double.parseDouble(arrayList.get(i).latitude), Double.parseDouble(arrayList.get(i).longtitude), arrayList.get(i).name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        mMap.setOnMyLocationClickListener(onMyLocationClickListener);
        enableMyLocationIfPermitted();

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(11);
    }

    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void displayMyLocation(android.location.Location myLocation) {
        try {
            if (myLocation != null) {
                LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                //map.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)             // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                createMarker(myLocation.getLatitude(), myLocation.getLongitude(), "hehe");

                // Thêm Marker cho Map:
                MarkerOptions option = new MarkerOptions();
                //option.title("Vị trí hiện tại");
                //option.snippet("....");
                //option.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_red));
                option.position(latLng);
                Marker currentMarker = mMap.addMarker(option);
                currentMarker.showInfoWindow();
            } else {
                Toast.makeText(this, "Vui lòng bật GPS !", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//show vị tri hiện tại
    private void showDefaultLocation() {
        Toast.makeText(this, "Location permission not granted, " +
                        "showing default location",
                Toast.LENGTH_SHORT).show();
        LatLng redmond = new LatLng(47.6739881, -122.121512);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(redmond));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocationIfPermitted();
                } else {
                    showDefaultLocation();
                }
                return;
            }
        }
    }

    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mMap.setMinZoomPreference(10);
                    return false;
                }
            };

    private GoogleMap.OnMyLocationClickListener onMyLocationClickListener =
            new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull android.location.Location location) {
                    mMap.setMinZoomPreference(12);
                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(new LatLng(location.getLatitude(), location.getLongitude()));
                    circleOptions.radius(200);
                    circleOptions.fillColor(Color.RED);
                    circleOptions.strokeWidth(6);
                    mMap.addCircle(circleOptions);
                }
            };

    //direction

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        //Key
        String key = "&key=" + getResources().getString(R.string.key_direction);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+ key;
        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask=new ParserTask();
            parserTask.execute(result);
        }
    }
    //A class to parse the Google Places in JSON format
    private class ParserTask extends AsyncTask<String, Integer,List<List<HashMap<String,String>>>>{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jsonObject;
            List<List<HashMap<String,String>>> router=null;
            try{
                jsonObject=new JSONObject(jsonData[0]);
                DirectionJSONParser parser=new DirectionJSONParser();
                router=parser.parse(jsonObject);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return router;
        }
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng>points=null;
            PolylineOptions lineOptions=null;
            MarkerOptions markerOptions=new MarkerOptions();
            String distance="";
            String duration="";
            if(result.size()<1){
                //Toast.makeText(activity, "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(15);
                lineOptions.color(Color.RED);
            }

            //Log.e(TAG, "--->" + "Distance:" + distance + ", Duration:" + duration);
            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

}

