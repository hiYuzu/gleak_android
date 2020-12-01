package com.hb712.gleak_android;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
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
import com.hb712.gleak_android.interfaceabs.HttpInterface;
import com.hb712.gleak_android.util.ToastUtil;

public class LeakMapActivity extends BaseActivity implements HttpInterface {

    private MapView mMapView = null;
    private BaiduMap baiduMap = null;
    private LocationClient mLocationClient = null;
    private LatLng myPosition;
    private boolean firstLoad = true;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak_map);
        setResult(Activity.RESULT_CANCELED);
        initView();
    }

    private void initView() {
        mTextView = findViewById(R.id.positionInfo);
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
                String s = mapStatus.target.latitude + "," + mapStatus.target.longitude;
                mTextView.setText(s);
            }
        });

        baiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                initMarker();
            }
        });
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
        //定义Maker坐标点
        LatLng point = new LatLng(39.963175, 116.400244);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        baiduMap.addOverlay(option);

        baiduMap.setOnMarkerClickListener(marker -> {
            ToastUtil.shortInstanceToast(marker.getTitle());
            return true;
        });
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

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public boolean isDiscardHttp() {
        return isFinishing();
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