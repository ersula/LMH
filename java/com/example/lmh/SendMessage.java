package com.example.lmh;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.lmh.util.BToast;
import com.example.lmh.util.PickerView;

import java.util.ArrayList;
import java.util.List;
import com.example.lmh.util.SQLLink;


public class SendMessage extends AppCompatActivity {
    private LinearLayout llContentView;
    ImageButton send;
    private LocationClient mLocationClient = null;
    int num_tag=-1;
    EditText message_edit;
    TextView positionText;
    PickerView minute_pv;
    PickerView hour_pv;
    PickerView weather_pv;
    private ImageView weather_img;

    String message,locx,locy,weather;
    int dateh,datem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_send_message);
        initTimePicker();
        initGPS();
        initCtrl();

    }
    private void toast_good()
    {
        //TODO 数据上传到服务器要给用户一定的反馈，上传成功请调用这个
        BToast.showText(SendMessage.this, "上传成功",Toast.LENGTH_LONG,true);
    }
    private void toast_bad()
    {
        //TODO 数据上传到服务器要给用户一定的反馈，上传失败请调用这个
        BToast.showText(SendMessage.this, "上传失败",Toast.LENGTH_LONG,false);
    }


    private void initGPS()
    {
        positionText=(TextView)findViewById(R.id.gps_display_txt) ;
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            requestLocation();
        }
    }

    private void initTimePicker()
    {
        hour_pv = (PickerView) findViewById(R.id.hour_pv);
        minute_pv = (PickerView) findViewById(R.id.minute_pv);
        weather_pv=(PickerView)findViewById(R.id.weather_pv) ;
        weather_img=(ImageView)findViewById((R.id.weather_img));
        weather_img.setImageResource(R.mipmap.yin);
        List<String> miniutes = new ArrayList<String>();
        List<String> hours = new ArrayList<String>();
        List<String> weathers = new ArrayList<String>();
        for (int i=59; i >=0; i--)
        {
            miniutes.add(i < 10 ? "0" + i : "" + i);
        }
        for  (int i=23; i >=0; i--)
        {
            hours.add(i < 10 ? "0" + i : "" + i);
        }
        weathers.add("多云");
        weathers.add("晴");
        weathers.add("雾");
        weathers.add("雪");
        weathers.add("阴");
        weathers.add("阵雨");
        weathers.add("雨");
        weathers.add("雾霾");
        weather_pv.setData(weathers);
        weather_pv.setOnSelectListener(new PickerView.onSelectListener()
        {

            @Override
            public void onSelect(String text)
            {
                weather = text;
                //TODO  这里获取用户指定的天气
                if(text.equals("多云"))
                {
                    weather_img.setImageResource(R.mipmap.duoyun);
                }else if(text.equals("晴"))
                {
                    weather_img.setImageResource(R.mipmap.qing);
                }else if(text.equals("雾"))
                {
                    weather_img.setImageResource(R.mipmap.wu);
                }else if(text.equals("雪"))
                {
                    weather_img.setImageResource(R.mipmap.xue);
                }else if(text.equals("阴"))
                {
                    weather_img.setImageResource(R.mipmap.yin);
                }else if(text.equals("阵雨"))
                {
                    weather_img.setImageResource(R.mipmap.zhenyu);
                }else if(text.equals("小雨"))
                {
                    weather_img.setImageResource(R.mipmap.yu);
                }else if(text.equals("雾霾"))
                {
                    weather_img.setImageResource(R.mipmap.wumai);
                }
            }
        });


        //TODO 下面两个监听函数可以获取用户指定的时间
        hour_pv.setData(hours);
        hour_pv.setOnSelectListener(new PickerView.onSelectListener()
        {
            @Override
            public void onSelect(String text)
            {
                dateh = Integer.parseInt(text);
            }
        });
        minute_pv.setData(miniutes);
        minute_pv.setOnSelectListener(new PickerView.onSelectListener()
        {
            @Override
            public void onSelect(String text)
            {
                datem = Integer.parseInt(text);
            }
        });
    }


    private void initCtrl()
    {
        send=(ImageButton) this.findViewById(R.id.send_btn);
        //TODO  这个messageedit可以获取用户输入的信息
        message_edit = (EditText) this.findViewById(R.id.message_write);

        //TODO  这里监听发动按钮按下，触发事件（那个小飞机）
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = message_edit.getText().toString();
                StringBuilder sb = new StringBuilder(message);
                int i = sb.indexOf("\"");
                while(i!=-1){
                    sb.insert(i,"\\");
                    i = sb.indexOf("\"");
                }
                message = sb.toString();
                if(SQLLink.insert(message,locx,locy,dateh,datem,weather))toast_good();
                else toast_bad();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setAddrType("all");
        mLocationClient.setLocOption(option);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        //mapView.onDestroy();
    }

    class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  StringBuilder currentPosition = new StringBuilder();
                                  currentPosition.append("纬度:").append(location.getLatitude()).append(" ");
                                  currentPosition.append("经度:").append(location.getLongitude()).append("\n");
                                  currentPosition.append(location.getCountry()).append(" ").append(location.getProvince()).append(" ");
                                  currentPosition.append(location.getCity()).append(" ");
                                  currentPosition.append(location.getDistrict()).append(" ").append(location.getStreet()).append("\n");

                                  //TODO 这里可以获取经纬度
                                  locx = String.valueOf(location.getLatitude());
                                  locy = String.valueOf(location.getLongitude());

                                  if(!currentPosition.toString().contains("null"))
                                  {
                                      positionText.setText(currentPosition);
                                  }
                              }
                          }
            );
//            positionText.setText(currentPosition);
//            Log.d("MainActivity", currentPosition.toString());
//            if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation) {
//                navigateTo(location);
//            }
        }

    }

}

