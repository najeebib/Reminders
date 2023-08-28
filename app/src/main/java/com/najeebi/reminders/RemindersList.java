package com.najeebi.reminders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RemindersList extends AppCompatActivity {
    private ListView listview;
    private ArrayList<Reminder> data;
    private Cursor cursor;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders_list);
        if (isPermissionToReadGPSLocationOK()) {
            Log.d("mylog", "OK - GPS Enabled & Location Permission Granted!");
        }
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Location listener
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("mylog", "onLocationChanged -> " + location.getLatitude() + "," + location.getLongitude());
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Reminders").orderBy("Time").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                        {
                            @Override
                            public void onSuccess(QuerySnapshot documentSnapshots)
                            {
                                for (QueryDocumentSnapshot document : documentSnapshots)
                                {
                                    //if the reminder has location enable
                                    if((boolean) document.get("Location_enabled"))
                                    {
                                        //get the location
                                        Location l = new Location("");
                                        l.setLatitude(Double.parseDouble(document.get("Latitude").toString()));
                                        l.setLongitude(Double.parseDouble(document.get("Longitude").toString()));
                                        Date timeStamp = ((com.google.firebase.Timestamp) document.get("Time")).toDate();
                                        //if the reminder is close to the current location, activate an alarm for this reminder
                                        if(location.distanceTo(l) < 30 &&(System.currentTimeMillis() < timeStamp.getTime()))
                                        {
                                            Log.d("mylog","near");

                                            String title = document.get("Title").toString();
                                            int alarmId = Integer.parseInt(document.get("AlarmID").toString());
                                            String docId = document.getId();
                                            String type = document.get("Type").toString();
                                            boolean repeating = (boolean)document.get("Repeating");
                                            int numOfminutes = Integer.parseInt(document.get("number_of_minutes").toString());
                                            setUpAlarm(timeStamp,title,alarmId,docId,type,repeating,numOfminutes);
                                        }
                                        else
                                        {//if the location changed, cancel any location reminder that isnt close to the new location
                                            int alarmId = Integer.parseInt(document.get("AlarmID").toString());
                                            String type = document.get("Type").toString();
                                            if(isAlarmOn(alarmId,type))
                                                cancelAlarm(alarmId,type);
                                        }
                                    }

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener()
                        {
                            @Override
                            public void onFailure(Exception e)
                            {
                                String msg = "Error getting documents.\n" + e.toString();
                                Log.d("mylog",msg);
                            }
                        });
            }
            @Override
            public void onProviderEnabled(String provider) {
                Log.d("mylog", "onProviderEnabled -> " + provider);
            }
            @Override
            public void onProviderDisabled(String provider) {
                Log.d("mylog", "onProviderDisabled -> " + provider);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100, locationListener);
        SharedPreferences prefs = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        int i = prefs.getInt("AlarmID",-1);
        if(i == -1)
        {
            prefs.edit().putInt("AlarmID",0).apply();
            i = 0;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        data = new ArrayList<Reminder>();
        adapter = new ListAdapter(this,data);
        listview = findViewById(R.id.RemindersListView);
        listview.setAdapter(adapter);
        //get the reminders from firebase and put it in the list
        db.collection("Reminders").orderBy("Time").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots)
                    {
                        for (QueryDocumentSnapshot document : documentSnapshots)
                        {
                            SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Date timeStamp = ((com.google.firebase.Timestamp) document.get("Time")).toDate();
                            Reminder rem = new Reminder(document.get("Title").toString(),sfd.format(timeStamp),document.get("Location").toString(),
                                    document.getId(),(boolean) document.get("Repeating"),Integer.parseInt(document.get("number_of_minutes").toString()),
                                    Integer.parseInt(document.get("AlarmID").toString()),document.get("Type").toString(),
                                    (boolean) document.get("Location_enabled"),Double.parseDouble(document.get("Latitude").toString()),
                                    Double.parseDouble(document.get("Longitude").toString()));
                            data.add(rem);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(Exception e)
                    {
                        String msg = "Error getting documents.\n" + e.toString();
                        Log.d("mylog",msg);
                    }
                });
        adapter.notifyDataSetChanged();
        //Long click listener, delete the reminder from list, firebase and cancel the alarm
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                AlertDialog.Builder mydialog = new AlertDialog.Builder(RemindersList.this);
                mydialog.setTitle("Are you sure you want to delete this reminder?");
                mydialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Reminder del = data.get(pos);
                        int AlarmID = del.getAlarmID();
                        String type = del.getType();
                        if(isAlarmOn(AlarmID,type))
                            cancelAlarm(AlarmID,type);
                        db.collection("Reminders").document(del.getID()).delete();
                        data.remove(pos);
                        adapter.notifyDataSetChanged();
                    }
                });
                mydialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                mydialog.show();
                return true;
            }
        });
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d("mylog", ">>> onStart()");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("mylog", ">>> onResume()");
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        Log.d("mylog", ">>> onRestart()");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d("mylog", ">>> onPause()");

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d("mylog", ">>> onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("mylog", ">>> onDestroy()");
    }


    public void cancelAlarm(int AlarmID,String type)
    {
        if(isAlarmOn(AlarmID,type))//if the alarm is on, cancel it
        {
            if(type.equals("Notification"))
            {
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent alarmIntent = new Intent(this, ReminderNotificationReceiver.class);
                PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this, AlarmID, alarmIntent, PendingIntent.FLAG_NO_CREATE);
                alarmManager.cancel(alarmPendingIntent);
                alarmPendingIntent.cancel();
            }
            if(type.equals("Alarm"))
            {
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent alarmIntent = new Intent(this, ReminderAlarmReceiver.class);
                PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this, AlarmID, alarmIntent, PendingIntent.FLAG_NO_CREATE);
                alarmManager.cancel(alarmPendingIntent);
                alarmPendingIntent.cancel();
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//display three dots option menue
        super.onCreateOptionsMenu(menu);
        MenuItem item1 = menu.add("Settings");
        MenuItem item2 = menu.add("About Us");
        MenuItem item3 = menu.add("Exit");
        item1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                goToSettings();
                return false;
            }
        });
        item2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                DisplayAboutUs();
                return false;
            }
        });
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                finish();
                System.exit(0);
                return true;
            }
        });
        return true;
    }
    public void goToSettings()
    {
        Intent intent = new Intent(this,Settings.class);
        startActivity(intent);
    }
    public void DisplayAboutUs()//display app information
    {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(RemindersList.this);
        mydialog.setTitle("APP information");
        final TextView appname = new TextView(RemindersList.this);
        appname.setText("App name: Reminders");
        final TextView studentname = new TextView(RemindersList.this);
        studentname.setText("Student name: Najeeb ibrahim");
        final TextView date = new TextView(RemindersList.this);
        date.setText("Submition date: 12/6/2022");
        final TextView os = new TextView(RemindersList.this);
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        os.setText("Android SDK: " + sdkVersion + " (" + release +")");
        LinearLayout lila1= new LinearLayout(this);
        lila1.setOrientation(LinearLayout.VERTICAL);
        lila1.addView(appname);
        lila1.addView(studentname);
        lila1.addView(date);
        lila1.addView(os);
        mydialog.setView(lila1);


        mydialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        mydialog.show();
    }

    public void goToCreateActivity(View v)
    {
        Intent intent = new Intent(this,CreateNewReminder.class);
        SharedPreferences prefs = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        int i = prefs.getInt("AlarmID",-1);
        intent.putExtra("NumberOfReminders",i);
        startActivity(intent);
    }


    public void importEvents(View view)//import the events from the calendar and make reminders for them
    {
        //check if the app has permission to read calendar
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
        {
            Log.d("mylog","permission not granted");
            String[] permissions = {Manifest.permission.READ_CALENDAR};
            //request permission to read calendar
            requestPermissions(permissions, 1);
        }
        else
        {
            SharedPreferences prefs = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
            int i = prefs.getInt("AlarmID",0);
            //get the events form calendar
            cursor = getContentResolver().query(CalendarContract.Events.CONTENT_URI,null,null,null,null);
            while(cursor.moveToNext())
            {
                if(cursor != null )
                {
                    //get the events data
                    int id1 = cursor.getColumnIndex(CalendarContract.Events.TITLE);
                    int id2 = cursor.getColumnIndex(CalendarContract.Events.DTSTART);
                    int id3 = cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION);
                    String title = cursor.getString(id1);
                    String location = cursor.getString(id3);
                    long time = cursor.getLong(id2);
                    Date eventDate = new Date(time);
                    boolean flag = false;
                    for(Reminder rem: data)
                    {
                        if(rem.getReminderDesc().equals(title))
                            flag = true;
                    }
                    if(System.currentTimeMillis() < time && (System.currentTimeMillis()+(long)26298*100000)> time && !flag)//get the events in the next month
                    {
                        Map<String, Object> todo = new HashMap<>();
                        todo.put("Title", title);
                        todo.put("Time", eventDate);
                        todo.put("Location", location);
                        todo.put("Repeating",false);
                        todo.put("number_of_minutes",-1);
                        todo.put("AlarmID",i);
                        todo.put("Location_enabled",false);
                        todo.put("Latitude",0);
                        todo.put("Longitude",0);
                        todo.put("Type","Notification");
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        final String[] docID = new String[1];
                        //add the reminder to firebase
                        int finalI = i;
                        db.collection("Reminders")
                                .add(todo)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent( DocumentSnapshot snapshot,
                                                                 FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.w("mylog", "Listen failed.", e);
                                                    return;
                                                }

                                                if (snapshot != null && snapshot.exists()) {
                                                    Date timeStamp = ((com.google.firebase.Timestamp) snapshot.get("Time")).toDate();
                                                    docID[0] = documentReference.getId();
                                                    SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                                    //add reminder to list and set up alarm
                                                    Reminder rem = new Reminder(title,sfd.format(eventDate),location,docID[0],false,-1, finalI,"Notification",false,0,0);
                                                    adapter.add(rem);
                                                    setUpAlarm(timeStamp,title, finalI,docID[0],"Notification",false,-1);
                                                } else {
                                                    Log.d("mylog", "Current data: null");
                                                }
                                            }
                                        });
                                        //Date timeStamp = ((com.google.firebase.Timestamp) documentReference.get("Time")).toDate();
                                        Log.d("mylog", "Todo (ID: " + documentReference.getId()+") Added to Firestore!");

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.d("mylog", "Error adding document", e);
                                    }
                                });
                        prefs.edit().putInt("AlarmID",i).apply();
                        i++;
                    }

                }
            }
            adapter.notifyDataSetChanged();
        }

    }
    private boolean isPermissionToReadGPSLocationOK()
    {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // first, check if GPS Provider (Location) is Enabled ?
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            // second, check if permission to ACCESS_FINE_LOCATION is granted ?
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                return true;
            else
            {
                Log.d("mylog","NO Permission To Access Location!");
                // Show to user requestPermissions Dialog
                ActivityCompat.requestPermissions(RemindersList.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 111);
                return false;
            }
        }
        else
        {
            Log.d("mylog","GPS is NOT Enabled!");
            return false;
        }
    }
    public void setUpAlarm(Date date,String title,int id,String docID,String type,boolean repeating,int numberOfMinutes)
    {
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String strDate = sfd.format(date);
        String alarmDate = strDate;
        //split the date into year,month,day,hour and minutes
        String[] items1 = strDate.split("-");
        String dd = items1[0];
        String month = items1[1];
        String tmpyear = items1[2];
        String [] splt = tmpyear.split(" ");
        String year = splt[0];
        String[] temp = strDate.split(" ");
        String[] itemTime = temp[1].split(":");
        String hour = itemTime[0];
        String min = itemTime[1];
        //set the calendar
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Integer.parseInt(year),Integer.parseInt(month)-1,Integer.parseInt(dd),Integer.parseInt(hour),Integer.parseInt(min),0);
        AlarmManager manager = (AlarmManager)  getSystemService(Context.ALARM_SERVICE);
        if(type.equals("Alarm"))
        {
            Intent intent = new Intent(this, ReminderAlarmReceiver.class);
            intent.putExtra("TITLE", title);
            intent.putExtra("Date", alarmDate);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,id,intent,0);
            if(repeating)
                manager.setRepeating(manager.RTC_WAKEUP,cal.getTimeInMillis(),numberOfMinutes*60*1000,pendingIntent);
            else
                manager.setExactAndAllowWhileIdle(manager.RTC_WAKEUP,cal.getTimeInMillis(),pendingIntent);
        }
        else
        {
            Intent intent = new Intent(this, ReminderNotificationReceiver.class);
            intent.putExtra("TITLE", title);
            intent.putExtra("NUM", id);
            intent.putExtra("DocID",docID);
            if(repeating)
            {
                intent.putExtra("Type","Repeating");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this,id,intent,0);
                manager.setRepeating(manager.RTC_WAKEUP,cal.getTimeInMillis(),numberOfMinutes*60*1000,pendingIntent);
            }
            else
            {
                intent.putExtra("Type","One time");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this,id,intent,0);
                manager.setExactAndAllowWhileIdle(manager.RTC_WAKEUP,cal.getTimeInMillis(),pendingIntent);
            }
        }
    }
    public boolean isAlarmOn(int AlarmID,String type)
    {
        if(type.equals("Alarm"))
        {
            Intent alarmIntent = new Intent(this, ReminderAlarmReceiver.class);
            PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this, AlarmID, alarmIntent, PendingIntent.FLAG_NO_CREATE);

            boolean alarmStatus = (alarmPendingIntent != null);
            Log.d("mylog",">>> is Alarm ON? " + alarmStatus);
            return alarmStatus;
        }
        else
        {
            Intent alarmIntent = new Intent(this, ReminderNotificationReceiver.class);
            PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this, AlarmID, alarmIntent, PendingIntent.FLAG_NO_CREATE);
            boolean alarmStatus = (alarmPendingIntent != null);
            Log.d("mylog",">>> is Alarm ON? " + alarmStatus);
            return alarmStatus;
        }
    }
}