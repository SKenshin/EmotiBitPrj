package com.lab.uqac.emotibit.application.launcher.Network;

import android.os.AsyncTask;
import android.os.Build;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Connection  {

    private int mPort;
    private String mAddress;
    private DatagramSocket mSocket;
    private AsyncTask<Void, String, Void> mAsynck;

    public Connection(int port){
        mPort = port;
    }

    public Connection(int port, DatagramSocket socket){
        mPort = port;
        mSocket = socket;
    }

    public Connection(int port, String address){
        mPort = port;
        mAddress = address;
    }

    private void startConnection() throws SocketException {
        if(mSocket == null) mSocket = new DatagramSocket(mPort);
    }



    public void broadCastHelloMessage()
    {
        mAsynck = new AsyncTask<Void, String, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {

                    mSocket = new DatagramSocket(mPort);
                    mSocket.setBroadcast(true);

                    InetAddress[] address = new InetAddress[5];

                    for(int i = 0; i < 5; i++) {
                        byte[] buf = new byte[10000];
                        DatagramPacket datagramPacketReceive = new DatagramPacket(buf, buf.length);
                        mSocket.receive(datagramPacketReceive);

                        address[i] = mSocket.getInetAddress();
                    }
                    for(int i = 0; i < 5; i++) {

                    }


                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

        };

        if (Build.VERSION.SDK_INT >= 11) mAsynck.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else mAsynck.execute();
    }

    public void stopConnection(){

        mSocket.close();
    }



    public int getmPort() {
        return mPort;
    }

    public void setmPort(int mPort) {
        this.mPort = mPort;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public DatagramSocket getmSocket() {
        return mSocket;
    }

    public void setmSocket(DatagramSocket mSocket) {
        this.mSocket = mSocket;
    }


}
