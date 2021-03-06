package com.example.yj.audiotest;

import android.media.MediaRecorder;
import android.net.LocalSocket;
import android.net.rtp.RtpStream;
import android.util.Log;

import java.io.FileDescriptor;

/**
 * Created by zhangyun on 14/11/26.
 */
public class RecorderManager {

    private MediaRecorder mMediaRecorder;
    private final String TAG="RecorderManager";

    public RecorderManager()
    {
        mMediaRecorder=new MediaRecorder();
    }

    public boolean recorder(FileDescriptor  fileDescriptor)
    {
        try
        {
            if(mMediaRecorder == null)
                mMediaRecorder = new MediaRecorder();
            else
                mMediaRecorder.reset();

            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //mMediaRecorder.setOutputFormat(OUTPUT_FORMAT_RTP_AVP);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            Log.i(TAG, "Audio：Current container format: " + "3GP\n");
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);


            Log.i(TAG, "Audio：Current encoding format: "+"aac\n");

            mMediaRecorder.setOutputFile(fileDescriptor);
            Log.i(TAG, "start send into sender~");

            mMediaRecorder.setMaxDuration(0);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            Log.i(TAG, "start recorder");
            return true;
        }
        catch (Exception e)
        {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
    }

    public  boolean stopRecorder()
    {
        mMediaRecorder.stop();
        return true;
    }


}
