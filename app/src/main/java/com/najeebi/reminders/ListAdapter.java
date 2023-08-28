package com.najeebi.reminders;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<Reminder> {
    //custom adapter for the listview
    public  ListAdapter(Activity context,ArrayList<Reminder> list)
    {
        super(context,0,list);
    }
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
            convertView = View.inflate(getContext(), R.layout.list_item, null);

        Reminder currentReminder = getItem(position);
        //set value for the listview item
        TextView txvVerDesc = convertView.findViewById(R.id.reminderDesc);
        txvVerDesc.setText(currentReminder.getReminderDesc());
        TextView txvVerDate = convertView.findViewById(R.id.reminderDate);
        txvVerDate.setText(currentReminder.getDate());
        TextView txvVerLocation = convertView.findViewById(R.id.reminderLocation);
        txvVerLocation.setText(currentReminder.getLocation());

        return convertView;
    }
}
