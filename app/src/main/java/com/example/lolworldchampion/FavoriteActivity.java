package com.example.lolworldchampion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteAdapter adapter;
    private List<FavoriteEvent> favoriteEvents;

    public static void start(Context context) {
        Intent intent = new Intent(context, FavoriteActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("收藏的事件");
        }

        recyclerView = findViewById(R.id.favoriteRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadFavoriteEvents();
        setupAdapter();
    }

    private void loadFavoriteEvents() {
        favoriteEvents = LocalStorageUtil.loadFavoriteEvents(this);
    }

    private void setupAdapter() {
        adapter = new FavoriteAdapter(favoriteEvents, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavoriteEvents();
        if (adapter != null) {
            adapter.setFavoriteEvents(favoriteEvents);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

        private List<FavoriteEvent> favoriteEvents;
        private Context context;

        public FavoriteAdapter(List<FavoriteEvent> favoriteEvents, Context context) {
            this.favoriteEvents = favoriteEvents;
            this.context = context;
        }

        public void setFavoriteEvents(List<FavoriteEvent> favoriteEvents) {
            this.favoriteEvents = favoriteEvents;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_event, parent, false);
            return new FavoriteViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
            FavoriteEvent favoriteEvent = favoriteEvents.get(position);
            FrameEvent event = favoriteEvent.getEvent();

            holder.eventTime.setText(formatTimestamp(event.getTimestamp()));
            holder.eventType.setText(EventTypeMapper.getDisplayText(event.getType()));
            holder.eventNote.setText(favoriteEvent.getNote());
            holder.matchId.setText("比赛ID: " + favoriteEvent.getMatchId()); // 显示 matchId

            // 设置事件详情文本
            StringBuilder detailsBuilder = new StringBuilder();
            if ("CHAMPION_KILL".equals(event.getType())) {
                detailsBuilder.append(event.getKillerName()).append(" 击杀了 ").append(event.getVictimName());
                if (event.getAssistingParticipantIds() != null && !event.getAssistingParticipantIds().isEmpty()) {
                    detailsBuilder.append(" (助攻: ");
                    for (int i = 0; i < event.getAssistingParticipantIds().size(); i++) {
                        detailsBuilder.append(event.getAssistingParticipantIds().get(i));
                        if (i < event.getAssistingParticipantIds().size() - 1) {
                            detailsBuilder.append(", ");
                        }
                    }
                    detailsBuilder.append(")");
                }
            } else if ("BUILDING_KILL".equals(event.getType())) {
                detailsBuilder.append(event.getKillerName()).append(" 摧毁了 ");
                if ("TOWER_BUILDING".equals(event.getBuildingType())) {
                    detailsBuilder.append(event.getLaneType()).append(" ").append(event.getTowerType());
                } else {
                    detailsBuilder.append(event.getBuildingType());
                }
            } else if ("ELITE_MONSTER_KILL".equals(event.getType())) {
                detailsBuilder.append(event.getKillerName()).append(" 击杀了 ");
                if ("DRAGON".equals(event.getMonsterType())) {
                    detailsBuilder.append(event.getMonsterSubType());
                } else {
                    detailsBuilder.append(event.getMonsterType());
                }
            } else {
                detailsBuilder.append("事件类型: ").append(EventTypeMapper.getDisplayText(event.getType()));
            }

            holder.eventDetails.setText(detailsBuilder.toString());

            holder.itemView.setOnClickListener(v -> showEditNoteDialog(favoriteEvent));

            holder.deleteButton.setOnClickListener(v -> {
                favoriteEvents.remove(position);
                LocalStorageUtil.saveFavoriteEvents(context, favoriteEvents);
                notifyItemRemoved(position);
                Toast.makeText(context, "已移除收藏", Toast.LENGTH_SHORT).show();
            });
        }

        private void showEditNoteDialog(FavoriteEvent favoriteEvent) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("编辑备注");

            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_favorite, null);
            EditText noteEditText = dialogView.findViewById(R.id.noteEditText);
            noteEditText.setText(favoriteEvent.getNote());
            builder.setView(dialogView);

            builder.setPositiveButton("保存", (dialog, which) -> {
                String newNote = noteEditText.getText().toString().trim();
                favoriteEvent.setNote(newNote);
                LocalStorageUtil.saveFavoriteEvents(context, favoriteEvents);
                notifyDataSetChanged();
                Toast.makeText(context, "备注已更新", Toast.LENGTH_SHORT).show();
            });

            builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

            builder.show();
        }

        @Override
        public int getItemCount() {
            return favoriteEvents.size();
        }

        private String formatTimestamp(long timestamp) {
            int minutes = (int) (timestamp / 60000);
            int seconds = (int) ((timestamp % 60000) / 1000);
            return String.format("%02d:%02d", minutes, seconds);
        }

        public class FavoriteViewHolder extends RecyclerView.ViewHolder {
            TextView eventTime;
            TextView eventType;
            TextView eventDetails;
            TextView eventNote;
            TextView matchId; // 添加 matchId 显示
            View deleteButton;

            public FavoriteViewHolder(@NonNull View itemView) {
                super(itemView);
                eventTime = itemView.findViewById(R.id.favoriteEventTime);
                eventType = itemView.findViewById(R.id.favoriteEventType);
                eventDetails = itemView.findViewById(R.id.favoriteEventDetails);
                eventNote = itemView.findViewById(R.id.favoriteEventNote);
                matchId = itemView.findViewById(R.id.favoriteMatchId); // 初始化 matchId 显示
                deleteButton = itemView.findViewById(R.id.deleteButton);
            }
        }
    }
}