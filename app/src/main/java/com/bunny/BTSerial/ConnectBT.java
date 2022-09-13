/**                     
    * Project:  BTSerial
    * Comments: 蓝牙连接类
    * JDK version used: <JDK1.8>
    * Author： Bunny     Github: https://github.com/bunny-chz/
    * Create Date：2022-05-11
    * Version: 1.0
    */

package com.bunny.BTSerial;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hjy.bluetooth.HBluetooth;
import com.hjy.bluetooth.entity.BluetoothDevice;
import com.hjy.bluetooth.exception.BluetoothException;
import com.hjy.bluetooth.inter.BleNotifyCallBack;
import com.hjy.bluetooth.inter.ConnectCallBack;
import com.hjy.bluetooth.inter.ScanCallBack;
import com.hjy.bluetooth.operator.abstra.Sender;

import java.util.ArrayList;
import java.util.List;

/*
 *此类用于连接蓝牙界面的蓝牙扫描、连接、断开操作，另外加有增加界面操作体验的操作
 */

public class ConnectBT extends Activity implements AdapterView.OnItemClickListener {

    private final static String TAGBT = "CONNECT_BT_LOG";
    private final static String TAG = "BT_LOG";
    private final List<BluetoothDevice> list = new ArrayList<>();
    private MyAdapter adapter;
    ImageView img_loading;
    TextView connect_status,connected_device;
    Button btn_scan_start,btn_disconnect;
    private HBluetooth mHBluetooth;
    SaveUnitSetting saveUnitSetting = new SaveUnitSetting(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_bt);
        initView();//调用初始化界面控件方法
        startDeviceThread();//调用子线程方法
        mHBluetooth = HBluetooth.getInstance();//get  HBluetooth类的实例
        scanDevice();//调用扫描蓝牙的方法，进入连接蓝牙界面就自动扫描
    }

    //初始化界面控件
    private void initView() {
        //下面这些是初始化界面控件的代码，如图片、按钮
        img_loading = findViewById(R.id.img_loading);
        connect_status = findViewById(R.id.connect_status);
        connected_device = findViewById(R.id.connected_device);
        ListView listView = findViewById(R.id.listView);
        adapter = new MyAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        btn_scan_start = findViewById(R.id.btn_scan_start);
        btn_disconnect = findViewById(R.id.btn_disconnect);
        //扫描蓝牙按钮
        btn_scan_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanDevice();//调用扫描蓝牙方法/
            }
        });
        //断开连接按钮
        btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHBluetooth.release();//调用断开连接方法
                connect_status.setText("已断开");//蓝牙断开，文字控件显示已断开
            }
        });
    }

    //开启一个子线程
    private void startDeviceThread() {
        new Thread(){
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(500);//500毫秒执行一次线程操作
                        Message message=new Message();
                        message.what=1;
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
    private final Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                connected_device.setText(saveUnitSetting.loadString("CONNECTED_DEVICE"));
                //读取名为 CONNECTED_DEVICE 的键值对的蓝牙连接状态，不断更新 connected_device 文字控件的文字，显示蓝牙连接的状态
            }
        }
    };

    private void scanDevice() {
        if (list != null && list.size() > 0) {
            list.clear(); //判断蓝牙设备列表，不为空且列表size大于0，就清除列表
            adapter.notifyDataSetChanged();//响应列表的改变
        }
        //调用HBluetooth类的scan方法扫描蓝牙设备，扫描6秒就自动停止扫描
        mHBluetooth.scan(android.bluetooth.BluetoothDevice.DEVICE_TYPE_CLASSIC, 6000, new ScanCallBack() {
            //开始扫描
            @Override
            public void onScanStart() {
                Log.i(TAGBT, "开始扫描");
                //扫描动画，图片开始可见并旋转
                Animation operatingAnim = AnimationUtils.loadAnimation(ConnectBT.this, R.anim.rotate);
                operatingAnim.setInterpolator(new LinearInterpolator());
                img_loading.startAnimation(operatingAnim);
                img_loading.setVisibility(View.VISIBLE);
                //connect_status 文字控件的文字显示蓝牙扫描的状态
                connect_status.setText("扫描中...");
            }

            @Override
            public void onScanning(List<BluetoothDevice> scannedDevices, BluetoothDevice currentScannedDevice) {
                if (scannedDevices != null && scannedDevices.size() > 0) {
                    list.clear();
                    list.addAll(scannedDevices);//把扫描到的蓝牙设备，加入列表
                    adapter.notifyDataSetChanged();
                }
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onError(int errorType, String errorMsg) {
                Log.e(TAGBT, "errorType:"+errorType+"  errorMsg:"+errorMsg);
                //connect_status 文字控件的文字显示蓝牙扫描的错误
                connect_status.setText("扫描出错\n错误类型:" + errorType + "  错误原因:" + errorMsg);
            }
            @Override
            public void onScanFinished(List<BluetoothDevice> bluetoothDevices) {
                Log.i(TAGBT, "扫描结束");
                //扫描动画，扫描完成图片不可见
                Animation operatingAnim = AnimationUtils.loadAnimation(ConnectBT.this, R.anim.rotate);
                operatingAnim.setInterpolator(new LinearInterpolator());
                img_loading.clearAnimation();
                img_loading.setVisibility(View.INVISIBLE);
                connect_status.setText("扫描结束");
                if (bluetoothDevices != null && bluetoothDevices.size() > 0) {
                    list.clear();
                    list.addAll(bluetoothDevices);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final BluetoothDevice device = list.get(i);//点击列表的第 i 设备
        //调用连接器连接蓝牙设备
        mHBluetooth.connect(device, new ConnectCallBack() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onConnecting() {
                Log.i(TAG, "连接中...");
                connect_status.setText("连接 " + device.getName() + " 中...");//connect_status 文字控件的文字显示蓝牙连接的状态
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onConnected(Sender sender) {
                Log.i(TAG, "连接成功,isConnected:" + mHBluetooth.isConnected());
                connect_status.setText("已经连接 " + device.getName() + device.getAddress());

                saveUnitSetting.saveString("当前连接的蓝牙设备 " + device.getName()+ " ( " + device.getAddress() + " )","CONNECTED_DEVICE");
                finish();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onDisConnecting() {
                Log.i(TAG, "断开连接中...");
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onDisConnected() {
                Log.i(TAG, "已断开连接,isConnected:" + mHBluetooth.isConnected());
                connect_status.setText("已断开 " + device.getName() + device.getAddress());
                saveUnitSetting.saveString("当前未连接蓝牙设备","CONNECTED_DEVICE");
            }

            @Override
            public void onError(int errorType, String errorMsg) {
                Log.i(TAG, "错误类型：" + errorType + " 错误原因：" + errorMsg);
                Toast.makeText(ConnectBT.this, "请重新点击连接或者重启软件重试\n" + "错误类型：" + errorType + " 错误原因：" + errorMsg, Toast.LENGTH_SHORT).show();
                connect_status.setText("出现错误，请重新点击连接或者重启软件重试");
                saveUnitSetting.saveString("当前未连接蓝牙设备","CONNECTED_DEVICE");
            }
            //低功耗蓝牙才需要BleNotifyCallBack
            //经典蓝牙可以只调两参方法connect(BluetoothDevice device, ConnectCallBack connectCallBack)
        }, new BleNotifyCallBack() {
            @Override
            public void onNotifySuccess() {
                Log.i(TAG, "打开通知成功");
            }

            @Override
            public void onNotifyFailure(BluetoothException bleException) {
                Log.i(TAG, "打开通知失败：" + bleException.getMessage());
            }
        });
    }
}
