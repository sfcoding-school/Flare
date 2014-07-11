package com.sfcoding.flare.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.Facebook;
import com.facebook.android.Util;
import com.facebook.model.GraphMultiResult;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;
import com.sfcoding.flare.Data.Group;
import com.sfcoding.flare.Data.Person;
import com.sfcoding.flare.R;
import com.sfcoding.flare.Support.FriendsAdapter;
import com.sfcoding.flare.Support.JsonIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectFriendsActivity extends Activity {
    ListView listView;
    ArrayList<Person> friendsList;
    List<GraphUser> friends;
    FriendsAdapter dataAdapter;
    ArrayList<Person> newchosen=new ArrayList<Person>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friends);
        listView = (ListView) findViewById(R.id.listFriends);
        initView();
    }

    private void initView() {
        //setto networkInfo per controllo accesso a internet
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //Controllo sessione FB
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            //se c'è la sessione e internet accessibile richiedo subito la lista amici
            if (networkInfo != null && networkInfo.isConnected()) {
                requestMyAppFacebookFriends(session);


            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SelectFriendsActivity.this);
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setMessage(getString(R.string.no_connection));
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.errFB), Toast.LENGTH_LONG).show();
        }
    }

    //Richiesta amici FB
   private void requestMyAppFacebookFriends(Session session) {
        Request friendsRequest = createRequest(session);

        friendsRequest.setCallback(new Request.Callback() {

            @Override
            public void onCompleted(Response response) {
                friends = getResults(response);
                friendsList = new ArrayList<Person>();
                //GraphUser use = friends.get(1);
                //Log.e("utente", use.getUsername());
                for (GraphUser user : friends) {
                    Person friend = new Person();
                    friend.setId(user.getId());
                    friend.setName(user.getName());
                    Log.e("amico", friend.getId());
                    friendsList.add(friend);
                }
                dataAdapter = new FriendsAdapter(SelectFriendsActivity.this, R.layout.friends_row, friendsList,newchosen,Group.Friends);
                listView.setAdapter(dataAdapter);
            }
        });
       /*new Request(
               session,
               "/me/friends",
               null,
               HttpMethod.GET,
               new Request.Callback() {
                   public void onCompleted(Response response) {
                       friends = getResults(response);
                       for (GraphUser user : friends) {
                           Person friend = new Person();
                           friend.setId(user.getId());
                           friend.setName(user.getName());
                           Log.e("amico", friend.getId());
                           //friendsList.add(friend);
                       }
                   }
               }
       ).executeAsync();*/
        friendsRequest.executeAsync();
    }


    private Request createRequest(Session session) {
        Request request = Request.newGraphPathRequest(session, "/me/friends", null);
        Set<String> fields = new HashSet<String>();
        String[] requiredFields = new String[]{"id", "name","installed"};
        fields.addAll(Arrays.asList(requiredFields));
        Bundle parameters = request.getParameters();
        parameters.putString("fields", TextUtils.join(",", fields));
        request.setParameters(parameters);
        return request;
    }

    private List<GraphUser> getResults(Response response) {
        GraphMultiResult multiResult = response
                .getGraphObjectAs(GraphMultiResult.class);
        GraphObjectList<GraphObject> data = multiResult.getData();
        return data.castToListOf(GraphUser.class);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.done) {
            try {
                JsonIO.saveFriends(Group.Friends,getApplicationContext(),"friends");
                JsonIO.loadFriends("friends",getApplicationContext());
                //Log.e("nuovo", Group.Friends.get(0).getId());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
