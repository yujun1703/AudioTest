package com.example.yj.audiotest;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.net.LocalSocket;
import android.net.LocalServerSocket;
import android.net.LocalSocketAddress;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.*;



public class MyActivity extends Activity {

    boolean isrecording;
    LocalSocket receiver;
    LocalServerSocket lss;
    LocalSocket sender;
    MediaRecorder mMediaRecorder;

    final int buffersize=5000;
    final String TAG="audio";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        final TextView tview=(TextView)findViewById(R.id.textView);
        final Button btn=(Button)findViewById(R.id.id_btn_record);
        final Button btn_stop=(Button)findViewById(R.id.btnStop);
        final Button btn_play=(Button)findViewById(R.id.btnplay);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tview.setText("start recording");

                if(false==isrecording) {
                    try {
                       // initializeAudio();
                        isrecording=true;
                    } catch (Exception e) {
                        //releaseMediaRecorder();
                        finish();
                        e.printStackTrace();
                    }
                    ReadThread readrun=new ReadThread();
                    Thread readthread=new Thread(readrun);
                    readthread.start();
                }
                else
                    Log.d(TAG, "recoding...");
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(true==isrecording) {
                    tview.setText("quit recording");
                    mMediaRecorder.stop();
                    isrecording = false;
                    Log.d(TAG, "stop");
                }
                else
                    Log.d(TAG, "has been stopped");
            }
        });

/*
        btn_play.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
               // MediaPlayer.create()
                MediaPlayer mediaPlayer=new MediaPlayer();
                mediaPlayer.setDataSource();
                mediaPlayer.start();

            }
        });
        */
    }

    public class ThreadFromRunnable implements Runnable{

        public void run()
        {
            initLocalSocket();
            initializeAudio();
        }
    }

    public class ReadThread implements Runnable{
        public void run()
        {

            File file=new File(Environment.getExternalStorageDirectory(),"raw.mp4");

            Log.d("aa", "filepath="+Environment.getExternalStorageDirectory());
            try{
                file.createNewFile();
            }
            catch (IOException e)
            {
                Log.d("aa", "IOException",e);
            }

            try {
                OutputStream os = new FileOutputStream(file);

                BufferedOutputStream bos = new BufferedOutputStream(os);

                DataOutputStream dos = new DataOutputStream(bos);
                Log.i(TAG, "##initializeVideo....");


                try {
                    InputStream stream = receiver.getInputStream();
                    byte[] buffer = new byte[buffersize];
                    int bufferReadResult;

                    while ((bufferReadResult = stream.read(buffer, 0, buffersize)) > 0) {
                        Log.i(TAG, "bufferReadResult:" + bufferReadResult);
                        for (int i = 0; i < bufferReadResult; i++) {
                            dos.writeByte(buffer[i]);
                        }
                        dos.flush();
                    }
                    Log.i(TAG, " stopped,read buff size:"+bufferReadResult);
                    dos.close();

                } catch (IOException e) {
                    Log.e(TAG, "IOException:" + e);
                }
            }

            catch (FileNotFoundException e)
            {
                Log.d("aa", "FileNotFoundException",e);
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
