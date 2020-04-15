package com.example.android.wifirttscan;

public class Trilateration {

//    private double[] ap1 = new double[]{0,0};
//    private double[] ap2 = new double[]{1,4.6};
//    private double[] ap3 = new double[]{4,3};

    private double[] ap1 = new double[]{0,0};
    private double[] ap2 = new double[]{0,4};
    private double[] ap3 = new double[]{4,4};
    private double[] xy = new double[2];

    private double r1;
    private double r2;
    private double r3;

    public Trilateration(double r1, double r2, double r3) {
        this.r1 = Math.abs(r1);
        this.r2 = Math.abs(r2);
        this.r3 = Math.abs(r3);
    }

    public double[] getPosition(){
        double A = -2*ap1[0]+2*ap2[0];
        double B = -2*ap1[1]+2*ap2[1];
        double C = Math.pow(r1,2)-Math.pow(r2,2)-Math.pow(ap1[0],2)
                +Math.pow(ap2[0],2)-Math.pow(ap1[1],2)+Math.pow(ap2[1],2);
        double D = -2*ap2[0]+2*ap3[0];
        double E = -2*ap2[1]+2*ap3[1];
        double F = Math.pow(r2,2)-Math.pow(r3,2)-Math.pow(ap2[0],2)
                +Math.pow(ap3[0],2)-Math.pow(ap2[1],2)+Math.pow(ap3[1],2);
        xy[0] = (C*E - F*B)/(E*A-B*D);
        xy[1] = (C*D - A*F)/(B*D-A*E);

        return xy;
    }


    //TODO doplnit multilateraci
}
