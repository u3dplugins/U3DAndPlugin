package com.wonderent.sdk.util;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 摇一摇工具类
 * Created by 9130game on 2018/5/10.
 */

public class ShakeUtils implements SensorEventListener {

    private Context mContext = null;
    private SensorManager mSensorManager = null;
    private OnShakeListener mOnShakeListener = null;
    private volatile List<OnSensorRotationListener> listenerList = new ArrayList<>();
    //摇晃检测阈值，决定了对摇晃的敏感程度，越小越敏感。
    public final int SHAKE_THRESHOLD = 40;
    //检测的时间间隔
    static final int UPDATE_INTERVAL = 100;
    //上一次检测的时间
    long mLastUpdateTime;
    //上一次检测时，加速度在x、y、z方向上的分量，用于和当前加速度比较求差。
    float mLastX, mLastY, mLastZ;

    private static ShakeUtils instance = null;
    public static ShakeUtils getInstance(Context context){
        if(instance == null){
            synchronized (ShakeUtils.class){
                if(instance == null){
                    instance = new ShakeUtils(context);
                }
            }
        }
        return instance;
    }

    public ShakeUtils(Context context){
        if(SupportAndroidVersion() && context != null) {
            mContext = context;
            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
    }

    public void setOnShakeListener(OnShakeListener listener){
        if(listener != null) {
            mOnShakeListener = listener;
        }
    }

    public void setmOnSensorRotationListener(OnSensorRotationListener listener){
        if (listener != null && !listenerList.contains(listener)){
            listenerList.add(listener);
        }
    }

    public void registerListener(){
        //传感器灵敏度分为四级，从上往下灵敏度依次降低  SENSOR_DELAY_FASTEST ->SENSOR_DELAY_GAME ->SENSOR_DELAY_UI ->SENSOR_DELAY_NORMAL
        if(SupportAndroidVersion() && mSensorManager != null)
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    public void unregisterListener(){
        if(SupportAndroidVersion() && mSensorManager != null)
            mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long diffTime = currentTime - mLastUpdateTime;
            if (diffTime < UPDATE_INTERVAL) {
                return;
            }
            mLastUpdateTime = currentTime;
            //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float deltaX = x - mLastX;
            float deltaY = y - mLastY;
            float deltaZ = z - mLastZ;
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            float delta = (float) (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / diffTime * 100);
            // 当加速度的差值大于指定的阈值，认为这是一个摇晃
            if (delta > SHAKE_THRESHOLD) {
                if (null != mOnShakeListener) {
                    mOnShakeListener.onShake();
                }
            }
        }
        if (listenerList != null){
            //rotation: 0（Surface.ROTATION_0---竖屏正向）、1（Surface.ROTATION_90---横屏正向）、2（Surface.ROTATION_180---竖屏反向）、3（Surface.ROTATION_270---横屏反向）
            int rotation = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
            for (OnSensorRotationListener listener : listenerList)
                listener.onSensorRotation(rotation);
        }
    }

    /**
     * 判断当前设备android版本是否符合要求
     * @return boolean
     */
    public boolean SupportAndroidVersion() {
        int curApiVersion = android.os.Build.VERSION.SDK_INT;
        // This work only for android 4.0+
        if (curApiVersion >= 14) {
            return true;
        }
        return false;
    }

    public interface OnShakeListener{
        void onShake();//摇一摇事件回调
    }

    public interface OnSensorRotationListener{
        void onSensorRotation(int rotation);//屏幕方向回调
    }
}