package com.example.lmh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class ReadLetter extends AppCompatActivity {

    TextView read;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        String text = i.getStringExtra("context");
        setContentView(R.layout.activity_read_letter);

        read=findViewById(R.id.read);
        read.setText(text);
        Typeface typeface= Typeface.createFromAsset(getAssets(),"方正清刻本悦宋简.TTF");

        read.setTypeface(typeface);

    }
}
