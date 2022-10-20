package com.example.sensorcapture;


import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements SensorEventListener, View.OnClickListener {

    private TextView mTextView;
    private TextView SensorNumAll;
    private SensorManager sensorManager;
    private TableLayout tableLayout;
    private CheckBox checkBox[]= new CheckBox[30];
    static public List<String> LS;
    static public List<Sensor> allSensors;
    private int SENSOR_RATE_NORMAL=20000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button start = findViewById(R.id.start);
        start.setOnClickListener(this);

        Button stop = findViewById(R.id.stop);
        stop.setOnClickListener(this);

        SensorNumAll=findViewById(R.id.SensorTypeAll);
        tableLayout=findViewById(R.id.tableLayout);

        SensorInfo();

        LS = new ArrayList<String>();
    }

    /*
    Get all sensors in this device and add the checkbox to provide the function that user can choose which sensor to collect.
     */
    public void SensorInfo(){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        Sensor sensor1 = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorNumAll.setText("Total have "+allSensors.size()+" sensors, choose sensors you need");
        for(int i=0;i<allSensors.size();i++){
            checkBox[i]=new CheckBox(this);
            checkBox[i].setId(i);
            checkBox[i].setText(allSensors.get(i).getName());
            checkBox[i].setOnClickListener(this);
            tableLayout.addView(checkBox[i]);
        }
        mTextView=new TextView(this);
        mTextView.setText(" ");
        tableLayout.addView(mTextView);
        mTextView=new TextView(this);
        tableLayout.addView(mTextView);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);

    }

    /*
    save all dataset into .txt file
     */
    public void writeLS(List<String> LS) {
        FileOutputStream fos;
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String filename = formatter.format(date);
        try{
            fos=openFileOutput(filename+".txt",MODE_APPEND);
            for (int i = 0; i < LS.size(); i++) {
                String text=LS.get(i)+"\n";
                fos.write(text.getBytes());
            }
            fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.start:
                // choose the sensor that user choose
                for(int i=0;i<allSensors.size();i++){
                    if(checkBox[i].isChecked()==true){
                        sensorManager.unregisterListener(this, allSensors.get(i));
                        sensorManager.registerListener(this, allSensors.get(i), SENSOR_RATE_NORMAL);
                    }
                }
                break;
            case R.id.stop:
                sensorManager.unregisterListener(this);
                if(LS.size()!=0) {
                    writeLS(LS);
                    Toast.makeText(this, "capture successfully! Total " + LS.size() + " records", Toast.LENGTH_SHORT).show();
                    LS.clear();
                }else {
                    Toast.makeText(this, "don't capture any data or the sensor doesn't work", Toast.LENGTH_SHORT).show();
                }
        }
    }

    /*
    collect sensor data
     */

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values=sensorEvent.values;
        int sensorType=sensorEvent.sensor.getType();
        StringBuilder sb=null;
        //if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            String s;
            long timeCurrentTimeMillis = System.currentTimeMillis();
            s = timeCurrentTimeMillis + " "+sensorEvent.sensor.getName();
            for(int i=0;i<values.length;i++) {
                s+=" "+Float.toString(values[i]);
            }
            LS.add(s);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        return;
    }
}