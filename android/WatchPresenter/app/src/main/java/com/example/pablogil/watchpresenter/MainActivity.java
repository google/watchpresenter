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

package com.example.pablogil.watchpresenter;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.TextView;

import com.example.pablogil.myapplication.backend.messaging.model.VersionMessage;
import com.example.pablogil.watchpresenter.messaging.GcmRegistrationAsyncTask;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.example.pablogil.watchpresenter.messaging.GcmGetVersionMessageAsyncTask;
import com.example.pablogil.watchpresenter.messaging.MessagingService;


public class MainActivity extends Activity {

    private SharedPreferences settings;
    private String accountName;
    private GoogleAccountCredential credential;
    private static final int REQUEST_ACCOUNT_PICKER = 2;



    private void chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(),
                REQUEST_ACCOUNT_PICKER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.pablogil.watchpresenter.R.layout.activity_main);
        settings = getSharedPreferences("Watchpresenter", MODE_PRIVATE);
        credential = GoogleAccountCredential.usingAudience(this,
                "server:client_id:" + Constants.ANDROID_AUDIENCE);
        setSelectedAccountName(settings.getString(Constants.PREF_ACCOUNT_NAME, null));
        if (credential.getSelectedAccountName() != null) {
            Log.d(Constants.LOG_TAG, "User already logged in");
        } else {
            Log.d(Constants.LOG_TAG, "User not logged in. Requesting user...");
            chooseAccount();
        }
        try {
            int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            Log.d(Constants.LOG_TAG, "Pagage name: " + getPackageName());
            Log.d(Constants.LOG_TAG, "Version code: " + versionCode);
            Log.d(Constants.LOG_TAG, "Version name: " + versionName);
            (new GcmGetVersionMessageAsyncTask(MessagingService.get(this), this)).execute(
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionCode);

            TextView versionTextView = (TextView)findViewById(com.example.pablogil.watchpresenter.R.id.versionText);
            versionTextView.setText("Version: " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(Constants.LOG_TAG, "Cannot retrieve app version", e);
        }
    }


    public void onRegisterGcmButtonClick(View v){
        (new GcmRegistrationAsyncTask(this)).execute();
    }

    public void onSendMessageButtonClick(View v){
        Intent i = new Intent("com.example.pablogil.watchpresenter.SEND_MESSAGE");
        i.putExtra(Constants.EXTRA_MESSAGE, Constants.NEXT_SLIDE_MESSAGE);
        sendBroadcast(i);
    }

    public void launchNotification(){
        int notificationId = 001;
// Build intent for notification content
        Intent viewIntent = new Intent(this, SendMessageReceiver.class);
        viewIntent.setAction("com.example.pablogil.watchpresenter.SEND_MESSAGE");
        viewIntent.putExtra(Constants.EXTRA_MESSAGE, Constants.NEXT_SLIDE_MESSAGE);
        PendingIntent viewPendingIntent =
                PendingIntent.getBroadcast(this, 0, viewIntent, 0);


        Intent dismissedIntent = new Intent(this, NotificationDismissedReceiver.class);
        viewIntent.setAction("com.example.pablogil.watchpresenter.NOTIFICATION_DISMISSED");
        PendingIntent dismissedPendingIntent =
                PendingIntent.getBroadcast(this, 0, dismissedIntent, 0);


        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                com.example.pablogil.watchpresenter.R.drawable.ic_stat_ic_action_forward_blue, null, viewPendingIntent).build();
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(com.example.pablogil.watchpresenter.R.drawable.ic_launcher)
                        .setContentTitle("WatchPresenter")
                        .setContentText("Dismiss when you're done presenting")
                        .setDeleteIntent(dismissedPendingIntent)
                        .addAction(action)
                        .setContentIntent(viewPendingIntent)
                        .extend(new NotificationCompat.WearableExtender()
                                .setContentAction(0));

// Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

// Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
        Intent objIntent = new Intent(this, PlayAudio.class);
        startService(objIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        launchNotification();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.example.pablogil.watchpresenter.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.example.pablogil.watchpresenter.R.id.action_about) {
            (new AlertDialog.Builder(this)
                    .setTitle(R.string.aboutTitle)
                    .setMessage(R.string.aboutMessage)

                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //nothing to do here
                    }
                })
                        .setIcon(android.R.drawable.ic_dialog_alert)
            ).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (data != null && data.getExtras() != null) {
                    String accountName =
                            data.getExtras().getString(
                                    AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        Log.d(Constants.LOG_TAG, "User picked. Account name: " + accountName);
                        setSelectedAccountName(accountName);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(Constants.PREF_ACCOUNT_NAME, accountName);
                        editor.commit();

                    }
                }
                break;
        }
    }

    private void setSelectedAccountName(String accountName) {
        credential.setSelectedAccountName(accountName);
        this.accountName = accountName;
    }

    public GoogleAccountCredential getCredential(){
        return this.credential;
    }


    public void showSuggestUpdateDialog(final VersionMessage message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("A message from the developer")
                .setMessage(message.getMessage());
        final String action = message.getAction();
        if(Constants.VersionMessageActions.ACTION_RECOMMEND_UPGRADE.equals(action) ||
                Constants.VersionMessageActions.ACTION_FORCE_UPGRADE.equals(action)){
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getUrl()));
                    startActivity(myIntent);
                    finish();
                }
            });
        }
        if(Constants.VersionMessageActions.ACTION_BLOCK.equals(action)){
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
        if(Constants.VersionMessageActions.ACTION_SHOW_MESSAGE.equals(action)){
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //nothing to do here
                }
            });
        }
        if(Constants.VersionMessageActions.ACTION_RECOMMEND_UPGRADE.equals(action)){
            builder.setNegativeButton(com.example.pablogil.watchpresenter.R.string.later, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //nothing to do here
                }
            });
        }
        if(Constants.VersionMessageActions.ACTION_FORCE_UPGRADE.equals(action)){
            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.show();
    }
}
