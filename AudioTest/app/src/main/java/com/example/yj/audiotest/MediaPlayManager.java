package com.example.yj.audiotest;

import android.media.MediaPlayer;
import android.provider.MediaStore;

import java.io.IOException;

/**
 * Created by zhangyun on 14/11/27.
 */
public class MediaPlayManager {
    String datasource;
    MediaPlayer mediaPlayer;
    public MediaPlayManager(String mdatasource)
    {
        mediaPlayer=new MediaPlayer();
        datasource=mdatasource;

    }
    public void mediaPlay()
    {
        try {
            mediaPlayer.setDataSource(datasource);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return;
    }
}
