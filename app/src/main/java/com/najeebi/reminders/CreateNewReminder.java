package com.najeebi.reminders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateNewReminder extends AppCompatActivity {
    private EditText date_time_in;//date and time input
    private EditText Title;//title input
    private EditText location;//name of the location input
    private Button btnCreate;
    private boolean repeating = false,locationEnable = false;
    private  int numberOfMinutes = -1;
    private RadioButton alarm;
    private RadioButton notification;
    private String DocID;
    private NotificationManager notificationManager;
    private int i;
    private GoogleMap gMap;
    private Date date;
    private Marker CurrLocation;
    private Marker reminderLocation;
    private double latitude = 0,longitude = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_reminder);

        if(isPermissionToReadGPSLocationOK())
        {
            Log.d("mylog","OK - GPS Enabled & Location Permission Granted!");
            showDeviceLocation();
        }
        //map to be displayed
        SupportMapFragment mapFragment =
                (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapID);
        mapFragment.getView().setVisibility(View.INVISIBLE);

        mapFragment.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(GoogleMap googleMap)
            {
                gMap = googleMap;
                gMap.getUiSettings().setZoomControlsEnabled(true);
                gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {//when the user clicks on map, put a marker and save the location

                    @Override
                    public void onMapClick(LatLng point) {
                        if(reminderLocation != null)
                            reminderLocation.remove();
                        latitude = point.latitude;
                        longitude = point.longitude;
                        reminderLocation =googleMap.addMarker(new MarkerOptions().position(point).title(Title.getText().toString()));
                    }
                });

            }
        });

        i =  getIntent().getIntExtra("NumberOfReminders",0);
        date_time_in = findViewById(R.id.edtTxt_Date_Time);
        date_time_in.setInputType(InputType.TYPE_NULL);

        //show date time dialog
        date_time_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(date_time_in);
            }
        });

        btnCreate = findViewById(R.id.btnCreateReminder);
        Title = findViewById(R.id.edtTxt_Title);
        location = findViewById(R.id.edtTxt_Location);
        btnCreate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(validateFields())//if all the fields are filled, add the reminder
                    addReminderToFirebase();
            }
        });

        CreateNotificationChannel();
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

    private boolean validateFields() {//check all the fields if they correct input
        if(Title.getText().toString().equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please enter a valid title", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(date_time_in.getText().toString().equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please enter a valid date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!alarm.isChecked() && !notification.isChecked())
        {
            Toast.makeText(this, "Please select alarm type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(locationEnable)
        {
            if(location.getText().toString().equalsIgnoreCase("") || reminderLocation == null)
            {
                Toast.makeText(this, "Please enter a valid location", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(repeating)
        {
            if(numberOfMinutes<0)
            {
                Toast.makeText(this, "Please enter a valid number of minutes", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public void CreateNotificationChannel()
    {
        notificationManager =   (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // 2. Create Notification-Channel. ONLY for Android 8.0 (OREO API level 26) and higher.
        NotificationChannel notificationChannel = new NotificationChannel(
                "Notification_reminder", // Constant for Channel ID
                "HIGH_CHANNEL", // Constant for Channel NAME
                NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription("Channel for Notification reminders");
        notificationManager.createNotificationChannel(notificationChannel);
    }

    public void onClickRepeat(View view) {//mak the reminder a repeating reminder
        if(repeating)//if the user changes his mind, make the repeating flag false
        {
            repeating = false;
            numberOfMinutes = -1;
            RadioButton rd = (RadioButton) findViewById(R.id.rdio_btn);
            rd.setChecked(false);
        }
        else
        {
            AlertDialog.Builder mydialog = new AlertDialog.Builder(CreateNewReminder.this);
            mydialog.setTitle("How many minutes between each reminder?");
            //ask the user to enter how many minutes should be between each time the alarm gos off
            final EditText minutes = new EditText(CreateNewReminder.this);
            minutes.setInputType(InputType.TYPE_CLASS_NUMBER);
            mydialog.setView(minutes);

            mydialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    repeating = true;
                    numberOfMinutes = Integer.parseInt(minutes.getText().toString());
                }
            });

            mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                    RadioButton rd = (RadioButton) findViewById(R.id.rdio_btn);
                    rd.setChecked(false);
                }
            });
            mydialog.show();
        }

    }

    private void addReminderToFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> todo = new HashMap<>();
        todo.put("Title", Title.getText().toString());
        todo.put("Time", date);
        todo.put("Location", location.getText().toString());
        todo.put("Repeating",repeating);
        todo.put("number_of_minutes",numberOfMinutes);
        todo.put("AlarmID",i);
        todo.put("Location_enabled",locationEnable);
        todo.put("Latitude",latitude);
        todo.put("Longitude",longitude);

        if(alarm.isChecked())
            todo.put("Type","Alarm");
        if(notification.isChecked())
            todo.put("Type","Notification");
        //add the reminder to firebase
        db.collection("Reminders")
                .add(todo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("mylog", "Todo (ID: " + documentReference.getId()+") Added to Firestore!");
                        DocID = documentReference.getId();
                        if(!locationEnable)//if the reminder has location enabled set up the alarm, otherwise set up the alarm when the user reaches that location
                            setUpAlarm();
                        goToList();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.d("mylog", "Error adding document", e);
                    }
                });
    }

    public void setUpAlarm() {
        //split the date into year,month,day,hour and minutes
        String[] items1 = date_time_in.getText().toString().split("-");
        String dd = items1[0];
        String month = items1[1];
        String tmpyear = items1[2];
        String [] splt = tmpyear.split(" ");
        String year = splt[0];
        String[] temp = date_time_in.getText().toString().split(" ");
        String[] itemTime = temp[1].split(":");
        String hour = itemTime[0];
        String min = itemTime[1];
        //set the calendar
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Integer.parseInt(year),Integer.parseInt(month)-1,Integer.parseInt(dd),Integer.parseInt(hour),Integer.parseInt(min),0);
        AlarmManager manager = (AlarmManager)  getSystemService(Context.ALARM_SERVICE);


        Intent intent = new Intent(this, ReminderNotificationReceiver.class);
        intent.putExtra("TITLE", Title.getText().toString());
        intent.putExtra("NUM", i);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,i,intent,0);
        if(repeating)
        {
            manager.setRepeating(manager.RTC_WAKEUP,cal.getTimeInMillis(),numberOfMinutes*60*1000,pendingIntent);
        }
        else
        {
            manager.setExactAndAllowWhileIdle(manager.RTC_WAKEUP,cal.getTimeInMillis(),pendingIntent);
        }

        i++;
        SharedPreferences prefs = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        prefs.edit().putInt("AlarmID",i).apply();
    }



    private void goToList() {
        Intent intent = new Intent(this,RemindersList.class);
        startActivity(intent);
    }

    private boolean isPermissionToReadGPSLocationOK()//check if permission to read GPS location is granted
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
                ActivityCompat.requestPermissions(CreateNewReminder.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 111);
                return false;
            }
        }
        else
        {
            Log.d("mylog","GPS is NOT Enabled!");
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    private void showDeviceLocation()
    {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                Log.d("mylog","onLocationChanged -> " + location.getLatitude() + "," + location.getLongitude());
                showUILocation(location);//when the location gets changed, update the marker on the map
            }

            @Override
            public void onProviderEnabled(String provider)
            {
                Log.d("mylog","onProviderEnabled -> " + provider);
            }

            @Override
            public void onProviderDisabled(String provider)
            {
                Log.d("mylog","onProviderDisabled -> " + provider);
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 50, locationListener);
    }

    private void showUILocation(Location location)
    {//update the map when the location changes
        if(CurrLocation!= null)
            CurrLocation.remove();
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        CurrLocation = gMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,14));
    }



    private void showDateTimeDialog(EditText date_time_in) {//show dat and time dialog picker to get date and time input from user
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm");

                        date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
                        date = calendar.getTime();
                        Log.d("mylog","Date: "+ calendar.getTime().toString());
                    }
                };

                new TimePickerDialog(CreateNewReminder.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(CreateNewReminder.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void enableLocation(View view) {//when the user wants the reminder to work at a location, make the location input and the map visible
        TextView txt = findViewById(R.id.TxtView_Location);
        txt.setVisibility(view.VISIBLE);
        EditText loc = findViewById(R.id.edtTxt_Location);
        loc.setVisibility(view.VISIBLE);
        View map = findViewById(R.id.mapID);
        map.setVisibility(view.VISIBLE);
        locationEnable =true;
    }
}