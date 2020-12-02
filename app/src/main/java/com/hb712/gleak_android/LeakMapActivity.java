package com.hb712.gleak_android;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.hb712.gleak_android.base.BaseActivity;
import com.hb712.gleak_android.base.BaseApplication;
import com.hb712.gleak_android.message.net.InitLeakData;
import com.hb712.gleak_android.util.GlobalParam;
import com.hb712.gleak_android.util.ToastUtil;

public class LeakMapActivity extends BaseActivity {

    private MapView mMapView = null;
    private BaiduMap baiduMap = null;
    private LocationClient mLocationClient = null;
    private LatLng myPosition;
    private LatLng newLeakPosition;
    private boolean firstLoad = true;
    private TextView leakName;
    private TextView leakCode;

    // 默认Marker图标
    private BitmapDescriptor bitmap;

    // 选择的leak点位
    private String selectedLeakId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak_map);
        setResult(Activity.RESULT_CANCELED);
        requestLocationPower();
        initView();
    }

    private void requestLocationPower() {
        LocationManager lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 判断手机的GPS是否开启
        if (ok) {
            //判断是否为android6.0系统版本，如果是，需要动态添加权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 没有权限，申请权限
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, GlobalParam.REQUEST_LOCATION_PERMISSION);
                }
            }
        } else {
            ToastUtil.shortInstanceToast("未开启GPS定位服务");
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        if (requestCode == GlobalParam.REQUEST_LOCATION_PERMISSION) {
            for (int grant : grantResult) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("地图需要开启定位功能，请到 “应用信息 -> 权限” 中授予！");
                    builder.setPositiveButton("去手动授权", (dialog, which) -> {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();
                }
            }
        }
    }

    private void initView() {
        leakName = findViewById(R.id.leakName);
        leakCode = findViewById(R.id.leakCode);
        mMapView = findViewById(R.id.bmapView);
        baiduMap = mMapView.getMap();
        setBaiduMapOption();
    }

    private void setBaiduMapOption() {
        // 开启地图的定位图层
        baiduMap.setMyLocationEnabled(true);
        //定位初始化
        mLocationClient = new LocationClient(this);
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        // 打开gps
        option.setOpenGps(true);
        // 设置坐标类型
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        //设置locationClientOption
        mLocationClient.setLocOption(option);
        //注册LocationListener监听器
        mLocationClient.registerLocationListener(new MyLocationListener());
        //开启地图定位图层
        mLocationClient.start();

        // 关闭比例尺
        mMapView.showScaleControl(false);
        // 关闭缩放按钮
        mMapView.showZoomControls(false);
        // 关闭地图俯视
        baiduMap.getUiSettings().setOverlookingGesturesEnabled(false);

        baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                newLeakPosition = mapStatus.target;
            }
        });

        baiduMap.setOnMapLoadedCallback(this::initMarker);
    }

    /**
     * 创建处于屏幕中央的marker
     */
    private void createCenterMarker() {
        // 获取地图坐标投影转换器，用于屏幕像素点坐标系统和地球表面经纬度点坐标系统之间的变换
        Projection projection = baiduMap.getProjection();
        if (null == projection) {
            return;
        }
        // 将地理坐标转换成屏幕坐标
        Point point = projection.toScreenLocation(myPosition);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_binding_point);
        if (null == bitmapDescriptor) {
            return;
        }

        MarkerOptions markerOptions = new MarkerOptions()
                .position(myPosition)
                .icon(bitmapDescriptor)
                .flat(false)
                .fixedScreenPosition(point);
        baiduMap.addOverlay(markerOptions);
        bitmapDescriptor.recycle();
    }

    private void initMarker() {
        //构建Marker图标
        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);

        for (InitLeakData temp : GlobalParam.initLeakData) {
            Bundle bundle = new Bundle();
            bundle.putString("leakId", temp.getId());
            bundle.putString("leakCode", temp.getCode());
            bundle.putString("leakName", temp.getName());
            createMarker(new LatLng(temp.getLatitude(), temp.getLongitude()), bundle);
        }

        baiduMap.setOnMarkerClickListener(marker -> {
            Bundle bundle = marker.getExtraInfo();
            // 显示
            leakName.setText(bundle.getString("leakName"));
            leakCode.setText(bundle.getString("leakCode"));
            // 存储
            selectedLeakId = bundle.getString("leakId");
            return true;
        });
    }

    public void confirmPoint(View view) {
        Intent intent = new Intent();
        intent.putExtra("leakId", selectedLeakId);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /*
    public void addNewPoint(View view) {
        // 获得新点的信息
        // 取消：取消；
        // 确认：得到并返回name，code，period，成员变量newLeakPosition
        NewLeak newLeak = new NewLeak(name, code, newLeakPosition.longitude, newLeakPosition.latitude, period);
        Bundle bundle = new Bundle();
        bundle.putSerializable("NewLeak", newLeak);
        Intent intent = new Intent();
        intent.putExtras(bundle);

        setResult(Activity.RESULT_FIRST_USER, intent);
        finish();
        // 添加到 GlobalParam.initLeakData
    }
    */
    private void createMarker(LatLng point, Bundle bundle) {
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .extraInfo(bundle)
                .icon(bitmap);
        baiduMap.addOverlay(option);
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        baiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        public void onReceiveLocation(BDLocation bdLocation) {
            // mapView 销毁后不在处理新接收的位置
            if (bdLocation == null || mMapView == null) {
                return;
            }
            MyLocationData locationData = new MyLocationData
                    .Builder()
                    .accuracy(bdLocation.getRadius())
                    // 设置获取到的方向信息，顺时针0-360
                    .direction(bdLocation.getDirection())
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .build();
            baiduMap.setMyLocationData(locationData);

            myPosition = new LatLng(mLocationClient.getLastKnownLocation().getLatitude(), mLocationClient.getLastKnownLocation().getLongitude());
            if (firstLoad) {
                firstLoad = false;
                MapStatus mMapStatus = new MapStatus.Builder()
                        .target(myPosition)
                        .zoom(16L)
                        .build();
                //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                //改变地图状态
                baiduMap.setMapStatus(mMapStatusUpdate);
                createCenterMarker();
            }
        }
    }
}