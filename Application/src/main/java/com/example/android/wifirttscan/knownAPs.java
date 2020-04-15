package com.example.android.wifirttscan;

import android.annotation.SuppressLint;
import android.net.wifi.ScanResult;
import android.net.wifi.rtt.RangingRequest;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class knownAPs {

    public List<ScanResult> scanResults = new ArrayList<>();

    public knownAPs() {
        final Constructor c1;
        final Constructor c2;
        final Constructor c3;
        try {
            c1 = ScanResult.class.getDeclaredConstructor();
            c1.setAccessible(true);
            ScanResult r1 = (ScanResult) c1.newInstance();
            r1.SSID = "RTT test";
            r1.BSSID = "70:3a:cb:7f:dc:1a";
            r1.capabilities = "[WPA2-PSK-CCMP][RSN-PSK-CCMP][ESS]";
            r1.level = -50;
            r1.frequency = 5180;
            r1.centerFreq0 = 5210;
            r1.centerFreq1 = 0;
            @SuppressLint("SoonBlockedPrivateApi") Method method1 = r1.getClass().getDeclaredMethod("setFlag", Long.TYPE);
            method1.setAccessible(true);
            Object x1 = method1.invoke(r1, 2); // FLAG_80211mc_RESPONDER

            c2 = ScanResult.class.getDeclaredConstructor();
            c2.setAccessible(true);
            ScanResult r2 = (ScanResult) c1.newInstance();
            r2.SSID = "WifiRTT1";
            r2.BSSID = "70:3a:cb:7f:23:fb";
            r2.capabilities = "[WPA2-PSK-CCMP][RSN-PSK-CCMP][ESS]";
            r2.level = -53;
            r2.frequency = 5180;
            r2.centerFreq0 = 5210;
            r2.centerFreq1 = 0;
            @SuppressLint("SoonBlockedPrivateApi") Method method2 = r2.getClass().getDeclaredMethod("setFlag", Long.TYPE);
            method2.setAccessible(true);
            Object x2 = method2.invoke(r2, 2); // FLAG_80211mc_RESPONDER

            c3 = ScanResult.class.getDeclaredConstructor();
            c3.setAccessible(true);
            ScanResult r3 = (ScanResult) c1.newInstance();
            r3.SSID = "RTT test";
            r3.BSSID = "70:3a:cb:7f:dc:b4";
            r3.capabilities = "[WPA2-PSK-CCMP][RSN-PSK-CCMP][ESS]";
            r3.level = -56;
            r3.frequency = 5180;
            r3.centerFreq0 = 5210;
            r3.centerFreq1 = 0;
            @SuppressLint("SoonBlockedPrivateApi") Method method3 = r3.getClass().getDeclaredMethod("setFlag", Long.TYPE);
            method3.setAccessible(true);
            Object x3 = method3.invoke(r3, 2); // FLAG_80211mc_RESPONDER

            scanResults.add(r1);
            scanResults.add(r2);
            scanResults.add(r3);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
