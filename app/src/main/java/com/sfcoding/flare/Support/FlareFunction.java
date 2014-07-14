package com.sfcoding.flare.Support;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrea on 13/07/2014.
 */
public class FlareFunction {
    private static final String URL = "http://serene-harbor-2202.herokuapp.com/";

    private static HttpClient httpclient = null;

    public static Boolean FlareSend(final String id_fb, final JSONArray flareTargetList, final String lat,final String lng) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... args) {
                String ris;
                ris = httpPostConnection("flare_send", new String[]{"id_fb", "target_list", "lat","lng"}, new String[]{id_fb, flareTargetList.toString(),lat,lng});
                Log.e("flareSend_ris: ", ris);
                return ris;
            }

            @Override
            protected void onPostExecute(String result) {

            }
        }.execute();
        return true;
    }

    public static Boolean FlareResponse(final String id_fb, final JSONArray id_friend, final String lat,final String lng){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... args) {
                String ris;
                ris = httpPostConnection("flare_response", new String[]{"FB_ID", "target_list", "lat","lng"}, new String[]{id_fb, id_friend.toString(),lat,lng});
                Log.e("sendFlare_ris: ", ris);
                return ris;
            }

            @Override
            protected void onPostExecute(String result) {

            }
        }.execute();
        return true;
    }

    public static Boolean RegisterId(final String id_fb, final String reg_id){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... args) {
                String ris;
                ris = httpPostConnection("register", new String[]{"id_fb","reg_id"}, new String[]{id_fb, reg_id});
                Log.e("register_ris: ", ris);
                return ris;
            }

            @Override
            protected void onPostExecute(String result) {

            }
        }.execute();
        return true;
    }

    public static String httpPostConnection(String url, String[] name, String[] param) {
        httpclient = getHttpclient();
        HttpPost httppost = new HttpPost(URL + url);
        String ris="";
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            for (int i = 0; i < name.length; i++) {
                nameValuePairs.add(new BasicNameValuePair(name[i], param[i]));
            }
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

            HttpResponse response = httpclient.execute(httppost);

           ris= EntityUtils.toString(response.getEntity());

           /* if (temp.equals("session error")) {
                login();
                return httpPostConnection(url, name, param);
            } else {
                return temp;
            }*/

        } catch (ClientProtocolException e) {
            Log.e("ClientProtocolException: ", e.toString());
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncodingException: ", e.toString());
        } catch (IOException e) {
            Log.e("IOException: ", e.toString());
        }
        return  ris;

    }


    static private HttpClient getHttpclient() {
        if (httpclient == null)
            httpclient = new DefaultHttpClient();
        return httpclient;
    }


}
