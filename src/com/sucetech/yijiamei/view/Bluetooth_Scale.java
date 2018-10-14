package com.sucetech.yijiamei.view;
/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class Bluetooth_Scale {
    // Debugging
    private static final String TAG = "BluetoothAdService";
    private static final boolean D = true;
    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
 
    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    Context ctx;
    private int mState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    /**
     * Constructor. Prepares a new BluetoothChat session.
     * @param context  The UI Activity Context
     * @param handler  A Handler to send messages back to the UI Activity
     */
    public Bluetooth_Scale(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        ctx=context;
        mHandler = handler;
    }
    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;
    }

    /**
     * Return the current connection state. */
    public synchronized int getState() {
        return mState;
    }



    public synchronized void connect(BluetoothDevice device) {
    	if (D) Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
       // if (mState == STATE_CONNECTING) {		///����ж��Ƿ���Ҫ
            if (mConnectThread != null) 
            {
            	mConnectThread.cancel(); 
            	mConnectThread = null;
            }
      //  }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null)
        {
        	mConnectedThread.cancel(); 
        	mConnectedThread = null;
        }
        	
        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }
    

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {
        if (D) Log.d(TAG, "connected, Socket Type:" + socketType);

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        //Log.d(TAG, "2");
        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, socketType);
        mConnectedThread.start();
        // Send the name of the connected device back to the UI Activity
        setState(STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
        // Start the service over to restart listening mode
    	if (D) Log.d(TAG, "connected fail" );
    	Message message=new Message();
     	message.what=2;  
     	message.arg1=0;
     	mHandler.sendMessage(message);
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
    	if (D) Log.d(TAG, "connected lost" );
        // Start the service over to restart listening mode
    	Message message=new Message();
     	message.what=2;  
     	message.arg1=1;
     	mHandler.sendMessage(message);
    }

    private void connectionok() {
        // Send a failure message back to the Activity
    	if (D) Log.d(TAG, "connected ad" );
        // Start the service over to restart listening mode
    	Message message=new Message();
     	message.what=2;  
     	message.arg1=2;
     	mHandler.sendMessage(message);
    }



    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                
            } catch (IOException e) {
            		Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
            }
            mmSocket = tmp;
        }


		public void run() {
			if(D) Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
            setName("ConnectThread" + mSocketType);

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + mSocketType +
                            " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (Bluetooth_Scale.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
            }
        }
    }
 
    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private boolean sconnected=false;
        public ConnectedThread(BluetoothSocket socket, String socketType) {
        	if(D) Log.d(TAG, "create ConnectedThread: " + socketType);
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            sconnected=true;
            connectionok();
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[50];
            byte[] strdata = new byte[30];
            int pStrdata=0;
            int r[]=new int[4];
         	int ad;
            int bytes;

            // Keep listening to the InputStream while connected
            while (sconnected) {
                try {
                    // Read from the InputStream
                    if (!mmSocket.isConnected()){
                        Log.e("LLL","!mmSocket.isConnected()-->");
                        connectionLost();
                        return;
                    }
                    bytes = mmInStream.read(buffer);
                    for(int i = 0; i < bytes; i++)
                    {
                    	if(buffer[i]==0x2)pStrdata=0;
                    	else  if(buffer[i]==0x0A)
                    	{
                    	    if (i==0)continue;
                            if(buffer[i-1]==0x0d)pStrdata--;
                            strdata[pStrdata]=0;    //��ʾ����
                            Bundle b=new Bundle();
                            b.putByteArray("weight",strdata);
             	        	Message message=new Message();
             	        	message.what=1;
                            message.setData(b);
             	        	mHandler.sendMessage(message);
                    		//Log.e(TAG, "r:" + new String(buffer));
                    		pStrdata=0;
                    	}
                    	else 
                    	{
                    		strdata[pStrdata] = buffer[i];
                    		if(++pStrdata>=30)pStrdata=0;
                    	}
                    	
                    	
                    }
                    // Send the obtained bytes to the UI Activity
                    //mHandler.obtainMessage(1, bytes, -1, buffer)
                        //    .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                //mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1, buffer)
                     //   .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
            	sconnected=false;
            	if(mmInStream!=null)mmInStream.close();
            	if(mmOutStream!=null)mmOutStream.close();
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
