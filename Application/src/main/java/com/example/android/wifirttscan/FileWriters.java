package com.example.android.wifirttscan;

import android.net.wifi.rtt.RangingResult;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

class FileWriters {
    private int j = 3;
    private int sumDist1 = 0;
    private int sumDist2 = 0;
    private int sumDist3 = 0;
    private int diameterMM1 = 0;
    private int diameterMM2 = 0;
    private int diameterMM3 = 0;

    FileWriters() {
    }

    void fileWriterOneAP(List<RangingResult> list) {

        FileWriter fWriter;
        File sdCardFile = new File(Environment.getExternalStorageDirectory() + "/WifiRTTmeasured.txt");
        Log.d("TAG", sdCardFile.getPath()); //<-- check the log to make sure the path is correct.
        try {
            fWriter = new FileWriter(sdCardFile, true);
            //if(sdCardFile.length() < 100000) {
            if (j <= 100) {
                for (int i = 0; i < list.size(); i++) {
                    fWriter.write(j++ + " " + list.get(i) + System.lineSeparator());
                    switch (i) {
                        case 0:
                            sumDist1 = sumDist1 + Math.abs(list.get(i).getDistanceMm());
                            diameterMM1 = sumDist1 / (j / 3);
                            break;
                        case 1:
                            sumDist2 = sumDist2 + Math.abs(list.get(i).getDistanceMm());
                            diameterMM2 = sumDist2 / (j / 3);
                            break;
                        case 2:
                            sumDist3 = sumDist3 + Math.abs(list.get(i).getDistanceMm());
                            diameterMM3 = sumDist3 / (j / 3);
                            break;
                    }
                    //sumDist = sumDist + Math.abs(list.get(0).getDistanceMm());
                    //diameterMM = sumDist / j;
                }
            }else {
                fWriter.write("Průměrná vzdálenost k AP1 je: " + diameterMM1 + " mm" + System.lineSeparator());
                fWriter.write("Průměrná vzdálenost k AP2 je: " + diameterMM2 + " mm" + System.lineSeparator());
                fWriter.write("Průměrná vzdálenost k AP3 je: " + diameterMM3 + " mm" + System.lineSeparator());
            }
            fWriter.flush();
            fWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void fileWriter2(List<RangingResult> list) {
//
//        FileWriter fWriter;
//        File sdCardFile = new File(Environment.getExternalStorageDirectory() + "/WifiRTTmeasured.txt");
//        Log.d("TAG", sdCardFile.getPath()); //<-- check the log to make sure the path is correct.
//        try {
//            fWriter = new FileWriter(sdCardFile, true);
//            //if(sdCardFile.length() < 100000) {
//            if (j <= 100) {
//                for (int i = 0; i < list.size(); i++) {
//                    fWriter.write(j++ + " " + list.get(i) + System.lineSeparator());
//                    sumDist = sumDist + list.get(i).getDistanceMm();
//
//                    double ap1 = 0;
//                    double ap2 = 0;
//                    double ap3 = 0;
//
//                    if (list.size() == 3) {
//                        for (int x = 0; x < 3; x++) {
//                            switch (list.get(x).getMacAddress().toString()) {
//                                case "70:3a:cb:7f:23:fb":
//                                    ap1 = list.get(x).getDistanceMm();
//                                    break;
//                                case "70:3A:CB:7F:DC:17":
//                                    ap2 = list.get(x).getDistanceMm();
//                                    break;
//                                case " 70:3A:CB:7F:22:82":
//                                    ap3 = list.get(x).getDistanceMm();
//                                    break;
//                            }
//                        }
//                    }
//                }
//                diameterMM = sumDist / j;
//                fWriter.write("Průměrná vzdálenost je: " + diameterMM + " mm" + System.lineSeparator());
//                fWriter.flush();
//                fWriter.close();
//            } else {
//                //sdCardFile.delete();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
