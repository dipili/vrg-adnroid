/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.diplombmstu.vrg.temp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.diplombmstu.vrg.*;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.neovisionaries.ws.client.*;
import org.json.JSONException;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Fragment for handling the Welcome tab.
 */
public class WelcomeFragment extends Fragment
{

    private VrPanoramaView panoWigetView;
    private ImageLoaderTask backgroundImageLoaderTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.welcome_fragment, container, false);
        panoWigetView = (VrPanoramaView) v.findViewById(R.id.pano_view);

        ConnectionManager connectionManager = new ConnectionManager();
        try
        {
            connectionManager.start(new SyncEventHandler()
            {
                @Override
                public void handle(InetAddress address)
                {
                    try
                    {
                        WebSocketFactory factory = new WebSocketFactory();
                        WebSocket socket = factory.createSocket(String.format("ws://%s:%d/communication/",
                                                                              address.getHostAddress(),
                                                                              VrgCommons.COMMUNICATION_SERVER_PORT));
                        socket.addListener(new WebSocketAdapter()
                        {
                            @Override
                            public void onTextMessage(WebSocket websocket, String message) throws Exception
                            {
                                System.out.println(message);
                            }
                        });

                        socket.connect();
                    }
                    catch (OpeningHandshakeException e)
                    {
                        Utils.printOpeningHandshakeException(e);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (IOException | JSONException e)
        {
            e.printStackTrace();
        }

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        loadPanoImage();
    }

    @Override
    public void onResume()
    {
        panoWigetView.resumeRendering();
        super.onResume();
    }

    @Override
    public void onPause()
    {
        panoWigetView.pauseRendering();
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        panoWigetView.shutdown();
        super.onDestroy();
    }


    private synchronized void loadPanoImage()
    {
        ImageLoaderTask task = backgroundImageLoaderTask;
        if (task != null && !task.isCancelled())
        {
            task.cancel(true);
        }

        VrPanoramaView.Options viewOptions = new VrPanoramaView.Options();
        viewOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;

        String panoImageName = "converted.jpg";

        task = new ImageLoaderTask(panoWigetView, viewOptions, panoImageName);
        task.execute(getActivity().getAssets());
        backgroundImageLoaderTask = task;
    }
}
