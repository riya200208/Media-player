package com.example.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Boolean isRunning =false;
    ArrayList<song> songContainer = new ArrayList<song>();
    myAdapter listAdapter;
    Button stop,pause,play;
    ListView lv;
    MediaPlayer mp;
    SeekBar seekBar;
    Runnable runnable;
    Boolean b1pressed = true,b2pressed=true,b3pressed = true;
    int seekValue,stopped=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stop = (Button) findViewById(R.id.stop);
        pause = (Button) findViewById(R.id.pause);
        play = (Button)findViewById(R.id.play);
        lv = (ListView) findViewById(R.id.lv);
        checkPermission();
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekValue = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
              //  mp.seekTo(seekValue);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekValue);
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                pause.setBackgroundResource(R.color.DarkBlue);
                play.setBackgroundResource(R.color.DarkBlue);
                stop.setBackgroundResource(R.color.DarkBlue);
                if(mp != null) {
                    mp.stop();
                    seekBar.setProgress(0);
                }
                mp = new MediaPlayer();

                try {

                    mp.setDataSource(songContainer.get(i).path);
                    mp.prepare();


                } catch (Exception e) {
                    e.printStackTrace();
                }

                createMp();
                seekBar.setMax(mp.getDuration());


            }
        });
    }
        public ArrayList<song> getAllSongs()
        {
            Uri allsong = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.IS_MUSIC+" != 0";
            Cursor cursor = getContentResolver().query(allsong,null,selection,null,null);
            if(cursor!=null)
            {
                if(cursor.moveToFirst())
                {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        String fullPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        //String albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.A))
                        songContainer.add(new song(fullPath,name,"Riya","Unknown"));

                    }while (cursor.moveToNext());
                }
                cursor.close();
            }
      //           songContainer.add(new song("http://server6.mp3.quaran.net/thubti/001.mp3","Fatah ","k-drama","sarooyi"));
            return songContainer;

        }
        public void createMp()
        {
          //  if(mp == null)
        //        mp = MediaPlayer.create(this,R.raw.sample2);

            isRunning=true;
            mythread t = new mythread();
            t.start();
            mp.start();
        }

        class mythread extends Thread{
        @Override
        public void run()
        {
            while (isRunning)
            {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            seekBar.setProgress(mp.getCurrentPosition());

                        }
                    });

                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }


    }

public  class myAdapter extends BaseAdapter{
        ArrayList<song> fullSongPath;

    public myAdapter(ArrayList<song> fullSongPath) {
        this.fullSongPath = fullSongPath;
    }

    @Override
    public int getCount() {
        return fullSongPath.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = getLayoutInflater();
        View myView = inflater.inflate(R.layout.list,null);
        final song s =fullSongPath.get(i);
        TextView name = (TextView) myView.findViewById(R.id.name);
        TextView desc = (TextView) myView.findViewById(R.id.desc);
        name.setText("Song: "+s.song_name);
        desc.setText("Artist: "+s.artist_name);
        return myView;
    }
}
    public void stop(View view) {
        if(b1pressed)
        {
            view.setBackgroundResource(R.color.DarkGreen);
            play.setBackgroundResource(R.color.DarkBlue);
            pause.setBackgroundResource(R.color.DarkBlue);
        }

        if(mp == null)
            Toast.makeText(MainActivity.this, "Please choose one song!", Toast.LENGTH_SHORT).show();
        else {

           // mp.seekTo(0);

            
            mp.pause();
            mp.seekTo(0);
            seekBar.setProgress(0);
            isRunning = false;
            stopped =1;
        }
    }

    public void play(View view) {
        if(b2pressed)
        {
            view.setBackgroundResource(R.color.DarkGreen);
            pause.setBackgroundResource(R.color.DarkBlue);
            stop.setBackgroundResource(R.color.DarkBlue);
        }

        if(mp == null)
            Toast.makeText(MainActivity.this, "Please choose one song!", Toast.LENGTH_SHORT).show();
        else {
            createMp();

        }
//        else
//        {
//           mp.reset();
//          //  mp.start();
//            stopped =0;
//         //   Toast.makeText(this, "came", Toast.LENGTH_SHORT).show();
//        }
//        seekBar.setMax(mp.getDuration());
//        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mediaPlayer) {
//                  if(mp !=null)
//                {
//                    mp.release();
//                    mp = null;
//                }
//
//            }
//        });

    }

    public void pause(View view) {
        if(b3pressed)
        {
            view.setBackgroundResource(R.color.DarkGreen);
            play.setBackgroundResource(R.color.DarkBlue);
            stop.setBackgroundResource(R.color.DarkBlue);
        }


        if(mp !=null)
            mp.pause();
        else
            Toast.makeText(MainActivity.this, "Please choose one song!", Toast.LENGTH_SHORT).show();
        isRunning=false;
    }
    @Override
    public void onStop()
    {
        super.onStop();
        if(mp !=null)
        {
            mp.release();
            mp = null;
        }
    }
    final private int REQUEST_CODE_ASK_PERMISSION=123;
    void checkPermission()
    {
        if(Build.VERSION.SDK_INT>=23)
        {
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]
                                {
                                        Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSION
                );
                return;
            }

        }
        LoadSong();

    }
    @Override
    public void onRequestPermissionsResult(int RequestCode, String[] projection, int[] grantResult)
    {
        switch (RequestCode)
        {
            case REQUEST_CODE_ASK_PERMISSION:
                if(grantResult[0]==PackageManager.PERMISSION_GRANTED)
                {
                    LoadSong();
                }
                else{
                    Toast.makeText(this, "denied", Toast.LENGTH_SHORT).show();
                }
            default:
                super.onRequestPermissionsResult(RequestCode,projection, grantResult);
        }
    }
    void LoadSong()
    {
        listAdapter = new myAdapter(getAllSongs());
        lv.setAdapter(listAdapter);
    }
}

