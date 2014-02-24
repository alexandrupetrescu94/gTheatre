package com.example.gtheatre;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Location;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
 
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
public class MainActivity extends Activity 
				implements GooglePlayServicesClient.ConnectionCallbacks,
				GooglePlayServicesClient.OnConnectionFailedListener {
	
	private int userIcon,blueIcon;
	private GoogleMap theMap;
	private Marker userMarker;
	private Location myLoc;
	private LocationClient myLocCl;
	private LatLng	lastLatLng;
	private double lat,lng;
	private String placesSearchStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        userIcon = R.drawable.yellow_point;
        blueIcon = R.drawable.blue_point;
        
        if(theMap==null){
            //map not instantiated yet
        	theMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        }
        
        if(theMap != null){
            //ok - proceed
        	theMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        	myLocCl = new LocationClient(getApplicationContext(),this,this);
        	// once we have the reference to the client, connect it
        	if(myLocCl != null)
        	  myLocCl.connect(); 
        }
    }
 	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	@Override
	public void onConnected(Bundle arg0) {
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		myLoc = myLocCl.getLastLocation();
		lat = myLoc.getLatitude();
		lng = myLoc.getLongitude();
		lastLatLng = new LatLng(lat,lng);
		
		if (userMarker != null) userMarker.remove();
		userMarker = theMap.addMarker(new MarkerOptions()
							.position(lastLatLng)
							.title("You are here!")
							.icon(BitmapDescriptorFactory.fromResource(userIcon))
							.snippet("Your last recorded location"));
		theMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng,20), 3000, null);
	}
	@Override
	public void onDisconnected() {
	
	}
		@Override
	public void onConnectionFailed(ConnectionResult arg0) {	
	}
	// TODO GetFirstIfConnected / onDisconnected method
	// TODO newLatLngBounds instead of newLatLngZoom
	// TODO LocationRequest needed?
	//http://www.codeproject.com/Articles/665527/A-GPS-Location-Plotting-Android-Application
}
