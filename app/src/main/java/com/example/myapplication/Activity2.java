package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationSettingsRequest;

import java.util.ArrayList;

import static android.Manifest.permission.CALL_PHONE;

public class Activity2 extends AppCompatActivity {

    Button b1,b2,b3;
    private FusedLocationProviderClient client;
    DatabaseHandler myDB;
    private final int REQUEST_CHECK_CODE=8989;
    private LocationSettingsRequest.Builder builder;
    String x="", y="";
    private static final int REQUEST_LOCATION = 1;

    LocationManager locationManager;
    Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        b1=findViewById(R.id.button);
        b2=findViewById(R.id.button2);
        b3=findViewById(R.id.button3);
        myDB=new DatabaseHandler(this);
        final MediaPlayer mp= MediaPlayer.create(getApplicationContext(), R.raw.alarm);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            onGPS();
        }
        else{
            startTrack();
        }

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Register.class);
                startActivity(i);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Activity3.class);
                startActivity(i);
            }
        });


        b2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Toast.makeText(getApplicationContext(),"Panic button started",Toast.LENGTH_SHORT).show();
                loadData();
                mp.start();
                return false;
            }
        });
    }
    private void loadData(){
        ArrayList<String> thelist= new ArrayList<>();
        Cursor data=myDB.getListContents();
        if(data.getCount()==0){
            Toast.makeText(this,"no content to show",Toast.LENGTH_SHORT).show();
        }
        else{
            String msg="Help Me My Location is : http://maps.google.com/maps?saddr=" +x + "," +y;
            String number = "";
            while(data.moveToNext()){
                thelist.add(data.getString(1));
                number=number+data.getString(1)+(data.isLast()?"":";");

                //sendSms(number,msg,true);
                //call(number);
            }

            sendSms(number,msg);

        }

    }

    private void sendSms(String number, String msg) {
//        msg="Help";
        Intent smsIntent=new Intent(Intent.ACTION_VIEW);
        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address",number);
        smsIntent.putExtra("sms_body",msg);
        //startActivity(smsIntent);

        try {
            startActivity(smsIntent);
            Log.i("Finished sending SMS...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getBaseContext(),
                    "SMS failed, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    private void call(String number) {
        Intent i= new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:"+number));
        if(ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE )== PackageManager.PERMISSION_GRANTED){
            startActivity(i);
        }
        else{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{CALL_PHONE},1);
            }

        }
    }

    private void onGPS() {

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();

    }

    private void startTrack() {
        if ((ActivityCompat.checkSelfPermission(Activity2.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                && ActivityCompat.checkSelfPermission(Activity2.this,
                Manifest.permission.ACCESS_COARSE_LOCATION )!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
//        else{
            Location locationGPS=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(locationGPS!=null)
            {
                double lat=locationGPS.getLatitude();
                double lan=locationGPS.getLongitude();
                x=String.valueOf(lat);
                y=String.valueOf(lan);
            }
            else{
                Toast.makeText(this,"Unable to find location",Toast.LENGTH_SHORT).show();
            }
//        }
    }
}
