package com.lyrical.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lyrical.R;
import com.lyrical.database.LyricsDatabaseHelper;
import com.lyrical.songuse.GetSongFromDevice;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class LyricsInsertActivity extends AppCompatActivity {

    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();
    int position;
    GetSongFromDevice dv;
    ArrayList<File> fileSongs;
    String path;

    ImageButton play,prev,next,playlist,pause;
    //Thread UpdateSeekBar;
    static UpdateSeekBar updatesb;
    Uri u;
    SeekBar sb;

    static MediaPlayer mPlayer;

    private TextView timeView1,timeView2;

    public String insertLyrics=new String();

    //time when lyrics is add
    public double currentTime = 0;
    public long in;
    EditText editLyrics;

    public long timeArray[]= new long[1000];
    public long diff;
    private String[] splitLine;
    Button addButton;

    public int j=1,i=0;
    public static int oneTimeOnly = 0;

    LyricsDatabaseHelper myDb;
    int songID=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertlyrics);

        play=(ImageButton)findViewById(R.id.play);
        pause=(ImageButton)findViewById(R.id.pause);
        prev=(ImageButton)findViewById(R.id.previousButton);
        next=(ImageButton)findViewById(R.id.nextButton);
        playlist=(ImageButton)findViewById(R.id.playlistButton);
        sb=(SeekBar)findViewById(R.id.seekBar);

        //adding lyrics button
        editLyrics = (EditText)findViewById(R.id.editLyrics);
        addButton = (Button)findViewById(R.id.addButton);
        addData();
        timeArray[0]=(0*1000);

        timeView1 =(TextView)findViewById(R.id.timeView1);
        timeView2=(TextView)findViewById(R.id.timeView2);

        myDb = new LyricsDatabaseHelper(this);
        songID++;

        if(mPlayer!=null){
            if(updatesb !=null){
                updatesb.endthread();
                updatesb.interrupt();
                updatesb =null;
            }
            mPlayer.stop();
            mPlayer.release();
        }

        dv =new GetSongFromDevice();
        if(dv.getstatus()!=true){
            fileSongs = dv.createList(Environment.getExternalStorageDirectory());
        }
        else{
            fileSongs = dv.listofsongs();
        }

        Intent intent=getIntent();
        position=intent.getIntExtra("pos",0);
        path= fileSongs.get(position).toString();
        u=Uri.parse(path);


        mPlayer=MediaPlayer.create(getApplicationContext(),u);
        playSong();
        mPlayer.setLooping(true);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPlayer.seekTo(sb.getProgress());
            }
        });


        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Pausing sound",Toast.LENGTH_SHORT).show();
                mPlayer.pause();
                pause.setEnabled(false);
                play.setEnabled(true);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(updatesb !=null){
                    updatesb.endthread();
                    updatesb.interrupt();
                    updatesb =null;
                }
                mPlayer.stop();
                mPlayer.release();
                position=(position+1)% fileSongs.size();
                path= fileSongs.get(position).toString();

                u=Uri.parse(path);
                mPlayer=MediaPlayer.create(getApplicationContext(),u);
                playSong();
                mPlayer.setLooping(true);

            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(updatesb !=null){
                    updatesb.endthread();
                    updatesb.interrupt();
                    updatesb =null;
                }
                mPlayer.stop();
                mPlayer.release();
                position=(position-1<0)? fileSongs.size()-1:position-1;
                path= fileSongs.get(position).toString();

                u=Uri.parse(path);
                mPlayer=MediaPlayer.create(getApplicationContext(),u);
                playSong();
                mPlayer.setLooping(true);

            }
        });
        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SongListActivity.class));
            }
        });
    }

    void playSong()
    {
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Playing sound",Toast.LENGTH_SHORT).show();
                mPlayer.start();

                finalTime = mPlayer.getDuration();
                startTime = mPlayer.getCurrentPosition();

                //seekbar
                sb.setMax(mPlayer.getDuration());
                sb.setProgress(mPlayer.getCurrentPosition());
                updatesb =new UpdateSeekBar(true);
                updatesb.start();

                //updating the time
                if (oneTimeOnly == 0) {
                    sb.setMax((int) finalTime);
                    oneTimeOnly = 1;
                }
                timeView2.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
                );

                timeView1.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
                );

                myHandler.postDelayed(UpdateSongTime,100);
                pause.setEnabled(true);
                play.setEnabled(false);
            }
        });
    }

    public void addData()
    {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentTime = mPlayer.getCurrentPosition();
                insertLyrics=editLyrics.getText().toString();
                splitLine= insertLyrics.split("\n");

                timeArray[j]=1000*(TimeUnit.MILLISECONDS.toSeconds((long) currentTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) currentTime)));
                diff=timeArray[j]-timeArray[j-1];

                boolean isInsert =  myDb.insertData(splitLine[i++] , diff, songID);
                j++;

                if( isInsert == true )
                    Toast.makeText(LyricsInsertActivity.this,"DATA INSERTED",Toast.LENGTH_LONG).show();

                else
                    Toast.makeText(LyricsInsertActivity.this,"DATA is not INSERTED",Toast.LENGTH_LONG).show();


            }
        });
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mPlayer.getCurrentPosition();
            timeView1.setText(String.format("%d:%d",

                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            myHandler.postDelayed(this, 100);
        }
    };

    class UpdateSeekBar extends Thread{

        private boolean running;

        UpdateSeekBar(boolean status){
            running=status;
        }

        public void endthread(){
            running=false;
        }

        @Override
        public void run() {
            try {
                while (running==true) {
                    int dur = mPlayer.getDuration();
                    int cur = mPlayer.getCurrentPosition();
                    while (cur < dur) {
                        sleep(500);
                        cur = mPlayer.getCurrentPosition();
                        sb.setProgress(cur);
                    }
                }
            }
            catch (InterruptedException e){
                e.printStackTrace();
                running=false;
            }
        }
    }

}