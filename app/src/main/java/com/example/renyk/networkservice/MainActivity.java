package com.example.renyk.networkservice;

import android.content.Intent;
import android.net.TrafficStats;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    Intent intent;
    NetThread netThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    protected void onDestroy() {
        stopService(intent);
        super.onDestroy();
    }



    public void startThreadTest(View view) {
        //开始监测网速
        if(!isCheck){
            netThread=new NetThread();
            isCheck=true;
            netThread.start();
        }
    }

    public void stopThreadTest(View view) {
        //关闭检测网速
        if(isCheck){
            isCheck=false;
            netThread=null;
        }

    }
    private boolean isCheck=false;
    public class NetThread extends Thread{

        private boolean isFirst=false;
        private long rxtxTotal = 0;
        private int time;
        private double rxtxSpeed = 1.0f;
        private boolean isNetBad = false;
        private DecimalFormat showFloatFormat = new DecimalFormat("0.00");
        @Override
        public void run() {
            do{
                long tempSum = TrafficStats.getTotalRxBytes()+ TrafficStats.getTotalTxBytes();
                if (isFirst) {
                    rxtxTotal = tempSum;
                    isFirst = false;
                }
                long rxtxLast = tempSum - rxtxTotal;
                double tempSpeed = rxtxLast * 1000 / 1000;
                rxtxTotal = tempSum;
                if ((tempSpeed / 1024d) < 20 && (rxtxSpeed / 1024d) < 20) {
                    time += 1;
                } else {
                    time = 0;
                }
                rxtxSpeed = tempSpeed;
                Log.i("testren","=============================" +showFloatFormat.format(tempSpeed / 1024d) + "kb/s");
                if (time >= 5) {//连续五次检测网速都小于20kb/s  断定网速很差.
                    isNetBad = true;
                    Log.i("testren", "===========================================网速差 " + isNetBad);
                    time = 0; //重新检测
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.i("testren","=========睡眠发生异常");
                    e.printStackTrace();
                }
            }while(isCheck);

        }
    }

}
