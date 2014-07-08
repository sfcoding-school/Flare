package com.sfcoding.flare.Support;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sfcoding.flare.Data.Person;
import com.sfcoding.flare.Data.Group;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Andrea on 07/07/2014.
 */
public class JsonIO {
    public static void writeJsonOnFile(Context context, JSONArray array) throws JSONException {
        JSONObject dato = new JSONObject();
        dato.put("num", 500);
        Log.e("object", dato.getString("num"));
        JSONArray dati = new JSONArray();
        dati.put(dato);
        Log.e("json:", dati.getJSONObject(0).getString("num"));
    }

    //called by saveFriends
    public static JSONArray fromFriendsToJson(ArrayList<Person> friends) throws JSONException {
        JSONArray array = new JSONArray();
        int n = friends.size();
        Log.e("num", Integer.toString(n));
        for (int i = 0; i < n; i++) {
            Person person = friends.get(i);
            JSONObject jPerson = new JSONObject();
            jPerson.put("ID", person.id);
            jPerson.put("name", person.name);
            jPerson.put("lastLat", person.lastLat);
            jPerson.put("lastLng", person.lastLng);
            array.put(jPerson);
        }
        Log.e("jsonObj", array.getJSONObject(0).getString("name"));
        return array;
    }

    public static void saveFriends(ArrayList<Person> friends, Context context, String fileName) throws JSONException {
        try {
            JSONArray array = fromFriendsToJson(friends);
            String jsonString = array.toString();
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(jsonString.getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e("DataProvide", "IOException saveJsonToFile: " + fileName + " " + e);
        } catch (NullPointerException e) {
            Log.e("DataProvide", "NullPointerException saveJsonToFile: " + fileName + " " + e);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray fileToJson(String fileName, Context context) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            fis.close();
            JSONArray res= new JSONArray(sb.toString());
            return new JSONArray(sb.toString());

        } catch (IOException e) {
            Log.e("DATA_PROVIDE-loadJsonFromFile ", fileName + " " + e.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Person> loadFriends(String fileName, Context context) throws JSONException {
        JSONArray jsonArray=fileToJson(fileName,context);
        JSONObject object;
        ArrayList<Person> arrayList= new ArrayList<Person>();
        for (int i=0;i<jsonArray.length();i++){
            object=jsonArray.getJSONObject(i);
            Person person=new Person();
            person.name=object.getString("name");
            person.id=object.getString("ID");
            person.lastLat=object.getDouble("lastLat");
            person.lastLng=object.getDouble("lastLng");
            arrayList.add(person);
        }
        return arrayList;
    }
}
