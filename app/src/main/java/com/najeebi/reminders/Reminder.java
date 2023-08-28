package com.najeebi.reminders;

public class Reminder {
    private String ReminderDesc;//title of the reminder
    private String date;//date of the reminder
    private  String location;//name of the location the reminder works on
    private String ID;//firebase ID
    private boolean Repeat,locationEnabled;//if the reminder is a repeating one, if it sould work in a specific location
    private int numberOfMinutes;//the number of minutes the reminder should repeat
    private int AlarmID;//Alarm id
    private String type;//type of the alarm (NNotification or Alarm)
    private double latitude,longtitude;//the location of the reminder


    public Reminder(String ReminderDesc, String date, String location,String id,boolean Repeat,int numberOfMinutes,int AlarmID,String type,boolean enable,double lat,double lng) {
        this.ReminderDesc = ReminderDesc;
        this.date = date;
        this.location = location;
        this.ID = id;
        this.Repeat = Repeat;
        this.numberOfMinutes = numberOfMinutes;
        this.AlarmID = AlarmID;
        this.type = type;
        this.locationEnabled = enable;
        this.latitude = lat;
        this.longtitude = lng;

    }

    public String getType() {
        return type;
    }

    public int getNumberOfMinutes() {
        return numberOfMinutes;
    }

    public boolean isRepeat() {
        return Repeat;
    }
    public String getReminderDesc() {
        return ReminderDesc;
    }

    public int getAlarmID() {
        return AlarmID;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getID() {
        return ID;
    }
}
