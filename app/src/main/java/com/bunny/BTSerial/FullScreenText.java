/**                     
    * Project:  BTSerial
    * Comments: 全屏实时显示距离类
    * JDK version used: <JDK1.8>
    * Author： Bunny     Github: https://github.com/bunny-chz/
    * Create Date：2022-05-11
    * Version: 1.0
    */

package com.bunny.BTSerial;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.hjy.bluetooth.HBluetooth;
import com.hjy.bluetooth.inter.ReceiveCallBack;

import java.io.DataInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FullScreenText extends Activity {
    private TextView tv_full_screen;
    SaveUnitSetting saveUnitSetting = new SaveUnitSetting(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.text_full_screen);
        //初始化界面控件
        tv_full_screen = findViewById(R.id.tv_full_screen);
        TextView tv_date = findViewById(R.id.tv_date);
        //显示日期
        tv_date.setText(CurrentTime());
        //调用监听单片机数据的方法
        initListener();
    }

    //监听单片机发送来的数据
    public void initListener() {
        HBluetooth.getInstance().setReceiver(new ReceiveCallBack() {
            @Override
            public void onReceived(DataInputStream dataInputStream, final byte[] result) {
                //设备发过来的数据将在这里出现
                Log.e("mylog", "收到蓝牙设备返回数据->" + Tools.bytesToString(result));
                //开启一个UI线程处理界面的变化
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        //处理单片机发送来的数据转化成字符串，并指定形式
                        String msg_length = String.format("%s", Tools.bytesToString(result));
                        //实时更新 tv_full_screen 文字控件的文字，显示单片机发送来的数据，
                        // 并加上自定义的单位，单位存储在名为 UnitParam 的键值对中
                        tv_full_screen.setText(msg_length + saveUnitSetting.loadString("UnitParam"));
                    }
                });
            }
        });
    }

    //获取现在的日期
    public String CurrentTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd"); //制定输出格式
        Date d = new Date();
        return simpleDateFormat.format(d);
    }
}
