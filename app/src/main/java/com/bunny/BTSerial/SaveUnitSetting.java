/**                     
    * Project:  BTSerial
    * Comments: SharedPreferences保存设置数据类
    * JDK version used: <JDK1.8>
    * Author： Bunny     Github: https://github.com/bunny-chz/
    * Create Date：2022-05-11
    * Version: 1.0
    */

package com.bunny.BTSerial;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 此类用于永久存储操作过程中产生的字符串数据，如蓝牙连接状态、设备名
 */

public class SaveUnitSetting {
    private final Context context;
    public SaveUnitSetting(Context context){
        this.context = context;
    }
    public void saveString(String value,String key){
        String name = context.getResources().getString(R.string.SaveUnitSetting);
        SharedPreferences shp = context.getSharedPreferences(name,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        editor.putString(key,value);
        editor.apply();
    }
    public String loadString(String key){
        String name = context.getResources().getString(R.string.SaveUnitSetting);
        SharedPreferences shp = context.getSharedPreferences(name,Context.MODE_PRIVATE);
        return shp.getString(key,null);
    }
}