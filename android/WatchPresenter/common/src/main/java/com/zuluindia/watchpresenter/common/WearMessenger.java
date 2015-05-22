/*
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zuluindia.watchpresenter.common;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;


/**
 * Created by pablogil on 4/19/15.
 */
public class WearMessenger {

    private GoogleApiClient gApiClient;
    private Context context;

    public WearMessenger(Context context){
        this.context = context;
    }

    public void sendToAllThread(final String path){
        (new Thread(new Runnable() {
            @Override
            public void run() {
                sendToAll(path);
            }
        },"WearMessengerSender")).start();
    }

    public void sendToAll(final String path){
        createApiClient();
        final Collection<String> nodes = getNodes();
        for(String currentNode : nodes){
            Log.d(Constants.LOG_TAG, "Sending " + path + " message to node: " + currentNode);
            Wearable.MessageApi.sendMessage(
                    gApiClient, currentNode, path, new byte[0]).setResultCallback(
                    new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.e(Constants.LOG_TAG, "Failed to send message with status code: "
                                        + sendMessageResult.getStatus().getStatusCode());
                            }
                        }
                    }
            );
        }
    }


    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(gApiClient).await();
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        Log.d(Constants.LOG_TAG, "Got " + results.size() + " nodes");
        return results;
    }

    private void createApiClient(){
        if(gApiClient == null){
            gApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Wearable.API)
                    .build();
            gApiClient.connect();
        }
    }
}
