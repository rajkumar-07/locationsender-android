package com.rk.locationsenderv1;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.location.LocationManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import android.Manifest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;

    private GpsTracker gpsTracker;
    private Button getLocation;

    private Button makeRequestBtn;

    private Map<String,Double> locationMap = new HashMap<>();
    private static final String API_URL = "http://192.168.194.230:8090/print-logs";

    private static  final String POST_API_URL="http://192.168.194.230:8090/receive-location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        getLocation = findViewById(R.id.get_location_btn);
        makeRequestBtn = findViewById(R.id.make_request_btn);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    getLocation(v);

            }
        });

        makeRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                makeAPIRequest();
                postRequest();
            }
        });
    }

    public void getLocation(View view){
        gpsTracker = new GpsTracker(MainActivity.this);
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            Toast.makeText(MainActivity.this, "latitude:" + latitude + "longitude:"+ longitude, Toast.LENGTH_SHORT).show();
            locationMap.put("latitude",latitude);
            locationMap.put("longitude",longitude);
            System.out.println("latitude:" + latitude + "longitude:"+ longitude);

        }else{
            gpsTracker.showSettingsAlert();
        }
    }

    private void makeAPIRequest()  {

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the string response here
                        System.out.println("Response: " + response); // Log the response
                        // Further process the response as needed

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle network errors
                    }
                });

// Add the request to the Volley queue
        queue.add(request);
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with location access
            } else {
                // Permission denied, handle the case
            }
        }
    }

    public void postRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject locationJsonObject = new JSONObject(locationMap);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, POST_API_URL,
                locationJsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the JSON response here
                        System.out.println("Response: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle network errors
                    }
                });
        System.out.println(jsonObjectRequest.toString());
        // Add the request to the Volley queue
        queue.add(jsonObjectRequest);
    }


}