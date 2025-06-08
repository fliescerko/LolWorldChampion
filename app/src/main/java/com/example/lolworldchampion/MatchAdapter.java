package com.example.lolworldchampion;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    // MatchAdapter.java
    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        MatchSummary matchSummary = matchSummaries.get(position);

        String title = String.format("%s vs %s (%s)",
                matchSummary.getBlueTeamFullName(),
                matchSummary.getRedTeamFullName(),
                matchSummary.getGame());

        String dateText = "开始时间: " + matchSummary.getStartTime();

        holder.matchName.setText(title);
        holder.matchDate.setText(dateText);

        holder.itemView.setOnClickListener(v -> {
            Log.d("MatchAdapter", "点击比赛: " + matchSummary.getMatchId());

            // 只传递基本信息和matchId，不传递时间线数据
            Intent intent = new Intent(v.getContext(), ChoiceActivity.class);
            intent.putExtra("match_id", matchSummary.getMatchId());
            intent.putExtra("blue_team", matchSummary.getBlueTeamFullName());
            intent.putExtra("red_team", matchSummary.getRedTeamFullName());
            intent.putExtra("game", matchSummary.getGame());
            intent.putExtra("start_time", matchSummary.getStartTime());
            v.getContext().startActivity(intent);
        });
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