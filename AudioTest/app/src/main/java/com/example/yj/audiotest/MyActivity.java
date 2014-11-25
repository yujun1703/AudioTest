package com.example.yj.audiotest;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
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

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tview.setText("start recording");
                isrecording=true;
                ThreadFromRunnable r=new ThreadFromRunnable();
                Thread mythread=new Thread(r);
                mythread.start();
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                tview.setText("quit recording");
                isrecording=false;
                Log.d("aa", "stop");
            }
        });
    }


    private void initLocalSocket()
    {
        receiver = new LocalSocket();
        try
        {
            lss = new LocalServerSocket("amr");
            receiver.connect(new LocalSocketAddress("amr"));
            receiver.setReceiveBufferSize(buffersize);
            receiver.setSendBufferSize(buffersize);
           // Log.e("", "filefd:" + sender.getFileDescriptor());
            sender = lss.accept();
            Log.e("", "sender filefd:" + sender.getFileDescriptor());
            sender.setReceiveBufferSize(buffersize);
            sender.setSendBufferSize(buffersize);
        } catch (IOException e1)
        {
            e1.printStackTrace();
            Log.e("", "localSocket error:" + e1.getMessage());
        }
    }

    private boolean initializeAudio()
    {
        try
        {
            File file=new File(Environment.getExternalStorageDirectory(),"raw.3gp");

            Log.d("aa", "filepath="+Environment.getExternalStorageDirectory());
            try{
                file.createNewFile();
            }
            catch (IOException e)
            {
                Log.d("aa", "IOException",e);
            }

            OutputStream os =new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            DataOutputStream dos=new DataOutputStream(bos);




            Log.i(TAG,"##initializeVideo....");

            if(mMediaRecorder == null)
                mMediaRecorder = new MediaRecorder();
            else
                mMediaRecorder.reset();

            // 〇state: Initial=>Initialized
            // set audio source as Microphone, video source as camera
            // specified before settings Recording-parameters or encoders，called only before setOutputFormat
            //mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            // 〇state: Initialized=>DataSourceConfigured
            // 设置錄製視頻输出格式
            //     THREE_GPP:    3gp格式，H263视频ARM音频编码
            //    MPEG-4:        MPEG4 media file format
            //    RAW_AMR:    只支持音频且音频编码要求为AMR_NB
            //    AMR_NB:
            //    ARM_MB:
            //    Default:
            // 3gp or mp4
            //Android支持的音频编解码仅为AMR_NB；支持的视频编解码仅为H263，H264只支持解码；支持对JPEG编解码；输出格式仅支持.3gp和.mp4
            String lVideoFileFullPath;
            lVideoFileFullPath = ".3gp"; //.mp4
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            //mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            Log.i(TAG, "Video：Current container format: "+"3GP\n");

            // 设置視頻/音频文件的编码：AAC/AMR_NB/AMR_MB/Default
            //    video: H.263, MP4-SP, or H.264
            //mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
            //mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
            //mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
            Log.i(TAG, "Video：Current encoding format: "+"H264\n");

            // audio: AMR-NB
            //mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
            //mMediaRecorder.setVideoSize(176, 144);
            //mMediaRecorder.setVideoSize(320, 240);
            //mMediaRecorder.setVideoSize(720, 480);
            //Log.i(TAG, "Video：Current Video Size: "+"320*240\n");

            // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
            //mMediaRecorder.setVideoFrameRate(FRAME_RATE);

            // 预览
            //mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
/*
            // 设置输出文件方式： 直接本地存储   or LocalSocket远程输出
            if(bIfNativeORRemote)    //Native
            {
                lVideoFileFullPath = strRecVideoFilePath + String.valueOf(System.currentTimeMillis()) + lVideoFileFullPath;
                mRecVideoFile = new File(lVideoFileFullPath);
                // mMediaRecorder.setOutputFile(mRecVideoFile.getAbsolutePath());
                mMediaRecorder.setOutputFile(mRecVideoFile.getPath());    //called after set**Source before prepare
                Log.i(TAG, "start write into file~");
            }
            else    //Remote
            {
                mMediaRecorder.setOutputFile(sender.getFileDescriptor()); //设置以流方式输出
                Log.i(TAG, "start send into sender~");
            }
            */
            mMediaRecorder.setOutputFile(sender.getFileDescriptor()); //设置以流方式输出
            Log.i(TAG, "start send into sender~");

            //
            mMediaRecorder.setMaxDuration(0);//called after setOutputFile before prepare,if zero or negation,disables the limit
           // mMediaRecorder.setMaxFileSize(0);//called after setOutputFile before prepare,if zero or negation,disables the limit
            try
            {
                //mMediaRecorder.setOnInfoListener(this);
               // mMediaRecorder.setOnErrorListener(this);
                // 〇state: DataSourceConfigured => prepared
                mMediaRecorder.prepare();
                // 〇state: prepared => recording
                mMediaRecorder.start();
               // bIfRecInProcess = true;

                InputStream stream = receiver.getInputStream();

                byte[] buffer=new byte[buffersize];
                int bufferReadResult;
                //isrecording=true;
                while((bufferReadResult=stream.read(buffer,0,buffersize))>0&&isrecording) {
                    Log.i(TAG, "bufferReadResult:" + bufferReadResult);
                    for (int i = 0; i < bufferReadResult; i++) {
                        dos.writeByte(buffer[i]);
                        Log.i(TAG, "buffer:" + buffer[i]);
                    }
                    dos.flush();
                }
                dos.close();




                Log.i(TAG, "initializeVideo Start!");
            } catch (Exception e)
            {
                //releaseMediaRecorder();
                finish();
                e.printStackTrace();
            }
            return true;
        } catch (Exception e)
        {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
    }


    public class ThreadFromRunnable implements Runnable{
        MediaRecorder mMediaRecorder=new MediaRecorder();
        public void run()
        {

            initLocalSocket();
            initializeAudio();
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
