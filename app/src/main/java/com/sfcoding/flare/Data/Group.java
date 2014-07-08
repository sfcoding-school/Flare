package com.sfcoding.flare.Data;

import java.util.ArrayList;

import com.sfcoding.flare.Data.Person;

/**
 * Created by Andrea on 06/07/2014.
 */
public class Group {
    public static ArrayList<Person> Friends = new ArrayList<Person>();

    public static Person findFriend(String id){
        for(int i=0;i<Friends.size();i++)
            if(Friends.get(i).id==id) return Friends.get(i);
        return null;
    }

    public static Boolean rmFriend(String id){
        return Friends.remove(findFriend(id));
    }

    public static void addFriend(Person friend) {
        Friends.add(friend);
    }

    public static void removeFriend(int i) {
        Friends.remove(i);
    }

    public static void removeAll() {
        Friends.removeAll(Friends);
    }
}
