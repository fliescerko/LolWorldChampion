package com.example.lolworldchampion;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lolworldchampion.R;
import com.example.lolworldchampion.MatchSummary;
import com.example.lolworldchampion.TimelineActivity;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {
    private List<MatchSummary> matchSummaries;
    private OnMatchClickListener listener;

    public interface OnMatchClickListener {
        void onMatchClick(String matchId);
    }

    public MatchAdapter(List<MatchSummary> matchSummaries, OnMatchClickListener listener) {
        this.matchSummaries = matchSummaries;
        this.listener = listener;
    }
    public void updateData(List<MatchSummary> newData) {
        this.matchSummaries = newData;
        notifyDataSetChanged(); // 通知适配器数据已变更
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    // MatchAdapter.java
    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        MatchSummary matchSummary = matchSummaries.get(position);

        // 标题格式："Team1 vs Team2 (Game)"
        String title = String.format("%s vs %s (%s)",
                matchSummary.getBlueTeamFullName(),
                matchSummary.getRedTeamFullName(),
                matchSummary.getGame());

        // 日期显示："开始时间: 时间戳"
        String dateText = "开始时间: " + matchSummary.getStartTime();

        holder.matchName.setText(title);
        holder.matchDate.setText(dateText); // 更新为显示 start_time

        holder.itemView.setOnClickListener(v -> listener.onMatchClick(matchSummary.getMatchId()));

    }

    @Override
    public int getItemCount() {
        return matchSummaries.size();
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView matchName;
        TextView matchDate;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            matchName = itemView.findViewById(R.id.matchName);
            matchDate = itemView.findViewById(R.id.matchDate);
        }
    }
}