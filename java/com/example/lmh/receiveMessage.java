package com.example.lmh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import com.dlong.rep.dlsimpleweathermanager.DLSimpleWeatherUtils;
import com.dlong.rep.dlsimpleweathermanager.OnGetWeatherListener;
import com.dlong.rep.dlsimpleweathermanager.model.DLCoordinateCode;
import com.dlong.rep.dlsimpleweathermanager.model.DLPlaceInfo;
import com.dlong.rep.dlsimpleweathermanager.model.DLWeatherInfo;
import com.example.lmh.util.BToast;
import com.example.lmh.util.SQLLink;
import com.example.lmh.util.weather.HTTPRetrieval;
import com.example.lmh.util.weather.JSONParser;
import com.example.lmh.util.weather.WeatherInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class receiveMessage extends AppCompatActivity {
    LottieAnimationView search_btn;
    private LocationClient mLocationClient = null;
    String message;
    String locx, locy;
    String weather;
    int hour, minute;
    private Handler hd;
    HttpThread ht;
    String citycode;
    Calendar calendar;
    int weatherCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new receiveMessage.MyLocationListener());
        //时间
        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);



        DLSimpleWeatherUtils.init(getApplicationContext());


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_message);
        initBtn();
        initGPS();
    }
    public class HttpThread extends Thread {
        @Override
        public void run() {
            super.run();
            HTTPRetrieval hr = new HTTPRetrieval();
            // 城市代码
            String weatherString = hr.HTTPWeatherGET(citycode);

            WeatherInfo wi = new WeatherInfo();
            JSONParser jp = new JSONParser();
            // 调用自定义的 JSON 解析类解析获取的 JSON 数据
            wi = jp.WeatherParse(weatherString);

            final WeatherInfo finalWi = wi;
            // 多线程更新 UI
            hd.post(new Runnable() {
                @Override
                public void run() {
                    weather=finalWi.getDescription();
                    Log.e("weather:",weather);
                }
            });
        }
    }

    private void toast_good() {
        //TODO 匹配成功请调用这个
        BToast.showText(receiveMessage.this, "匹配成功", Toast.LENGTH_LONG, true);
    }

    private void toast_bad() {
        //TODO 匹配失败请调用这个
        BToast.showText(receiveMessage.this, "匹配失败", Toast.LENGTH_LONG, false);
    }

    private void initBtn() {

        search_btn = findViewById(R.id.listen_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_btn.playAnimation();
                //TODO  这里是按下按钮后，提取用户当前的天气，位置，时间等因素，在数据库里搜索是否有符合条件的信件

                message = SQLLink.select(locx, locy,weather,hour,minute);
                Log.e("sql",weather);
                Log.e("sql",hour+" ");
                if (message == null) toast_bad();
                else {
                    toast_good();
                    // Log.i("what",message);
                    //TODO  这里是符合条件以后，因为虚拟现实还没实现，所以直接跳转到了读信界面
                    new Thread() {
                        @Override
                        public void run() {
                            Intent sendIntent = new Intent(receiveMessage.this, takephoto.class);
                            sendIntent.putExtra("context", message);
                            startActivity(sendIntent);
                        }
                    }.start();
                }
            }
        });
    }

    private void initGPS() {
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



    class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  //TODO 这里可以获取经纬度
                                  locx = String.valueOf(location.getLatitude());
                                  locy = String.valueOf(location.getLongitude());
                                  hour = calendar.get(Calendar.HOUR_OF_DAY);
                                  minute = calendar.get(Calendar.MINUTE);

                                  Log.e("hour",hour+"");
                                  Log.e("minute",minute+"");
                                  DLSimpleWeatherUtils.checkWeather(location.getLatitude(), location.getLongitude(),
                                          DLCoordinateCode.CODE_WGS84,onGetWeatherListener);


                              }
                          }
            );
        }

    }
    /**
     * 天气获取监听器
     */
    private OnGetWeatherListener onGetWeatherListener = new OnGetWeatherListener() {
        @Override
        public void OnNetworkDisable() {
            Log.e("no internet", "没有打开网络，或没有网络权限");
        }

        @Override
        public void OnError(int step, int code) {
            // 这里会返回错误出现的步骤，和错误码
            Log.e("error:", "step = " + step + "; code = " + code);
        }

        @Override
        public void OnGetWeather(DLWeatherInfo weatherInfo) {
            // 返回获得的天气信息

            weather=weatherInfo.getStatusText();
            weatherCode=Integer.parseInt(weatherInfo.getStatusCode());
            Log.e("weather",weather);

        }

        @Override
        public void OnGetLatAndLon(double latitude, double longitude) {
            // 这里返回获得的经纬度信息
            Log.e("location: ", "latitude = " + latitude + "; longitude = " + longitude);
        }

        @Override
        public void OnGetRealAddress(DLPlaceInfo placeInfo) {
            // 这里返回获取的地址信息
            Log.e("address", placeInfo.getInfo());
        }
    };


}