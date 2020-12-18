package com.example.towardthatgoal;

import android.app.AlarmManager;
import android.app.PendingIntent;

import java.util.Calendar;

public class AlarmRepeat{
    Calendar calendar;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    public AlarmRepeat(int doHour){
        if(doHour==0){ //알람 설정 안함.
            alarmMgr.cancel(alarmIntent);
        }else{
            setAlarm(doHour);
        }

    }
    //알람 시간 설정
    private boolean setAlarm(int doHour){
        calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,doHour);
        calendar.set(Calendar.MINUTE,0);

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,alarmIntent);

        return true;
    }
}
