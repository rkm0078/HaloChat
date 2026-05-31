package com.rishabh.chatapp;

import android.content.Context;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {

    Context context;
    ArrayList<User> list;

    public UserAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() { return list.size(); }

    @Override
    public Object getItem(int i) { return list.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        }

        ImageView img = view.findViewById(R.id.profileImage);
        TextView name = view.findViewById(R.id.username);

        User user = list.get(i);

        name.setText(user.username);

        // 🔥 Load image
        if (user.profileImage != null && !user.profileImage.isEmpty()) {
            Glide.with(context).load(user.profileImage).into(img);
        } else {
            img.setImageResource(R.mipmap.ic_launcher);
        }

        return view;
    }
}