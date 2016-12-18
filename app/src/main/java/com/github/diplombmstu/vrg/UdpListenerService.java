package com.github.diplombmstu.vrg;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/*
 * Linux command to send UDP:
 * #socat - UDP-DATAGRAM:192.168.1.255:11111,broadcast,sp=11111
 */
public class UdpListenerService extends Service
{
    private static String UDP_BROADCAST = "UDPBroadcast";

    //Boolean shouldListenForUDPBroadcast = false;
    private DatagramSocket socket;
    private Thread UDPBroadcastThread;
    private Boolean shouldRestartSocketListen = true;

    @Override
    public void onCreate()
    {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        shouldRestartSocketListen = true;
        startListenForUDPBroadcast();
        Log.i("UDP", "Service started");
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        stopListen();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    void startListenForUDPBroadcast()
    {
        Log.i("sync", "starting listening");

        UDPBroadcastThread = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    InetAddress broadcastIP = InetAddress.getByName("localhost"); //172.16.238.42 //192.168.1.255
                    Integer port = VrgCommons.SYNC_PORT + 1;
                    while (shouldRestartSocketListen)
                    {
                        listenAndWaitAndThrowIntent(broadcastIP, port);
                    }
                    //if (!shouldListenForUDPBroadcast) throw new ThreadDeath();
                }
                catch (Exception e)
                {
                    Log.e("UDP", "no longer listening for UDP broadcasts cause of error " + e.getMessage());
                }
            }
        });
        UDPBroadcastThread.start();
    }

    ;

    private void stopListen()
    {
        shouldRestartSocketListen = false;
        socket.close();
    }

    private void listenAndWaitAndThrowIntent(InetAddress broadcastIP, Integer port) throws Exception
    {
        byte[] recvBuf = new byte[15000];
        if (socket == null || socket.isClosed())
        {
            socket = new DatagramSocket(port, broadcastIP);
            socket.setBroadcast(true);
        }
        //socket.setSoTimeout(1000);
        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
        Log.e("UDP", "Waiting for UDP broadcast");
        socket.receive(packet);

        String senderIP = packet.getAddress().getHostAddress();
        String message = new String(packet.getData()).trim();

        Log.e("UDP", "Got UDB broadcast from " + senderIP + ", message: " + message);

        broadcastIntent(senderIP, message);
        socket.close();
    }

    private void broadcastIntent(String senderIP, String message)
    {
        Intent intent = new Intent(UdpListenerService.UDP_BROADCAST);
        intent.putExtra("sender", senderIP);
        intent.putExtra("message", message);
        Log.i("message", message);
        sendBroadcast(intent);
    }
}
