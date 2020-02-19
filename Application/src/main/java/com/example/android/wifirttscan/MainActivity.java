/*
 * Copyright (C) 2018 Google Inc. All Rights Reserved.
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
package com.example.android.wifirttscan;

import static com.example.android.wifirttscan.AccessPointRangingResultsActivity.SCAN_RESULT_EXTRA;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.MacAddress;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.rtt.RangingRequest;
import android.net.wifi.rtt.RangingResult;
import android.net.wifi.rtt.RangingResultCallback;
import android.net.wifi.rtt.WifiRttManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.android.wifirttscan.MyAdapter.ScanResultClickListener;


import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays list of Access Points enabled with WifiRTT (to check distance). Requests location
 * permissions if they are not approved via secondary splash screen explaining why they are needed.
 */
public class MainActivity extends AppCompatActivity implements ScanResultClickListener
{

    private static final String TAG = "MainActivity";
    private boolean mLocationPermissionApproved = false;
    List<ScanResult> mAccessPointsSupporting80211mc;
    private WifiManager mWifiManager;
    private WifiScanReceiver mWifiScanReceiver;
    private TextView mOutputTextView;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;


    private WifiRttManager mWifiRttManager;
    private RttRangingResultCallback mRttRangingResultCallback;
    private int mStatisticRangeSDHistoryEndIndex;
    private ArrayList<Integer> mStatisticRangeSDHistory;
    final Handler mRangeRequestDelayHandler = new Handler();

    private int j;
    private int sumDist = 0;
    private int diameterMM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mOutputTextView = findViewById(R.id.access_point_summary_text_view);

        mRecyclerView = findViewById(R.id.recycler_view);

        // Improve performance if you know that changes in content do not change the layout size
        // of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAccessPointsSupporting80211mc = new ArrayList<>();

        mAdapter = new MyAdapter(mAccessPointsSupporting80211mc, this);
        mRecyclerView.setAdapter(mAdapter);

        j=0;
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mWifiScanReceiver = new WifiScanReceiver();
        logToUi(getString(R.string.retrieving_access_points));
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();


        mLocationPermissionApproved =
                ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        registerReceiver(
                mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        onClickFindDistancesToAccessPoints(mRecyclerView);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
        unregisterReceiver(mWifiScanReceiver);
    }

    private void logToUi(final String message) {
        if (!message.isEmpty()) {
            Log.d(TAG, message);
            mOutputTextView.setText(message);
        }
    }

    @Override
    public void onScanResultItemClick(ScanResult scanResult) {
//        Log.d(TAG, "onScanResultItemClick(): ssid: " + scanResult.SSID);
//
//        Intent intent = new Intent(this, AccessPointRangingResultsActivity.class);
//        intent.putExtra(SCAN_RESULT_EXTRA, scanResult);
//        startActivity(intent);
    }

    @Override
    public void onScanResultItemClick(List<ScanResult> scanResult) {
            Log.d(TAG, "onScanResultItemClick(): ssid: " + scanResult.get(0).SSID);
            Intent intent = new Intent(this, AccessPointRangingResultsActivity.class);
            intent.putExtra(SCAN_RESULT_EXTRA, scanResult.get(0));
            startActivity(intent);
    }

    public void onClickFindDistancesToAccessPoints(View view) {
        if (mLocationPermissionApproved) {
            //logToUi(getString(R.string.retrieving_access_points));
            mWifiManager.startScan();

        } else {
            // On 23+ (M+) devices, fine location permission not granted. Request permission.
            Intent startIntent = new Intent(this, LocationPermissionRequestActivity.class);
            startActivity(startIntent);
        }
    }

    public void onClickStartRanging(View view) {
        mWifiRttManager = (WifiRttManager) getSystemService(Context.WIFI_RTT_RANGING_SERVICE);
        mRttRangingResultCallback = new MainActivity.RttRangingResultCallback();

        File sdCardFile = new File(Environment.getExternalStorageDirectory() + "/WifiRTTmeasured.txt");
        sdCardFile.delete();

        // Used to store range (distance) and rangeSd (standard deviation of the measured distance)
        // history to calculate averages.

        mStatisticRangeSDHistory = new ArrayList<>();

        if(mAccessPointsSupporting80211mc.size() != 0) {
            startRangingRequest();
        }
    }

    public void onClickStopRanging(View view){
        mAccessPointsSupporting80211mc.clear();
    }

    public class RttRangingResultCallback extends RangingResultCallback {

        private void queueNextRangingRequest() {
            onClickFindDistancesToAccessPoints(mRecyclerView);
            mRangeRequestDelayHandler.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            if(mAccessPointsSupporting80211mc.size() != 0) {
                                startRangingRequest();
                            }
                        }
                    },
                    100);
        }

        @Override
        public void onRangingFailure(int code) {
            Log.d(TAG, "onRangingFailure() code: " + code);
            queueNextRangingRequest();
        }

        @Override
        public void onRangingResults(List<RangingResult> list) {
            Log.d(TAG, "onRangingResults(): " + list);
            fileWriter(list);
            queueNextRangingRequest();
        }
    }


    private void startRangingRequest() {
        // Permission for fine location should already be granted via MainActivity (you can't get
        // to this class unless you already have permission. If they get to this class, then disable
        // fine location permission, we kick them back to main activity.
        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            finish();
        }
        Log.d("TAG","TEST" + mAccessPointsSupporting80211mc.toString()); //<-- check the log to make sure the path is correct.
        RangingRequest rangingRequest =
                new RangingRequest.Builder().addAccessPoints(mAccessPointsSupporting80211mc).build();
                //new RangingRequest.Builder().addAccessPoint(testovaci).build();
        RangingRequest test;
        mWifiRttManager.startRanging(
                rangingRequest, getApplication().getMainExecutor(), mRttRangingResultCallback);
    }

    private class WifiScanReceiver extends BroadcastReceiver {

        private List<ScanResult> find80211mcSupportedAccessPoints(
                List<ScanResult> originalList) {
            List<ScanResult> newList = new ArrayList<>();

            for (ScanResult scanResult : originalList) {

                if (scanResult.is80211mcResponder()) {
                    newList.add(scanResult);
                }

                if (newList.size() >= RangingRequest.getMaxPeers()) {
                    break;
                }
            }
            return newList;
        }

        // This is checked via mLocationPermissionApproved boolean
        @SuppressLint("MissingPermission")
        public void onReceive(Context context, Intent intent) {

            List<ScanResult> scanResults = mWifiManager.getScanResults();

            if (scanResults != null) {

                if (mLocationPermissionApproved) {
                    mAccessPointsSupporting80211mc = find80211mcSupportedAccessPoints(scanResults);

                    mAdapter.swapData(mAccessPointsSupporting80211mc);

                    logToUi(
                            scanResults.size()
                                    + " APs discovered, "
                                    + mAccessPointsSupporting80211mc.size()
                                    + " RTT capable.");

                } else {
                    // TODO (jewalker): Add Snackbar regarding permissions
                    Log.d(TAG, "Permissions not allowed.");
                }
            }
        }
    }

    public void fileWriter(List<RangingResult> list){

        FileWriter fWriter;
        File sdCardFile = new File(Environment.getExternalStorageDirectory() + "/WifiRTTmeasured.txt");
        Log.d("TAG", sdCardFile.getPath()); //<-- check the log to make sure the path is correct.
        try{
            fWriter = new FileWriter(sdCardFile, true);
            //if(sdCardFile.length() < 100000) {
            if(j <= 100){
                for(int i = 0; i < list.size(); i++) {
                    fWriter.write( j++ +" "+ list.get(i) + System.lineSeparator());
                    sumDist = sumDist + list.get(i).getDistanceMm();
                }
                diameterMM = sumDist/j;
                fWriter.write("Průměrná vzdálenost je: " + diameterMM + " mm" + System.lineSeparator());
                fWriter.flush();
                fWriter.close();
            }else{
                //sdCardFile.delete();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
