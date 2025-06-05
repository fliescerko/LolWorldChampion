package com.example.lolworldchampion;

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
        void onMatchClick(String jsonFileName);
    }

    public MatchAdapter(List<MatchSummary> matchSummaries, OnMatchClickListener listener) {
        this.matchSummaries = matchSummaries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        MatchSummary matchSummary = matchSummaries.get(position);
        holder.matchName.setText(matchSummary.getBlueTeamFullName() + " vs " + matchSummary.getRedTeamFullName());
        holder.matchDate.setText(matchSummary.getMatchId());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMatchClick(matchSummary.getJsonFileName());
            }
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