package com.example.yj.audiotest;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Environment;
import android.util.Log;

import java.net.MulticastSocket;
import java.net.DatagramPacket;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.InetAddress;
/**
 * Created by zhangyun on 14/11/26.
 */
public class FdManager {

    static final String TAG="FdManager";
    LocalSocket receiver=new LocalSocket();
    FileDescriptor sockfd;
    FileDescriptor filefd;
    String filepath;
    final int buffersize=5000;
    private static final int MULTICAST_PORT=5111;
    private static final String GROUP_IP="224.5.0.7";
    private static byte[] sendData;

    public FdManager()
    {

    }

    public FileDescriptor GetSocket()
    {
        int buffersize = 5000;

        try
        {
            LocalServerSocket lss = new LocalServerSocket("amr");
            receiver.connect(new LocalSocketAddress("amr"));
            receiver.setReceiveBufferSize(buffersize);
            receiver.setSendBufferSize(buffersize);
            LocalSocket sender = lss.accept();
            Log.e("", "sender filefd:" + sender.getFileDescriptor());
            sender.setReceiveBufferSize(buffersize);
            sender.setSendBufferSize(buffersize);
            sockfd=sender.getFileDescriptor();
            return sockfd;

        } catch (IOException e1)
        {
            e1.printStackTrace();
            Log.e("", "localSocket error:" + e1.getMessage());

        }

        return sockfd;
    }

    public void SendToBCast()
    {
        DatagramPacket packet;
        try {
            MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);
            multicastSocket.setLoopbackMode(false);
            InetAddress group = InetAddress.getByName(GROUP_IP);
            multicastSocket.joinGroup(group);
            packet=new DatagramPacket(sendData, sendData.length,group,MULTICAST_PORT);

            InputStream stream = receiver.getInputStream();
            byte[] buffer = new byte[buffersize];
            int bufferReadResult;

            while ((bufferReadResult = stream.read(buffer, 0, buffersize)) > 0) {
                Log.i(TAG, "bufferReadResult:" + bufferReadResult);
                packet.setData(buffer,0,bufferReadResult);
                multicastSocket.send(packet);
                //for (int i = 0; i < bufferReadResult; i++) {
                  //  dos.writeByte(buffer[i]);
                //}
               // dos.flush();
            }
            Log.i(TAG, " stopped,read buff size:"+bufferReadResult);
           // dos.close();

        } catch (IOException e) {
            Log.e(TAG, "IOException:" + e);
        }
    }

    public FileDescriptor GetFileFd()
    {
        File file=new File(Environment.getExternalStorageDirectory(),"1121.mp4");

        Log.d("aa", "filepath="+Environment.getExternalStorageDirectory());
        try{
            file.createNewFile();
            FileOutputStream os = new FileOutputStream(file);

           // BufferedOutputStream bos = new BufferedOutputStream(os);
           // DataOutputStream dos = new DataOutputStream(bos);
            filefd=os.getFD();
            filepath=file.getPath();

            Log.i(TAG, "##initializeVideo....");
        }
        catch (IOException e)
        {
            Log.d("aa", "IOException",e);
        }
        return filefd;
    }


    public void SocketRead()
    {


        try {
            InputStream stream = receiver.getInputStream();
            byte[] buffer = new byte[buffersize];
            int bufferReadResult;

            while ((bufferReadResult = stream.read(buffer, 0, buffersize)) > 0) {
                Log.i(TAG, "bufferReadResult:" + bufferReadResult);
                for (int i = 0; i < bufferReadResult; i++) {
                    //dos.writeByte(buffer[i]);
                }
                //dos.flush();
            }
            Log.i(TAG, " stopped,read buff size:" + bufferReadResult);
            //dos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public String getFilepath()
    {
        Log.i(TAG, " get filepath:" + filepath);
        return filepath;
    }
}
