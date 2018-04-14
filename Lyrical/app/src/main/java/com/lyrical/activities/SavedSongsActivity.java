package com.lyrical.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lyrical.R;
import com.lyrical.database.SavedSongsDatabase;

import java.io.File;
import java.util.ArrayList;

public class SavedSongsActivity extends Activity {

    SavedSongsDatabase sdb;
    ListView savedlv;
    String[] items;
    ArrayList<File> savedSongs;
    String[] songName= new String[1000];
    String[] songPath = new String[1000];

    static int j=0,totalSong;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savedsonglist);

        savedlv =(ListView)findViewById(R.id.savedlv);
        sdb = new SavedSongsDatabase(this);
        readSongfromDatabase();

        items= new String[totalSong];

        for (int i = 0; i< totalSong; i++){
            items[i]= songName[i];

        }


        ArrayAdapter<String> adp= new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,items);
        savedlv.setAdapter(adp);
        savedlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                startActivity(new Intent(getApplicationContext(),LyricsPreviewActivity.class).putExtra("pos",position));

            }
        });

    }

    public void readSongfromDatabase()
    {
        //reading from databse

        Cursor res = sdb.getAllData();
        if(res.getCount() == 0)
        {
            showMessage("song","nothing to be fund");
            return ;
        }

        while(res.moveToNext()){

            songName[j]=res.getString(0);
            songPath[j++]=res.getString(1);
            //tv.setText(" " +songPath[0] +"\n");
            //buffer.append(res.getString(0)+"\n");
            // buffer.append(res.getString(1)+"\n");
        }
        totalSong=j;

        //showMessage("DATA",buffer.toString());

    }

    public void showMessage(String Lyrics , String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(Lyrics);
        builder.setMessage(Message);
        builder.show();
    }
}
