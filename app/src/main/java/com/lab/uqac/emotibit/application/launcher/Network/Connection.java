package com.lab.uqac.emotibit.application.launcher.Network;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.lab.uqac.emotibit.application.launcher.Datas.MessageGenerator;
import com.lab.uqac.emotibit.application.launcher.Datas.TypesDatas;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import GUI.EmotiBitButton;

public class Connection {

    private int mPort;
    private InetAddress mAddress;
    private DatagramSocket mSocket = null;
    private List<InetAddress> mInetAddresses;
    private int mMaxConnectedDevice;
    private EmotiBitButton mEmotiBitButton;
    static int mIndex = 0;
    String mHelloMessage;
    HandlerThread mHandlerThread;

    Handler mHandler;


    public Connection(int port, int maxConnectedDevice, EmotiBitButton emotiBitButton) {
        mPort = port;
        mInetAddresses = new ArrayList<InetAddress>();
        mMaxConnectedDevice = maxConnectedDevice;
        mEmotiBitButton = emotiBitButton;
        mHandlerThread = new HandlerThread("threadname");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    public Connection(int port, DatagramSocket socket, InetAddress address,
                      int maxConnectedDevice, EmotiBitButton emotiBitButton) {
        mPort = port;
        mSocket = socket;
        mAddress = address;
        mInetAddresses = new ArrayList<InetAddress>();
        mMaxConnectedDevice = maxConnectedDevice;
        mEmotiBitButton = emotiBitButton;
        mHandlerThread = new HandlerThread("threadname");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    public Connection(int port, InetAddress address, int maxConnectedDevice,
                      EmotiBitButton emotiBitButton) {
        mPort = port;
        mAddress = address;
        mInetAddresses = new ArrayList<InetAddress>();
        mMaxConnectedDevice = maxConnectedDevice;
        mEmotiBitButton = emotiBitButton;
        mHandlerThread = new HandlerThread("threadname");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }


    public void stopConnection() {
        if(mSocket != null) {
            mSocket.close();
            mSocket.disconnect();
        }
    }


    public int getmPort() {
        return mPort;
    }

    public void setmPort(int mPort) {
        this.mPort = mPort;
    }

    public InetAddress getmAddress() {
        return mAddress;
    }

    public void setmAddress(InetAddress mAddress) {
        this.mAddress = mAddress;
    }

    public DatagramSocket getmSocket() {
        return mSocket;
    }

    public void setmSocket(DatagramSocket mSocket) {
        this.mSocket = mSocket;
    }

    public List<InetAddress> getmInetAddresses() {
        return mInetAddresses;

    }

    public void start(){

        mHandler.postDelayed(mSendTask, 5000);


        if (mReceiveTask != null && mReceiveTask.getStatus() == AsyncTask.Status.FINISHED ||
                mReceiveTask.getStatus() == AsyncTask.Status.PENDING)
            mReceiveTask.execute();

    }

    public void end(){
        mHandler.removeCallbacks(mSendTask);

        if (mReceiveTask != null && mReceiveTask.getStatus() != AsyncTask.Status.FINISHED )
            mReceiveTask.cancel(true);

        stopConnection();
    }


    Runnable mSendTask = new Runnable() {
        @Override
        public void run() {

            try {



                if (mSocket == null || !mSocket.isBound()) {
                    mSocket = new DatagramSocket(mPort);
                    mSocket.setBroadcast(true);

                    mHelloMessage = MessageGenerator.generateMessageWithLocalTime(TypesDatas.HE,
                            1, 1, 100);
                }

                if (mIndex < mMaxConnectedDevice) {

                    byte[] buffer = mHelloMessage.getBytes();
                    DatagramPacket datagramPacketSend = new DatagramPacket(buffer, buffer.length,
                            mAddress, mPort);
                    mSocket.send(datagramPacketSend);
                    mHandler.postDelayed(mSendTask, 5000);
                }
                else
                    mHandler.removeCallbacks(mSendTask);


            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }};


    AsyncTask<Void, InetAddress, Void> mReceiveTask = new AsyncTask<Void, InetAddress, Void>() {
        @Override
        protected Void doInBackground(Void... voids) {

            try {

                while (mSocket == null || !mSocket.isBound());

                while (mIndex < mMaxConnectedDevice) {

                    byte[] bufferDatas = new byte[10000];
                    DatagramPacket datagramPacketReceive = new DatagramPacket(bufferDatas, bufferDatas.length);

                    mSocket.receive(datagramPacketReceive);

                    String datas = new String(bufferDatas, 0, datagramPacketReceive.getLength());

                    InetAddress inetAddress = datagramPacketReceive.getAddress();

                    if (!mHelloMessage.equals(datas) && !mInetAddresses.contains(inetAddress)) {
                        mInetAddresses.add(inetAddress);
                        publishProgress(inetAddress);
                        mIndex++;
                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(InetAddress... inetAddresses) {
            super.onProgressUpdate(inetAddresses[0]);

            mEmotiBitButton.add(inetAddresses[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            stopConnection();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            stopConnection();
        }

    };

}
