package com.hb712.gleak_android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import com.hb712.gleak_android.dialog.CommonDialog;
import com.hb712.gleak_android.message.net.InitLeakData;
import com.hb712.gleak_android.message.net.NewLeak;
import com.hb712.gleak_android.util.GlobalParam;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.ToastUtil;

public class LeakMapActivity extends BaseActivity {

    private static final String TAG = LeakMapActivity.class.getSimpleName();

    private MapView mMapView = null;
    private BaiduMap baiduMap = null;
    private LocationClient mLocationClient = null;
    private LatLng myPosition;
    private double newLeakLon;
    private double newLeakLat;
    private boolean firstLoad = true;
    private TextView leakName;
    private TextView leakCode;
    // 默认Marker图标
    private BitmapDescriptor bitmap;
    // 选择的leak点位
    private String selectedLeakId;
    private String selectedLeakName;
    // new leak data
    private EditText newLeakName;
    private EditText newLeakCode;
    private EditText newLeakPeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak_map);
        setResult(Activity.RESULT_CANCELED);
        initView();
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
                newLeakLon = mapStatus.target.longitude;
                newLeakLat = mapStatus.target.latitude;
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
            // 存储
            selectedLeakId = bundle.getString("leakId");
            selectedLeakName = bundle.getString("leakName");
            // 显示
            leakName.setText(selectedLeakName);
            leakCode.setText(bundle.getString("leakCode"));
            return true;
        });
    }

    public void confirmPoint(View view) {
        Intent intent = new Intent();
        intent.putExtra("leakId", selectedLeakId);
        intent.putExtra("leakName", selectedLeakName);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @SuppressLint({"SetTextI18n", "InflateParams", "DefaultLocale"})
    public void addNewPoint(View view) {
        if (newLeakLat == 0 || newLeakLon == 0) {
            CommonDialog.infoDialog("请移动地图确认新增漏点位置！");
            return;
        }
        System.out.println("Lon:" + newLeakLon + "Lat:" + newLeakLat);

        View newLeakDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input_leak, null);

        newLeakName = newLeakDialogView.findViewById(R.id.newLeakName);
        newLeakCode = newLeakDialogView.findViewById(R.id.newLeakCode);
        newLeakPeriod = newLeakDialogView.findViewById(R.id.newLeakPeriod);

        AlertDialog dialog = CommonDialog.getDialog(this, "新增漏点", null, newLeakDialogView, () -> {
            try {
                String name = newLeakName.getText().toString().trim();
                String code = newLeakCode.getText().toString().trim();
                int period = Integer.parseInt(newLeakPeriod.getText().toString().trim());
                if ("".equals(name) || "".equals(code)) {
                    throw new Exception("新增漏点名称与编号不得为空");
                }
                NewLeak newLeak = new NewLeak(name, code, newLeakLon, newLeakLat, period);
                Bundle bundle = new Bundle();
                bundle.putSerializable("NewLeak", newLeak);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(Activity.RESULT_FIRST_USER, intent);
                finish();
            } catch (Exception e) {
                ToastUtil.toastWithLog("非法输入，请重试！");
                LogUtil.errorOut(TAG, e, "非法输入");
            }
        });
        dialog.show();
    }

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

    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
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