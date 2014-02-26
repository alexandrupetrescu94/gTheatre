package com.example.gtheatre;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

 
public class MainActivity extends FragmentActivity 
				implements GooglePlayServicesClient.ConnectionCallbacks,
				GooglePlayServicesClient.OnConnectionFailedListener,
				LocationListener{
	
	private int userIcon,blueIcon;
	private GoogleMap theMap;
	private Marker userMarker;
	private Location myLoc;
	private LocationClient myLocCl;
	private LatLng	lastLatLng;
	private double lat,lng;
	private  LocationRequest mLocationRequest;
	private ProgressBar mActivityIndicator;
	
    private static final int UPDATE_INTERVAL_IN_SECONDS = 60;
    private static final long UPDATE_INTERVAL = 1000 * UPDATE_INTERVAL_IN_SECONDS;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 10;
    private static final long FASTEST_INTERVAL = 1000 * FASTEST_INTERVAL_IN_SECONDS;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        		
        userIcon = R.drawable.yellow_point;
        blueIcon = R.drawable.blue_point;
        
        if(theMap==null){
            //map not instantiated yet
        	theMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        }
        
        if(theMap != null){
            //ok - proceed
        	theMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

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
		theMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng,17), 3000, null);
		
		myLocCl.requestLocationUpdates(mLocationRequest, this);
	}
	
	@Override
	public void onDisconnected() {
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {	
	}
	
	@Override
	public void onLocationChanged(Location location) {
		String msg = "Updated Location: " +
	            Double.toString(location.getLatitude()) + "," +
	            Double.toString(location.getLongitude());
	    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();		
	}
	
	//Not actually needed,just for getting a location having coords
	private class GetAddressTask extends AsyncTask<Location, Void, String>{
		Context mContext;
		public GetAddressTask(Context context)
		{
			mContext = context;
		}	
		@Override
		protected String doInBackground(Location... params) {
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
			Location loc = params[0];
			List<Address> addresses = null;
			try{
				addresses = geocoder.getFromLocation(loc.getLatitude() , loc.getLongitude(), 1);
			}
			catch(IOException e1){ return "IOException trying to get address";}
			catch(IllegalArgumentException e2){ return "No address for your coordinates found.";}
			if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String addressText = String.format("%s %s %s",
                		address.getAddressLine(0),
                		address.getLocality(),
                		address.getCountryName());
                return addressText;
			}
			else
			{
				return "No address found";
			}
		}
		protected void onPostExecute(String address){
			mActivityIndicator.setVisibility(View.GONE);
			Toast.makeText(mContext, address , Toast.LENGTH_LONG).show();
		}
	}
	
	public void getAddress(View v) {
		//BUTTON ADDRESS AND PROGRESS BAR 
		if ( Geocoder.isPresent() ){
		mActivityIndicator = (ProgressBar) findViewById(R.id.address_progress);
		mActivityIndicator.setVisibility(View.VISIBLE);
		(new GetAddressTask(this)).execute(myLoc);
		}	
	}
	//
	
	
	
	// TODO Starting setting if gMaps isn't enabled
	// TODO newLatLngBounds instead of newLatLngZoom
}
