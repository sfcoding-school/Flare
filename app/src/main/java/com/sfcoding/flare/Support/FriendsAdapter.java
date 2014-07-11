package com.sfcoding.flare.Support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sfcoding.flare.Activity.SelectFriendsActivity;
import com.sfcoding.flare.Data.Person;
import com.sfcoding.flare.Data.Group;
import com.sfcoding.flare.R;
import com.sfcoding.flare.Support.JsonIO;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrea on 09/07/2014.
 */
public class FriendsAdapter extends ArrayAdapter<Person> {
    public ArrayList<Person> friendList;
    private Context context;
    private FriendsAdapter adapter;
    private ArrayList<Person> newchosen;
    private ArrayList<Person> chosen;


    public FriendsAdapter(Context context, int textViewResourceId, ArrayList<Person> friendList,ArrayList<Person> newchosen,ArrayList<Person> chosen) {
        super(context, textViewResourceId, friendList);
        this.friendList = new ArrayList<Person>();
        this.context = context;
        this.friendList.addAll(friendList);
        this.newchosen=newchosen;
        this.chosen=chosen;
    }

    private class ViewHolder {
        CheckBox name;
        ImageView foto_profilo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.friends_row, null);

            holder = new ViewHolder();
            holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
            holder.foto_profilo = (ImageView) convertView.findViewById(R.id.img_profilo);
            convertView.setTag(holder);
            holder.name.setOnClickListener(new View.OnClickListener() {
                                               public void onClick(View v) {
                                                   CheckBox cb = (CheckBox) v;
                                                   Person currentFriend = (Person) cb.getTag();
                                                   if (cb.isChecked()) {
                                                       Log.e("aggiunto", currentFriend.getId());
                                                       currentFriend.setLastLat(0.0);
                                                       currentFriend.setLastLng(0.0);
                                                       chosen.add(currentFriend);
                                                   } else {
                                                       //elimina dagli amici se presente
                                                       Log.e("eliminato", currentFriend.getName());
                                                       chosen.remove(currentFriend);
                                                   }
                                               }
                                           }
            );

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Person currentFriend = friendList.get(position);
        holder.name.setText(currentFriend.getName());
        holder.name.setTextColor(Color.parseColor("#8b9dc3"));
        //Se l amico è già stato selezionato in passato, metto la spunta
        if (Group.searchById(currentFriend.getId())!=null) holder.name.setChecked(true);
        else holder.name.setChecked(false);
        holder.name.setTag(currentFriend);

        //Gestione foto profilo nella listview
       /* holder.foto_profilo.setImageBitmap(null);
        holder.foto_profilo.setBackground(context.getResources().getDrawable(R.drawable.com_facebook_profile_default_icon));
        if (currentFriend.photo != null) {
            holder.foto_profilo.setBackground(null);
            holder.foto_profilo.setImageBitmap(currentFriend.getPhoto());
        } else {
            getFacebookProfilePicture(currentFriend, adapter, 0);
        }*/



        return convertView;
    }
    public static void getFacebookProfilePicture(final Person friends, final Adapter adapter, final int chi) {
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... args) {
                Bitmap bitmap = friends.getPhoto();
                if (bitmap == null) {
                    URL imageURL;

                    try {
                        imageURL = new URL("https://graph.facebook.com/" + friends.getId() + "/picture?type=small");
                        bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    friends.setPhoto(bitmap);
                    if (chi == 0) {
                        ((FriendsAdapter) adapter).notifyDataSetChanged();
                    } else {
                        ((FriendsAdapter) adapter).notifyDataSetChanged();
                    }
                }
            }
        }.execute();
    }

    //Aggiungo gli amici scelti all'activity e alla lista "finali"
   /* private void delete_friend_to_activity(String toDelete) {

        Friends friends1;

        for (int i = 0; i < finali.size(); i++) {
            friends1 = finali.get(i);
            if (friends1.getName().equals(toDelete)) {
                finali.remove(i);
            }
        }

        if (container_friends != null) {
            container_friends.setText("");
            for (int i = 0; i < finali.size(); i++) {
                friends1 = finali.get(i);
                if (i == 0) {
                    container_friends.append(friends1.getName());
                } else {
                    container_friends.append(", " + friends1.getName());
                }
            }
        }
    }*/
}
