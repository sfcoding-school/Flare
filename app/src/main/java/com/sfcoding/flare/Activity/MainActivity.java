package com.sfcoding.flare.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import com.facebook.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.sfcoding.flare.Data.Group;
import com.sfcoding.flare.Data.Person;
import com.sfcoding.flare.R;
import com.sfcoding.flare.Support.JsonIO;
import com.sfcoding.flare.Support.FlareFunction;


import org.json.JSONArray;
import org.json.JSONException;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static Location mLocation;

    static final String TAG = "GCMDemo";
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    GoogleCloudMessaging gcm;
    Context context;
    LocationClient mLocationClient;
    private GoogleMap mMap;
    LatLng mLatLng;
    String id_fb;
    public static Handler handlerService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Session session = getSession(this);
        try {
            JsonIO.loadFriends("friends", getApplicationContext());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!session.isOpened()) {
            Intent newact = new Intent(this, ProfileActivity.class);
            startActivity(newact);
        }
        try {
            JsonIO.loadFriends("friends", getApplicationContext());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            final String sender_id = extras.getString("id_fb");
            if (!sender_id.equals("")) {
                flareDialog(sender_id);

            }
        }

        //Riceve i messaggi inviati dall intentService
        handlerService = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("SERVICEHANDLER", "arrivato il messaggio " + msg.toString());
                Bundle b = msg.getData();
                mMap.clear();
                placeMarker();
                if (b.getInt("dialog") == 1) {
                    Log.e("op", "aggiorno");
                    final String sender_id = b.getString("sender_id");
                    if (!sender_id.equals("")) {
                        flareDialog(sender_id);
                    }
                }

            }

        };

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        getKeyHash();
        Button tiny = (Button)findViewById(R.id.flare);
        tiny.setBackgroundResource(R.drawable.button);
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(this, this, this);
        mLocationClient.connect();


        //GCM
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);

        } else
            Log.e("Errore: ", "No valid Google Play Service APK found.");

    }

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        mLocation = mLocationClient.getLastLocation();
        SharedPreferences preferences = getSharedPreferences("com.sfcoding.flare", Context.MODE_PRIVATE);
        //salvo la mia posizione sulle preferences così da poterla avere sull onCreate
        preferences.edit().putString("lat", Double.toString(mLocation.getLatitude())).apply();
        preferences.edit().putString("lng", Double.toString(mLocation.getLongitude())).apply();


    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.e("error:", Integer.toString(connectionResult.getErrorCode()));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences pref = getSharedPreferences("com.sfcoding.flare", Context.MODE_PRIVATE);
        id_fb = pref.getString("id_fb", "");
        Double lat = Double.parseDouble(pref.getString("lat", "-100"));
        Double lng = Double.parseDouble(pref.getString("lng", "-100"));
        Log.e("my_lat", Double.toString(lat));
        if (lat != -100) {
            mLatLng = new LatLng(lat, lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 18));
        }

        Session session = getSession(this);
        if (!session.isOpened()) {
            Intent newact = new Intent(this, ProfileActivity.class);
            startActivity(newact);
        }
        try {
            JsonIO.loadFriends("friends", getApplicationContext());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //POSIZIONO I MARKER IN BASE ALLE ULTIME INFO
        placeMarker();


    }

    public void flareDialog(final String sender_id){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Flare Response");

        // Setting Dialog Message
        alertDialog.setMessage("Rispondere al flare di " + Group.searchById(sender_id).getName());

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.flare);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                FlareFunction.FlareResponse(id_fb, sender_id, Double.toString(mLocation.getLatitude()), Double.toString(mLocation.getLongitude()));

            }
        });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    //Apre il json con gli amici e le loro ultime posizioni e piazza i marker sulla mappa
    public void placeMarker() {
        try {
            JsonIO.loadFriends("friends", getApplicationContext());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (Person person : Group.Friends) {
            if (person.lastLat != 91) {
                LatLng fLatLng = new LatLng(person.getLastLat(), person.getLastLng());
                mMap.addMarker(new MarkerOptions()
                        .position(fLatLng)
                        .title(person.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
            }
        }
    }

    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.select_friends) {
            Intent intent = new Intent(this, SelectFriendsActivity.class);
            startActivity(intent);

        }
        if (id == R.id.fb) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    public static String getToken() {
        Activity activity = new MainActivity();
        Session session = getSession(activity);
        String token = session.getAccessToken();
        Log.e("TOKEN - getToken", token);
        return token;
    }

    public void onClick(final View view) {
        // Perform action on click
        if (view == findViewById(R.id.flare)) {
            if (Group.dim == 0) {
                //se non ho scelto ancora deglki amici manda una label
                CharSequence text = "Scegli gli amici prima";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            } else {

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String msg = "";
                        mLocationClient.disconnect();
                        mLocationClient.connect();
                        String mLat = Double.toString(mLocation.getLatitude());
                        String mLng = Double.toString(mLocation.getLongitude());
                        Log.e("mie pos", mLat + " " + mLng);

                        JSONArray jsonArray = JsonIO.fileToJson("friends", getApplicationContext());
                        FlareFunction.FlareSend(id_fb, jsonArray, mLat, mLng);

                        return msg;
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                        Log.e("send_result", msg);
                    }
                }.execute(null, null, null);
            }
        }
    }


    //get hash key
    // Add code to print out the key hash
    public void getKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.sfcoding.flare", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("MY KEY HASH:", sign);
                //Toast.makeText(getApplicationContext(), sign, Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("ProfileActivity:", "PackageManager.NameNotFoundExceptio" + e);
            //Toast.makeText(getApplicati
        } catch (NoSuchAlgorithmException e) {
            Log.e("ProfileActivity:", "NoSuchAlgorithmException" + e);
        }
    }


    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public static Session getSession(Activity activity) {
        Session session = Session.getActiveSession();
        if (session == null) {
            session = new Session(activity);
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(activity));
            }
        }
        return session;
    }


}
