package com.example.lolworldchampion;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MonsterDetailsActivity extends AppCompatActivity {
     static final String EXTRA_MONSTER_TYPE = "monsterType";
     static final String EXTRA_MONSTER_SUBTYPE = "monsterSubType";
     static final String EXTRA_POSITION = "position"; // 添加用于传递位置的Extra键名

    private static final String TAG = "MonsterDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monster_details);

        // 获取传递的野怪信息
        String monsterType = getIntent().getStringExtra(EXTRA_MONSTER_TYPE);
        String monsterSubType = getIntent().getStringExtra(EXTRA_MONSTER_SUBTYPE);
        Position position = (Position) getIntent().getSerializableExtra(EXTRA_POSITION); // 获取位置信息

        TextView typeView = findViewById(R.id.monster_type_view);
        TextView subTypeView = findViewById(R.id.monster_subtype_view);

        // 显示野怪类型
        typeView.setText("野怪类型: " + mapMonsterType(monsterType));

        // 显示野怪种类
        if (!monsterSubType.isEmpty()) {
            subTypeView.setText("野怪种类: " + mapMonsterSubType(monsterSubType));
        } else {
            subTypeView.setVisibility(View.GONE);
        }

        // 显示地图标记
        if (position != null) {
            showMapMarker(findViewById(R.id.map_image), findViewById(R.id.map_marker), position);
        }
    }

    private String mapMonsterType(String type) {
        switch (type) {
            case "DRAGON": return "巨龙";
            case "BARON_NASHOR": return "纳什男爵";
            case "RIFTHERALD": return "峡谷先锋";
            default: return type.isEmpty() ? "未知类型" : type.toUpperCase();
        }
    }

    private String mapMonsterSubType(String subType) {
        switch (subType) {
            case "FIRE_DRAGON": return "火龙";
            case "AIR_DRAGON": return "风龙";
            case "EARTH_DRAGON": return "土龙";
            case "WATER_DRAGON": return "水龙";
            case "CHEMTECH_DRAGON": return "炼金龙";
            case "HEXTECH_DRAGON": return "电龙";
            case "ELDER_DRAGON": return "远古巨龙";
            default: return subType.isEmpty() ? "未知种类" : subType.toUpperCase();
        }
    }

    private void showMapMarker(final ImageView mapImage, final View mapMarker, final Position position) {
        Log.d(TAG, "showMapMarker: 开始处理标记，坐标 x=" + position.getX() + ", y=" + position.getY());

        // 确保地图视图已完成布局
        if (mapImage.getWidth() <= 0 || mapImage.getHeight() <= 0) {
            Log.d(TAG, "showMapMarker: 地图尺寸未确定，等待布局完成");
            mapImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mapImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        mapImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                    Log.d(TAG, "showMapMarker: 地图布局完成，尺寸 width=" + mapImage.getWidth() + ", height=" + mapImage.getHeight());
                    setMarkerPosition(mapImage, mapMarker, position.getX(), position.getY());
                }
            });
        } else {
            Log.d(TAG, "showMapMarker: 地图尺寸已确定，直接设置标记位置");
            setMarkerPosition(mapImage, mapMarker, position.getX(), position.getY());
        }
    }

    private void setMarkerPosition(ImageView mapImage, View mapMarker, int x, int y) {
        int imageWidth = 14800; // 图片的原始宽度
        int imageHeight = 14800; // 图片的原始高度

        // 获取ImageView的实际显示尺寸
        int imageViewWidth = mapImage.getWidth();
        int imageViewHeight = mapImage.getHeight();

        // 计算图片上的坐标
        int markerX = (int) ((double) x / imageWidth * imageViewWidth);
        int markerY = imageViewHeight - (int) ((double) y / imageHeight * imageViewHeight); // Y坐标需要翻转

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mapMarker.getLayoutParams();
        if (params == null) {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
        // 确保标记居中
        params.leftMargin = markerX - mapMarker.getWidth() / 2;
        params.topMargin = markerY - mapMarker.getHeight() / 2;
        mapMarker.setLayoutParams(params);
        mapMarker.setVisibility(View.VISIBLE);
    }

    // 在 onResume 方法中确保标记可见
    @Override
    protected void onResume() {
        super.onResume();
        View mapMarker = findViewById(R.id.map_marker);
        if (mapMarker != null && mapMarker.getVisibility() != View.VISIBLE) {
            mapMarker.setVisibility(View.VISIBLE);
            Log.d(TAG, "onResume: 重新设置标记为可见状态");
        }
    }
}