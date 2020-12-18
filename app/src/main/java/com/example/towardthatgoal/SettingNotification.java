package com.example.towardthatgoal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import java.security.PublicKey;
import java.util.Collections;

public class SettingNotification extends PreferenceActivity implements Preference.OnPreferenceChangeListener{

    private NotificationCompat.Builder builder; //알림-콘텐츠와 채널을 설정.
    private NotificationManager notificationManager;

    private  static final String TAG = "SettingNotification";

    private static final String KEY_EDITTEXT="key_edit_text_preference_goal";
    private static final String KEY_SWITCH="key_switch_preference_notification";
    private static final String KEY_LIST="key_list_preference_alarm";

    private PreferenceScreen screen;
    private EditTextPreference mUseerGoal; //목표작성
    private SwitchPreference mUserswitchOn; //PUSH알람 스위치
    private boolean SwitchCondition; //PUSH알람 설정 유무

    private  ListPreference mUseralarm; //alrm 시간 설정 dialog
    
    AlarmRepeat alarmRepeat; // 시간 알림 객체

    //main 함수
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preference);

        screen=getPreferenceScreen();

        //인자로 전달되는 KEY값을 가지는 preference 항목을 인스턴스로 가져옴
        mUseerGoal=(EditTextPreference)screen.findPreference(KEY_EDITTEXT);
        mUserswitchOn=(SwitchPreference)screen.findPreference(KEY_SWITCH);
        mUseralarm=(ListPreference)screen.findPreference(KEY_LIST);

        //변화 이벤트가 일어났을 시 동작
        mUseerGoal.setOnPreferenceChangeListener(this);
        mUserswitchOn.setOnPreferenceChangeListener(this);
        mUseralarm.setOnPreferenceChangeListener(this);

        createNotification();

    }

    //변화가 일어났을 때
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        Log.i(TAG,"preference : "+preference+"newVale : "+o);

        if(preference==mUseerGoal){ //edittext 변화 이벤트
            String value=(String)o;
            Log.i(TAG,"mUserGoal onPreferenceChage");
            mUseerGoal.setSummary(value);
            //알람
            builder.setContentText(value);
            if(SwitchCondition==true)
                notificationManager.notify(1,builder.build());

        }else if(preference==mUserswitchOn) { //스위치 변화 이벤트
            SwitchCondition = (boolean) o;
            if (SwitchCondition==true) {
                Toast.makeText(this, "설정됨.", Toast.LENGTH_SHORT).show();
                notificationManager.notify(1, builder.build());
            } else {
                Toast.makeText(this, "해제됨.", Toast.LENGTH_SHORT).show();
                NotificationManagerCompat.from(this).cancel(1);
            }
        }
        else if(preference==mUseralarm){
            if(o instanceof String){ //설정시 꺼짐. 알아보기
                alarmRepeat=new AlarmRepeat(Integer.parseInt((String) o) );
                Toast.makeText(this,"알림 해제", Toast.LENGTH_SHORT).show();
            }else{
                alarmRepeat=new AlarmRepeat((Integer) o);
                Toast.makeText(this, o+"시 설정", Toast.LENGTH_SHORT).show();
            }



        }


        return true;
    }

    //목표 summary 업데이트 함수
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateSummary(){
        mUseerGoal.setSummary(mUseerGoal.getText());
        Log.d(TAG,"mUserGoal : "+mUseerGoal);
    }
    //알람(push) 업데이트 함수.
    private void updateNotification(){
        builder.setContentText(mUseerGoal.getText());
        if(SwitchCondition==true) //switch가 켜져 있어있는 상태
            notificationManager.notify(1,builder.build());
        else
            NotificationManagerCompat.from(this).cancel(1);
        Log.d(TAG,"notificationManger : "+notificationManager);
    }
    //알람 생성 함수
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotification(){
        builder = new NotificationCompat.Builder(this, "default").setOngoing(true);

        builder.setSmallIcon(R.mipmap.ic_launcher);//설정한 작은 아이콘
        builder.setContentTitle("설정한 목표");
        builder.setContentText(mUseerGoal.getText());
        
        // 색깔setting
        //builder.setColor(Color.RED);
        //사용자가 탭을 클릭하면 자동 제거
        //builder.setAutoCancel(true);

        notificationManager = (NotificationManager) this.getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        //채널 생성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_0_1) {
            notificationManager.createNotificationChannels(Collections.singletonList(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT)));
        }
        notificationManager.notify(1, builder.build());
    }
    //창이 리셋되었을 때 호출되는 함수.
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
       updateSummary();
       updateNotification();
       Log.d(TAG,"onResume");
    }



}
