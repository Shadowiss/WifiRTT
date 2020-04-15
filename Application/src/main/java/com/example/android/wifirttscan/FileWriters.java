package com.example.android.wifirttscan;

import android.net.wifi.rtt.RangingResult;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

class FileWriters {
    private int j = 1;
    private int sumDist = 0;
    private int diameterMM;

    FileWriters() {
        diameterMM = 0;
    }

    void fileWriterOneAP(List<RangingResult> list) {

        FileWriter fWriter;
        File sdCardFile = new File(Environment.getExternalStorageDirectory() + "/WifiRTTmeasured.txt");
        Log.d("TAG", sdCardFile.getPath()); //<-- check the log to make sure the path is correct.
        try {
            fWriter = new FileWriter(sdCardFile, true);
            //if(sdCardFile.length() < 100000) {
            if (j <= 100) {
                fWriter.write(j++ + " " + list.get(0) + System.lineSeparator());
                sumDist = sumDist + list.get(0).getDistanceMm();
                diameterMM = sumDist / j;
                fWriter.write("Průměrná vzdálenost je: " + diameterMM + " mm" + System.lineSeparator());
                fWriter.flush();
                fWriter.close();
            } else {
                sdCardFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fileWriter2(List<RangingResult> list) {

        FileWriter fWriter;
        File sdCardFile = new File(Environment.getExternalStorageDirectory() + "/WifiRTTmeasured.txt");
        Log.d("TAG", sdCardFile.getPath()); //<-- check the log to make sure the path is correct.
        try {
            fWriter = new FileWriter(sdCardFile, true);
            //if(sdCardFile.length() < 100000) {
            if (j <= 100) {
                for (int i = 0; i < list.size(); i++) {
                    fWriter.write(j++ + " " + list.get(i) + System.lineSeparator());
                    sumDist = sumDist + list.get(i).getDistanceMm();

                    double ap1 = 0;
                    double ap2 = 0;
                    double ap3 = 0;

                    if (list.size() == 3) {
                        for (int x = 0; x < 3; x++) {
                            switch (list.get(x).getMacAddress().toString()) {
                                case "70:3a:cb:7f:23:fb":
                                    ap1 = list.get(x).getDistanceMm();
                                    break;
                                case "70:3A:CB:7F:DC:17":
                                    ap2 = list.get(x).getDistanceMm();
                                    break;
                                case " 70:3A:CB:7F:22:82":
                                    ap3 = list.get(x).getDistanceMm();
                                    break;
                            }
                        }
                    }
                }
                diameterMM = sumDist / j;
                fWriter.write("Průměrná vzdálenost je: " + diameterMM + " mm" + System.lineSeparator());
                fWriter.flush();
                fWriter.close();
            } else {
                //sdCardFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
