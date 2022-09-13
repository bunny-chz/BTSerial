/**                     
    * Project:  BTSerial
    * Comments: Adapter类
    * JDK version used: <JDK1.8>
    * Author： Bunny     Github: https://github.com/bunny-chz/
    * Create Date：2022-05-11
    * Version: 1.0
    */

package com.bunny.BTSerial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hjy.bluetooth.entity.BluetoothDevice;

import java.util.List;

/**
 *此类是列表的容器操作类
 */

public class MyAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<BluetoothDevice> list;

    public MyAdapter(Context context, List<BluetoothDevice> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item, null);
        }

        TextView tv_name = ViewHolder.getView(view, R.id.tv_name);
        TextView tv_address = ViewHolder.getView(view, R.id.tv_address);
        TextView tv_record = ViewHolder.getView(view,R.id.tv_record);


        BluetoothDevice bluetoothDevice = list.get(position);
        tv_name.setText(bluetoothDevice.getName());
        tv_address.setText(bluetoothDevice.getAddress());

        byte[] scanRecord = bluetoothDevice.getScanRecord();
        if(scanRecord != null && scanRecord.length > 0){
            tv_record.setVisibility(View.VISIBLE);
            tv_record.setText(Tools.bytesToHexString(scanRecord));
        }else {
            tv_record.setVisibility(View.GONE);
        }

        return view;
    }


}
