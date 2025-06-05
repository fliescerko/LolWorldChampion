package com.example.lolworldchampion;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DamageDetailsActivity extends AppCompatActivity {
    static final String EXTRA_DAMAGE_DATA_LIST = "damageDataList";
    static final String EXTRA_POSITION = "position";

    private static final String TAG = "DamageDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_damage_details);

        Log.d(TAG, "Activity 创建");
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(EXTRA_DAMAGE_DATA_LIST)) {
            Log.d(TAG, "接收到伤害数据");

            try {
                ArrayList<DamageData> damageList =
                        (ArrayList<DamageData>) intent.getSerializableExtra(EXTRA_DAMAGE_DATA_LIST);

                Map<String, DamageData> mergedData = new HashMap<>();
                for (DamageData data : damageList) {
                    String source = data.getName();
                    if (mergedData.containsKey(source)) {
                        DamageData existing = mergedData.get(source);
                        existing.setPhysicalDamage(existing.getPhysicalDamage() + data.getPhysicalDamage());
                        existing.setMagicDamage(existing.getMagicDamage() + data.getMagicDamage());
                        existing.setTrueDamage(existing.getTrueDamage() + data.getTrueDamage());
                    } else {
                        mergedData.put(source, data);
                    }
                }

                updateDamageDetails(mergedData.values());
            } catch (Exception e) {
                Log.e(TAG, "处理数据时出错: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(this, "加载伤害数据失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "未接收到伤害数据");
            Toast.makeText(this, "未找到伤害数据", Toast.LENGTH_SHORT).show();
        }

        // 显示地图标记
        Position position = (Position) intent.getSerializableExtra(EXTRA_POSITION);
        if (position != null) {
            showMapMarker(findViewById(R.id.map_image), findViewById(R.id.map_marker), position);
        }
    }

    private void updateDamageDetails(Collection<DamageData> damageDataList) {
        StringBuilder sb = new StringBuilder();
        sb.append("伤害详情:\n");

        for (DamageData data : damageDataList) {
            sb.append("来源: ").append(data.getName()).append("\n");
            sb.append("总伤害: ").append(data.getTotalDamage()).append("\n");
            sb.append("物理: ").append(data.getPhysicalDamage()).append("\n");
            sb.append("魔法: ").append(data.getMagicDamage()).append("\n");
            sb.append("真实: ").append(data.getTrueDamage()).append("\n\n");
        }

        TextView damageTextView = findViewById(R.id.damage_text_view);
        damageTextView.setText(sb.toString());
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