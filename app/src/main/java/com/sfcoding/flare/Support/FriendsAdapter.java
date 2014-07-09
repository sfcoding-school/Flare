package com.sfcoding.flare.Support;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sfcoding.flare.Data.Person;
import com.sfcoding.flare.Data.Group;
import com.sfcoding.flare.R;
import com.sfcoding.flare.Support.JsonIO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrea on 09/07/2014.
 */
public class FriendsAdapter extends ArrayAdapter<Person> {
    public ArrayList<Person> friendList;
    private Context context;
    private TextView container_friends;
    private static List<Person> finali = null;

    public FriendsAdapter(Context context,int textViewResourceId, ArrayList<Person> friendList) {
        super(context,textViewResourceId,friendList);
        this.friendList = new ArrayList<Person>();
        this.context = context;
        this.friendList.addAll(friendList);
        if (finali == null)
            finali = new ArrayList<Person>();
    }

    private class ViewHolder {
        CheckBox name;
        ImageView foto_profilo;
        TextView installed;
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
            holder.installed = (TextView) convertView.findViewById(R.id.txt_installed);

            convertView.setTag(holder);

            holder.name.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    Person friends1 = (Person) cb.getTag();

                    if (cb.isChecked()) {
                        finali.add(friends1);
                        Log.e("aggiunto",friends1.getName());
                        }

                     else {
                        //elimina dagli amici se presente
                        Log.e("eliminato",friends1.getName());
                    }}}
           );

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Person friends1 = friendList.get(position);
        holder.name.setText(friends1.getName());
        holder.name.setTextColor(Color.BLACK);
        holder.name.setChecked(false);
        holder.name.setTag(friends1);

        //Gestione foto profilo nella listview
       /* holder.foto_profilo.setImageBitmap(null);
        holder.foto_profilo.setBackground(context.getResources().getDrawable(R.drawable.com_facebook_profile_default_icon));
        if (friends1.photo != null) {
            holder.foto_profilo.setBackground(null);
            holder.foto_profilo.setImageBitmap(friends1.getFoto());
        } else {
            HelperFacebook.getFacebookProfilePicture(friends1, adapter, 0);
        }

        if (container_friends == null) {
            for (int i = 0; i < DatiFriends.ITEMS.size(); i++) {
                if (friends1.code.equals(DatiFriends.ITEMS.get(i).code)) {
                    holder.name.setTextColor(Color.GRAY);
                    holder.foto_profilo.setAlpha(100);
                    holder.installed.setAlpha(100);
                    holder.name.setEnabled(false);
                }
            }
        }*/

        return convertView;
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
