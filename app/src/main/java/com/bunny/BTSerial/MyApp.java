/**                     
    * Project:  BTSerial
    * Comments: 此类是继承应用类，用于初始化 HBluetooth类，本软件接入了HBluetooth项目
    * JDK version used: <JDK1.8>
    * Author： Bunny     Github: https://github.com/bunny-chz/
    * Create Date：2022-05-11
    * Version: 1.0
    */

package com.bunny.BTSerial;

import android.app.Application;

import com.hjy.bluetooth.HBluetooth;



public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化 HBluetooth
        HBluetooth.init(this);
        HBluetooth.getInstance()
                .setConnectTimeOut(10000);
    }
}
