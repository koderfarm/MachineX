package com.am.machinex.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.am.machinex.models.PartName;

import java.util.ArrayList;

public class PartNameAdapter extends ArrayAdapter<PartName> {
    // Your sent context
    private Context context;
    // Your custom arrayList for the spinner (User)
    private ArrayList<PartName> arrayList;

    public PartNameAdapter(Context context, int textViewResourceId,
                                  ArrayList<PartName> arrayList) {
        super(context, textViewResourceId, arrayList);
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount(){
        return arrayList.size();
    }

    @Override
    public PartName getItem(int position){
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setGravity(Gravity.CENTER_VERTICAL);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the arrayList array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(arrayList.get(position).getDescription());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(arrayList.get(position).getDescription());

        return label;
    }
}
