package com.github.diplombmstu.vrg;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ConnectionManager
{
    private static final String TAG = "hello";

    public void start(final SyncEventHandler syncHandler) throws IOException, JSONException
    {
        AsyncTask serverTask = new AsyncTask()
        {
            @Override
            protected Object doInBackground(Object[] params)
            {
                try
                {
                    //Keep a socket open to listen to all the UDP trafic that is destined for this port
                    DatagramSocket socket = new DatagramSocket(VrgCommons.SYNC_PORT, InetAddress.getByName("0.0.0.0"));
                    socket.setBroadcast(true);

                    while (true)
                    {
                        Log.i(TAG, "Ready to receive broadcast packets!");

                        //Receive a packet
                        byte[] recvBuf = new byte[15000];
                        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                        socket.receive(packet);

                        //Packet received
                        Log.i(TAG, "Packet received from: " + packet.getAddress().getHostAddress());
                        String data = new String(packet.getData()).trim();
                        Log.i(TAG, "Packet received; data: " + data);

                        syncHandler.handle(packet.getAddress());
                        break;
                    }
                }
                catch (IOException ex)
                {
                    Log.e(TAG, "Oops " + ex.getMessage());
                }

                return null;
            }
        };

        serverTask.execute();
    }
}