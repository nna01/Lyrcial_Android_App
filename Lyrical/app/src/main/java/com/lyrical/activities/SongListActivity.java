package com.lyrical.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.lyrical.R;
import com.lyrical.songuse.GetSongFromDevice;

import java.io.File;
import java.util.ArrayList;
import com.lyrical.database.SavedSongsDatabase;

public class SongListActivity extends AppCompatActivity {

    ListView liVw;
    String[] items;
    GetSongFromDevice dv;
    ArrayList<File> fileSongs;
    SavedSongsDatabase sdb;

    int j=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songlist);
        sdb = new SavedSongsDatabase(this);

        liVw =(ListView)findViewById(R.id.listView);
        dv =new GetSongFromDevice();

        fileSongs = dv.createList(Environment.getExternalStorageDirectory());
        fileSongs = dv.listofsongs();

        items= new String[fileSongs.size()];

        for (int i = 0; i< fileSongs.size(); i++){
            items[i]= fileSongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
        }

        ArrayAdapter<String> adp= new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,items);
        liVw.setAdapter(adp);
        liVw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                startActivity(new Intent(getApplicationContext(),LyricsInsertActivity.class).putExtra("pos",position));

                //inserting in song list database
                boolean isInsert =  sdb.insertData(items[position] , fileSongs.get(position).toString());

                if( isInsert == true )
                    Toast.makeText(SongListActivity.this,"DATA INSERTED",Toast.LENGTH_LONG).show();

                else
                    Toast.makeText(SongListActivity.this,"DATA is not INSERTED",Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}

