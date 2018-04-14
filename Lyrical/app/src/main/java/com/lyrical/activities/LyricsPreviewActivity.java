package com.lyrical.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lyrical.R;
import com.lyrical.database.LyricsDatabaseHelper;
import com.lyrical.database.SavedSongsDatabase;
import java.util.concurrent.TimeUnit;


public class LyricsPreviewActivity extends Activity {


    SavedSongsDatabase sdb;
    private Button forward,pause,play,backward;
    private ImageView iv;
    private MediaPlayer mediaPlayer;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekbar;
    private TextView tx1,tx2,tx3,texttime;

    //time when lyrics is add
    public TextView showLyrics;

    public String[] lines = new String[1000];
    public int i=0,k=0,j=0;
    public long Aratime[]= new long [1000];

    public static int oneTimeOnly = 0;
    boolean isPaused = true;
    LyricsDatabaseHelper mydb;
    String[] songName= new String[1000];
    String[] songPath = new String[1000];
    String[] items;
    static int totalSong=0;
    Uri u;
    int position;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previewlyrics);
        mydb = new LyricsDatabaseHelper(this);

        sdb = new SavedSongsDatabase(this);
        readSongfromDatabase();

        forward = (Button) findViewById(R.id.forward);
        pause = (Button) findViewById(R.id.pause);
        play=(Button)findViewById(R.id.play);
        backward=(Button)findViewById(R.id.backward);

        tx1=(TextView)findViewById(R.id.timeView1);
        tx2=(TextView)findViewById(R.id.timeView2);
        tx3=(TextView)findViewById(R.id.textView4);

        texttime =(TextView)findViewById(R.id.texttime);
        tx3.setText("Song.mp3");
        Aratime[0]=100;
        k=0;
        //showing lyrics text
        showLyrics = (TextView)findViewById(R.id.showLyrics);


        //song from database
        items= new String[totalSong];

        for (int i = 0; i< totalSong; i++){
            items[i]= songPath[i];
        }

        Intent intent=getIntent();
        position=intent.getIntExtra("pos",0);
        path= items[position];
        texttime.setText(path);
        u= Uri.parse(path);

        mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
        seekbar=(SeekBar)findViewById(R.id.seekBar);
        seekbar.setClickable(false);
        pause.setEnabled(false);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Playing sound",Toast.LENGTH_SHORT).show();

                mediaPlayer.start();
                readDatafromDatabase();

                finalTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();

                if (oneTimeOnly == 0) {
                    seekbar.setMax((int) finalTime);
                    oneTimeOnly = 1;
                }
                tx2.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
                );

                tx1.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
                );

                //thread for showing lyrics
                isPaused=true;
                //splitLine = lines.split("\n");

                //myHandler.postDelayed(UpdateWords,7000);
                myHandler.postDelayed(UpdateWords,Aratime[k++]);


                seekbar.setProgress((int)startTime);
                myHandler.postDelayed(UpdateSongTime,100);
                pause.setEnabled(true);
                play.setEnabled(false);

            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Pausing sound",Toast.LENGTH_SHORT).show();
                mediaPlayer.pause();
                pause.setEnabled(false);
                play.setEnabled(true);
                //pausingLyrics();
                isPaused=false;
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int)startTime;

                if((temp+forwardTime)<=finalTime){
                    startTime = startTime + forwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast.makeText(getApplicationContext(),"You have Jumped forward 5 seconds",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Cannot jump forward 5 seconds",Toast.LENGTH_SHORT).show();
                }
            }
        });

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int)startTime;

                if((temp-backwardTime)>0){
                    startTime = startTime - backwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast.makeText(getApplicationContext(),"You have Jumped backward 5 seconds",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Cannot jump backward 5 seconds",Toast.LENGTH_SHORT).show();
                }
            }
        });

        readDatafromDatabase();
    }
    public void readDatafromDatabase()
    {
        //reading from databse
        Cursor res = mydb.getAllData();
        if(res.getCount() == 0)
        {
            showMessage("song","nothing to be fund");
            return ;
        }
        StringBuffer buffer = new StringBuffer();

        while(res.moveToNext()){

            lines[j]=res.getString(0);
            Aratime[j++]=res.getLong(1);

        }

    }

    public void showMessage(String Lyrics , String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(Lyrics);
        builder.setMessage(Message);
        builder.show();
    }


    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            tx1.setText(String.format("%d:%d",

                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };

    //lyrics runnable

    public Runnable UpdateWords = new Runnable() {
        @Override
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            showLyrics.setText(String.format("%s",lines[i++]));
            showLyrics.startAnimation(AnimationUtils.loadAnimation(LyricsPreviewActivity.this,android.R.anim.fade_in));

            int size = Aratime.length;
            texttime.setText(String.format("timeArray[%d] = %d ",k,Aratime[k]));

            if(k==size+1)
                k=1;

            if(i==showLyrics.length())
                i=0;

            if(isPaused==true)
                myHandler.postDelayed(this,Aratime[k]);

            k++;
        }
    };

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

            songName[totalSong]=res.getString(0);
            songPath[totalSong++]=res.getString(1);

        }
    }

}
