package com.cyun.tracker.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyun.tracker.R;
import com.cyun.tracker.base.BaseActivity;
import com.cyun.tracker.bean.TrackBean;
import com.cyun.tracker.db.DBManager;
import com.cyun.tracker.event.RefreshListEvent;
import com.cyun.tracker.util.DialogUtil;
import com.cyun.tracker.util.Finals;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import okhttp3.Call;


/**
 * 创建新笔记
 * ----
 * 使用android的定位功能,
 * 缺点：1 需要依赖于用户开启gps功能
 *       2 有延迟
 *       3 不好用
 */
public class TestCreateActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_date)
    TextView tv_date;
    @BindView(R.id.tv_rest)
    TextView tv_rest;
    @BindView(R.id.et_content)
    EditText et_content;
    @BindView(R.id.iv_image)
    ImageView iv_image;

    @BindView(R.id.iv_del_img)
    ImageView iv_del_img;

    @BindView(R.id.tv_addr)
    TextView tv_addr;

    private int MAX_NUM = 500;

    private TrackBean mTrackBean = new TrackBean();

    private MyLocationListener locListener;
    private LocationManager locationManager;
    private String locationProvider;// 获取GPS信息的提供者


    /**
     * 获取gps状态，进行设置 //
     */
    private void openGPSSettings() {
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
//                showTip("GPS模块正常");
                initGpsLocation(); // 初始化gps设置，并开启定时任务，获取坐标
            } else {
                showToast("请开启GPS");
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                startActivityForResult(intent, 12); // 此为设置完成后返回到获取界面
            }
        } else {
            return;
        }

    }


    /**
     * 初始化gps设置，并开启定时任务，获取坐标
     */
    private void initGpsLocation() {
        Log.i(TAG, "initGpsLocation");
        // 获取位置管理服务
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        criteria.setAltitudeRequired(true);//要求海拔信息
        criteria.setBearingRequired(false);//要求方位信息
        criteria.setCostAllowed(true);//是否允许付费
        criteria.setSpeedAccuracy(criteria.ACCURACY_HIGH);//对速度的精确度
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);//对水平的精确度
        criteria.setSpeedRequired(true);//要求速度信息
        criteria.setPowerRequirement(Criteria.POWER_HIGH); // 功耗
        locationProvider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
        Log.i(TAG, "locationProvider = " + locationProvider);
//        locationProvider = LocationManager.GPS_PROVIDER;

        // 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米
        locListener = new MyLocationListener();
        // todo 设置自动更新的最小时间为4秒
        //绑定监听，有4个参数
        //参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
        //参数2，位置信息更新周期，单位毫秒
        //参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
        //参数4，监听
        //备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新

        // 1秒更新一次，或最小位移变化超过1米更新一次；
        //注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
        locationManager.requestLocationUpdates(locationProvider, 4 * 1000, 200, locListener);
//        getCurrentLocation(locationProvider);

    }

    private class MyLocationListener implements LocationListener {
        // 手机位置发生变动 todo 暂且不处理，只由定时任务去主动获取定位信息
        public void onLocationChanged(Location location) {
            getCurrentLocation(locationProvider);

        }

        // 当某个位置提供者的状态发生改变时
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        // 某个设备打开时
        public void onProviderEnabled(String provider) {

        }


        // 某个设备关闭时
        public void onProviderDisabled(String provider) {

        }
    }


    /**
     * WorkTask定时分发的任务， 获取gps定位数据
     */
    private void getCurrentLocation(String provider) {
        try {
            Location location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
            getAddrByLocation(location);
            Log.i(TAG, "location = " + location.toString());
        } catch (Exception e) {
//            e.printStackTrace(); // 空指针
        }
    }


    /**
     * 根据坐标获取地址信息，访问百度接口
     *
     * @param location
     */
    private void getAddrByLocation(Location location) {
        String latlngStr = "" + location.getLatitude() + location.getLongitude();
        doRequestGet(Finals.Url_BaiduApi + latlngStr, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showToast("未获取到坐标");
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject rootObj = new JSONObject(response);
                    String result = rootObj.getString("result");
                    JSONObject resultObj = new JSONObject(result);
                    String addr = resultObj.getString("formatted_address");
                    tv_addr.setText(addr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_create);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        toolbar.canShowOverflowMenu();
        iv_image.setOnClickListener(this);
        iv_del_img.setOnClickListener(this);

        iv_del_img.setVisibility(View.GONE);

        openGPSSettings(); // 开启定位
        getCurrentLocation(locationProvider);

        //设置返回键可用，如果某个页面想隐藏掉返回键比如首页，可以调用mToolbar.setNavigationIcion(null);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        et_content.setMovementMethod(ScrollingMovementMethod.getInstance());

        tv_date.setText(getToday());

        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //只要编辑框内容有变化就会调用该方法，s为编辑框变化后的内容
                Log.i("onTextChanged", s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 编辑框内容变化之前会调用该方法，s为编辑框内容变化之前的内容
                Log.i("beforeTextChanged", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
                Log.i("afterTextChanged", s.toString());
                if (s.length() > MAX_NUM) {
                    s.delete(MAX_NUM, s.length());
                }
                int num = MAX_NUM - s.length();
//                tv_rest.setText(String.valueOf(num));
                tv_rest.setText("已输入" + s.length() + "字");
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_pic:
                choosePicture();
                break;
            case R.id.action_loc:
                DialogUtil.editDialog(context, "输入位置信息",
                        new DialogUtil.EditDialogSureListener() {
                            @Override
                            public void getContent(String content) {
                                tv_addr.setText(content);
                            }
                        });

                break;
            case R.id.action_done:
                createTrack();
                break;
        }

        return true;
    }

    private void createTrack() {

        String content = et_content.getText().toString().trim();
        mTrackBean.setContent(content);

        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        mTrackBean.setDate(month + "." + day);

        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        mTrackBean.setTime(hour + "." + minute);

        String addr = tv_addr.getText().toString().trim();
        if (TextUtils.isEmpty(addr) || addr.equals(getResources().getString(R.string.str_locating))) {
            addr = "来无影去无踪，找不到小主啊~";
        }
        mTrackBean.setAddr(addr);

        mTrackBean.setTimeStamp(System.currentTimeMillis());
        DBManager.getInstance().insertTrackBean(mTrackBean);

        EventBus.getDefault().postSticky(new RefreshListEvent());

        showToast("小主，您有了一条新的记录哦~");
        finish();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_image:
                choosePicture();
                break;
            case R.id.iv_del_img:
                if (mTrackBean != null && !TextUtils.isEmpty(mTrackBean.getPic())) {
                    iv_image.setImageBitmap(null);
                    iv_image.setBackgroundResource(R.drawable.ic_png);
                    mTrackBean.setPic("");
                    iv_del_img.setVisibility(View.GONE);
                }
                break;
            default:

                break;
        }

    }


    private static final int CODE_CHOOSE_PIC = 0x1;
    private static final int CODE_CHOOSE_PIC_KITKAT = 0x2;

    private void choosePicture() {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        /* 开启Pictures画面Type设定为image */
        intent.setType("image/*");
        /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_GET_CONTENT); // ACTION_OPEN_DOCUMENT
        /* 取得相片后返回本画面 */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            startActivityForResult(intent, CODE_CHOOSE_PIC); // CODE_CHOOSE_PIC_KITKAT
        } else {
            startActivityForResult(intent, CODE_CHOOSE_PIC);
        }

    }

    public String getToday() {
        String value = "";
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        value = sdf.format(date);
        return value;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CODE_CHOOSE_PIC) {

                // 方法1 获取路径
                Uri uri = data.getData();
                String path = getImagePathFromURI(uri);

//                // 第二种方式去读取路径,不适合华为
//                ContentResolver cr = this.getContentResolver();
//                Cursor cursor = cr.query(uri, null, null, null, null);
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                cursor.moveToFirst();
//                String path = cursor.getString(column_index);


                Log.i(TAG, "图片的路径" + path);
                mTrackBean.setPic(path);

                try {
                    Bitmap bmp = BitmapFactory.decodeFile(path);
                    iv_image.setImageBitmap(bmp);
                    iv_del_img.setVisibility(View.VISIBLE);
//                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
//                    iv_image.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CODE_CHOOSE_PIC_KITKAT) {

            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    /**
     * 根据URI获取路径
     *
     * @param uri
     * @return
     */
    public String getImagePathFromURI(Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        String path = null;
        if (cursor != null) {
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();
            cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            if (cursor != null) {
                cursor.moveToFirst();
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
            }
        }
        return path;
    }


}
