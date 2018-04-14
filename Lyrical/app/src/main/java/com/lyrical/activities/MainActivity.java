package com.lyrical.activities;

import android.app.Activity;


import android.content.Intent;

import android.os.Bundle;

import android.view.View;

import android.widget.Button;

import com.lyrical.R;


public class MainActivity extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button savedfile = (Button) findViewById(R.id.savedfile);
        Button newfile = (Button) findViewById(R.id.newfile);

        newfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), SongListActivity.class);
                startActivityForResult(intent,0);
            }
        });


        savedfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), SavedSongsActivity.class);
                startActivityForResult(intent,0);
            }
        });


    }
}