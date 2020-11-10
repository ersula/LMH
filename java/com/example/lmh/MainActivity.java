package com.example.lmh;

import android.content.Intent;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.example.lmh.util.ThreeDLayout;
import com.example.lmh.util.myGesture;

import androidx.appcompat.app.AppCompatActivity;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class MainActivity extends AppCompatActivity {
    ThreeDLayout myThreeD;
    public LottieAnimationView animationView;
    public RelativeLayout quteview;
    public LinearLayout mask;
    GestureDetector mGestureDetector;
    myGesture mGestureListener;
    RelativeLayout.LayoutParams layoutParams;
    int quteheight=0;
    int startX,startY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        animationView.setImageAssetsFolder("images/");
        animationView.setAnimation("data.json");
        animationView.loop(true);
        animationView.playAnimation();
        quteview=(RelativeLayout)findViewById(R.id.quteview);
        mask=(LinearLayout)findViewById(R.id.mask);
        mask.getBackground().setAlpha(0);
        mGestureListener=new myGesture(getApplicationContext());
        mGestureDetector = new GestureDetector(getApplicationContext(), mGestureListener);
        layoutParams = (RelativeLayout.LayoutParams) quteview.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, (int)(-600*0.316-220));
        quteview.setLayoutParams(layoutParams);

        ImageButton send=(ImageButton)findViewById(R.id.send);
        send.setImageDrawable(getResources().getDrawable(R.mipmap.send));
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent=new Intent(MainActivity.this,SendMessage.class);
                startActivity(sendIntent);
            }
        });

        ImageButton receive=(ImageButton)findViewById(R.id.receive);
        receive.setImageDrawable(getResources().getDrawable(R.mipmap.receive));
        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent=new Intent(MainActivity.this,receiveMessage.class);
                startActivity(sendIntent);
            }
        });

        ImageButton close=(ImageButton)findViewById(R.id.close);
        close.setImageDrawable(getResources().getDrawable(R.mipmap.close));
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quteheight!=600)
                {
                    quteheight=600;
                    layoutParams.setMargins(0, 0, 0, (int)(-quteheight*0.316-220));
                    quteview.setLayoutParams(layoutParams);
                    mask.getBackground().setAlpha(0);
                }
            }
        });

        ImageView bar = (ImageView)findViewById(R.id.bar);
        bar.setImageResource(R.mipmap.bar1);

        ImageView up = (ImageView)findViewById(R.id.upupupupup);
        up.setImageResource(R.mipmap.up);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * 重写触摸事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //获取当前按下的坐标
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                //获取移动后的坐标
                int moveX = (int) event.getRawX();
                int moveY = (int) event.getRawY();
                //拿到手指移动距离的大小
                int move_bigX = moveX - startX;
                int move_bigY = moveY - startY;

                quteheight+=move_bigY;
                quteheight=min(600,max(quteheight,-600));
                layoutParams.setMargins(0, 0, 0, (int)(-quteheight*0.316-220));
                quteview.setLayoutParams(layoutParams);

                int alpha=(int)(-(float)quteheight*0.1+60);
                mask.getBackground().setAlpha((int)(alpha));

                startX = moveX;
                startY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;//此处一定要返回true，否则监听不生效
    }

    /**
     * 如果触摸事件下有控件点击事件，则重写下面方法
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(mGestureDetector.onTouchEvent(ev)){
            return mGestureDetector.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }


}
