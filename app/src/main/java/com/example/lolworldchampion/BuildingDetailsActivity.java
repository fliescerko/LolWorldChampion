package com.example.lolworldchampion;

import android.content.res.AssetManager;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildingDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_EVENT = "frameEvent";

    private List<Map<String, String>> towersData = new ArrayList<>();

    private static final Map<String, String> POSITION_MAP = new HashMap<>();

    private static final Map<String, String> TOWER_TYPE_MAP = new HashMap<>();

    private static final Map<String, String> LANE_TYPE_MAP = new HashMap<>();

    static {
        POSITION_MAP.put("BLUE", "蓝色方");
        POSITION_MAP.put("RED", "红色方");

        TOWER_TYPE_MAP.put("OUTER_TURRET", "外塔");
        TOWER_TYPE_MAP.put("INNER_TURRET", "内塔");
        TOWER_TYPE_MAP.put("BASE_TURRET", "高地塔");
        TOWER_TYPE_MAP.put("NEXUS_TURRET", "门牙塔");

        LANE_TYPE_MAP.put("TOP_LINE", "上路");
        LANE_TYPE_MAP.put("MID_LINE", "中路");
        LANE_TYPE_MAP.put("BOT_LINE", "下路");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_details);

        loadTowersData();

        FrameEvent event = (FrameEvent) getIntent().getSerializableExtra(EXTRA_EVENT);
        TextView eventTypeView = findViewById(R.id.event_type_view);
        TextView positionView = findViewById(R.id.position_view);
        TextView buildingInfoView = findViewById(R.id.building_info_view);
        TextView eventDetailView = findViewById(R.id.event_detail_view);

        if (event != null) {
            Position position = event.getPosition();
            if (position != null) {
                positionView.setText("位置坐标: (" + position.getX() + ", " + position.getY() + ")");

                Map<String, String> nearestTower = findNearestTower(position);
                if (nearestTower != null) {
                    String chinesePosition = POSITION_MAP.getOrDefault(nearestTower.get("position"), nearestTower.get("position"));
                    String towerType = nearestTower.get("towerType");
                    String chineseTowerType = TOWER_TYPE_MAP.getOrDefault(towerType, towerType);
                    String laneType = nearestTower.get("laneType");
                    String chineseLaneType = LANE_TYPE_MAP.getOrDefault(laneType, laneType);

                    buildingInfoView.setText("相关建筑: " + chinesePosition + " " + chineseLaneType + " " + chineseTowerType);

                    String eventDetail = getEventDetail(event, nearestTower);
                    if (!eventDetail.isEmpty()) {
                        eventDetailView.setText(eventDetail);
                    } else {
                        eventDetailView.setVisibility(View.GONE);
                    }

                    // 初始化并显示标记
                    View mapMarker = findViewById(R.id.map_marker);
                    showMapMarker(findViewById(R.id.map_image), mapMarker, position);
                } else {
                    buildingInfoView.setText("附近没有已知建筑");
                    eventDetailView.setVisibility(View.GONE);
                }
            } else {
                positionView.setVisibility(View.GONE);
                buildingInfoView.setText("未提供位置信息");
                eventDetailView.setVisibility(View.GONE);
            }
        } else {
            eventTypeView.setVisibility(View.GONE);
            positionView.setVisibility(View.GONE);
            buildingInfoView.setVisibility(View.GONE);
            eventDetailView.setVisibility(View.GONE);
            Toast.makeText(this, "未提供事件信息", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTowersData() {
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("fangyuta.csv");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    Map<String, String> towerData = new HashMap<>();
                    towerData.put("buildingId", parts[0].trim());
                    towerData.put("position", parts[1].trim());
                    towerData.put("buildingType", parts[2].trim());
                    towerData.put("laneType", parts[3].trim());
                    towerData.put("towerType", parts[4].trim());
                    towerData.put("posX", parts[5].trim());
                    towerData.put("posY", parts[6].trim());

                    towersData.add(towerData);
                }
            }

            reader.close();
        } catch (IOException e) {
            Log.e("BuildingDetails", "加载防御塔数据失败", e);
            Toast.makeText(this, "加载数据失败", Toast.LENGTH_SHORT).show();
        }
    }

    private Map<String, String> findNearestTower(Position position) {
        Map<String, String> nearest = null;
        double minDistance = Double.MAX_VALUE;
        int x = position.getX();
        int y = position.getY();

        for (Map<String, String> towerData : towersData) {
            try {
                int towerX = Integer.parseInt(towerData.get("posX"));
                int towerY = Integer.parseInt(towerData.get("posY"));

                double distance = Math.sqrt(Math.pow(x - towerX, 2) + Math.pow(y - towerY, 2));

                if (distance < minDistance && distance < 1000) {
                    minDistance = distance;
                    nearest = towerData;
                }
            } catch (NumberFormatException e) {
                Log.e("BuildingDetails", "解析防御塔坐标失败", e);
            }
        }

        return nearest;
    }

    private String getEventDetail(FrameEvent event, Map<String, String> towerData) {
        Position position = event.getPosition();
        if (position == null) return "";

        int x = position.getX();
        int y = position.getY();
        double radius = 300;

        String buildingType = towerData.get("buildingType");
        String towerType = towerData.get("towerType");
        String colorposition = towerData.get("position");
        String laneType = towerData.get("laneType");

        String chinesePosition = POSITION_MAP.getOrDefault(colorposition, colorposition);
        String chineseTowerType = TOWER_TYPE_MAP.getOrDefault(towerType, towerType);
        String chineseLaneType = LANE_TYPE_MAP.getOrDefault(laneType, laneType);

        if ("TOWER_BUILDING".equals(buildingType)) {
            switch (towerType) {
                case "OUTER_TURRET":
                    radius = 400;
                    break;
                case "INNER_TURRET":
                    radius = 350;
                    break;
                case "BASE_TURRET":
                case "NEXUS_TURRET":
                    radius = 250;
                    break;
            }

            int towerX = Integer.parseInt(towerData.get("posX"));
            int towerY = Integer.parseInt(towerData.get("posY"));
            double distance = Math.sqrt(Math.pow(x - towerX, 2) + Math.pow(y - towerY, 2));

            if ("BUILDING_KILL".equals(event.getType()) && distance < radius) {
                return "事件类型：防御塔击杀";
            } else if ("TURRET_PLATE_DESTROYED".equals(event.getType()) && distance < radius * 1.5) {
                return "事件类型：获取镀层";
            }
        } else if ("INHIBITOR_BUILDING".equals(buildingType) &&
                "BUILDING_KILL".equals(event.getType())) {
            int towerX = Integer.parseInt(towerData.get("posX"));
            int towerY = Integer.parseInt(towerData.get("posY"));
            double distance = Math.sqrt(Math.pow(x - towerX, 2) + Math.pow(y - towerY, 2));

            if (distance < 200) {
                return "摧毁水晶: " + chinesePosition + " " + chineseLaneType + " 水晶";
            }
        }

        return "";
    }

    private static final String TAG = "BuildingDetails";

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
        int mapWidth = mapImage.getWidth();
        int mapHeight = mapImage.getHeight();
        int imageWidth = 14800; // 图片的原始宽度
        int imageHeight = 14800; // 图片的原始高度

        // 计算图片上的坐标
        int markerX = (int) ((double) x / imageWidth * mapWidth);
        // 调整 y 坐标，使其从上往下增加
        int markerY = mapHeight - (int) ((double) y / imageHeight * mapHeight);

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