/**                     
    * Project:  BTSerial
    * Comments: 主界面类
    * JDK version used: <JDK1.8>
    * Author： Bunny     Github: https://github.com/bunny-chz/
    * Create Date：2022-05-11
    * Version: 1.0
    */

package com.bunny.BTSerial;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hjy.bluetooth.HBluetooth;
import com.hjy.bluetooth.inter.ReceiveCallBack;

import java.io.DataInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 主界面类
 * 本软件接入了HBluetooth项目
 */

public class MainActivity extends Activity {

    private TextView msg_log_text;
    private HBluetooth mHBluetooth;
    Button btn_scan_bt,btn_unit_setting,btn_clear_log;
    TextView length,main_connected_device,tv_length_title;
    SaveUnitSetting saveUnitSetting = new SaveUnitSetting(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();//调用初始化界面控件方法
        initPermission();//调用获取手机权限方法
        startDeviceThreadMainActivity();//调用线程方法
        mHBluetooth = HBluetooth.getInstance();//get HBluetooth类实例
        mHBluetooth
                //开启蓝牙功能
                .enableBluetooth();
        //点击btn_unit_setting设置单位按钮
        btn_unit_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditTextSize();//打开设置单位的对话框
            }
        });
        //点击btn_scan_bt按钮跳转到蓝牙连接界面
        btn_scan_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ConnectBT.class);
                startActivity(i);
            }
        });
        //点击tv_length_title按钮跳转到全屏显示界面
        tv_length_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, FullScreenText.class);
                startActivity(i);
            }
        });
        //点击btn_clear_log按钮清除日志
        btn_clear_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msg_log_text.setText("");
            }
        });
        //设置单位默认值
        if(saveUnitSetting.loadString("UnitParam") == null) {
            saveUnitSetting.saveString("米","UnitParam");
        }
        //设置第一次开启软件，蓝牙连接状态
        if(saveUnitSetting.loadString("CONNECTED_DEVICE") == null) {
            saveUnitSetting.saveString("当前未连接蓝牙设备","CONNECTED_DEVICE");
        }
        //调用监听单片机数据的方法
        initListener();
    }
    //初始化界面控件
    public void initView() {
        msg_log_text = findViewById(R.id.msg_log_text);
        btn_scan_bt = findViewById(R.id.btn_scan_bt);
        tv_length_title = findViewById(R.id.tv_length_title);
        btn_clear_log = findViewById(R.id.btn_clear_log);
        btn_unit_setting = findViewById(R.id.btn_unit_setting);
        length = findViewById(R.id.length);
        main_connected_device = findViewById(R.id.main_connected_device);
    }

    //开启一个子线程
    private void startDeviceThreadMainActivity() {
        new Thread(){
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(500);
                        Message message=new Message();
                        message.what=2;
                        handler.sendMessage(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }while (true);
            }
        }.start();
    }

    //在主线程中进行数据处理
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 2) {
                main_connected_device.setText(saveUnitSetting.loadString("CONNECTED_DEVICE"));
                //读取名为 CONNECTED_DEVICE 的键值对的蓝牙连接状态，不断更新 main_connected_device 文字控件的文字，显示蓝牙连接的状态
            }
        }
    };
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
                        String msg_log = String.format("\n%s", Tools.bytesToString(result));
                        String msg_length = String.format("%s", Tools.bytesToString(result));
                        //实时更新 msg_log_text 和 length 文字控件的文字，显示单片机发送来的数据，
                        // 并加上自定义的单位，单位存储在名为 UnitParam 的键值对中
                        msg_log_text.append(CurrentTime() + msg_log + saveUnitSetting.loadString("UnitParam") + "\n");
                        length.setText(msg_length + saveUnitSetting.loadString("UnitParam"));
                    }
                });
            }
        });
    }

    //自定义设置单位的对话框
    public void EditTextSize() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog dialog = builder.create();
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View view = factory.inflate(R.layout.unit_setting,null);
        dialog.setTitle("编辑单位(默认单位米)");
        dialog.setView(view);
        dialog.setCancelable(true);
        dialog.show();
        Button confirm = view.findViewById(R.id.confirm);
        Button cancel = view.findViewById(R.id.cancel);
        Button clear = view.findViewById(R.id.clear);
        final EditText unit_edit = view.findViewById(R.id.unit_edit);
        if(saveUnitSetting.loadString("UnitParam") != null) {
            unit_edit.setText(saveUnitSetting.loadString("UnitParam"));
        }
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText dialog_edit = view.findViewById(R.id.unit_edit);
                if(!TextUtils.isEmpty(dialog_edit.getText().toString())){
                    saveUnitSetting.saveString(dialog_edit.getText().toString(),"UnitParam");
                    Toast.makeText(MainActivity.this, "编辑成功", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "编辑有误！请输入内容", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unit_edit.setText("");
            }
        });
    }
    //获取当前时间，精确到毫秒
    public String CurrentTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS"); //制定输出格式
        Date d = new Date();
        String currentTime = simpleDateFormat.format(d);
        return "时间: " + currentTime + " ";
    }

    //安卓软件退出时进行的操作
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHBluetooth.release();//断开蓝牙
        saveUnitSetting.saveString("当前未连接蓝牙设备","CONNECTED_DEVICE");//因为开机蓝牙必断开，存储 未连接 ，以防逻辑错误
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        @SuppressLint("InlinedApi")
        String[] permissions = { Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        ArrayList<String> toApplyList = new ArrayList<>();
        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);// 进入到这里代表没有权限.
            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 0x01);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //再按一次退出提示
    long exitTime = 0;
    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            // ToastUtil.makeToastInBottom("再按一次退出应用", MainMyselfActivity);
            Toast.makeText(this, R.string.tap_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
            return;
        }
        finish();
    }
}
