package com.example.lolworldchampion;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class MatchListActivity extends AppCompatActivity implements MatchAdapter.OnMatchClickListener {
    private static final String TAG = "MatchListActivity";
    private static final String WIKI_BASE_URL = "https://lol.fandom.com/wiki/V5_data:";
    private static final String URL_SUFFIX = "/Timeline?action=edit";
    private static final int MAX_RETRIES = 3;
    private static final int REQUEST_DELAY_MS = 1500;
    private final Gson gson = new Gson();
    private List<MatchSummary> matchSummaries;
    private MatchAdapter adapter;
    private ExecutorService executorService;
    private Map<String, Map<Integer, String>> participantMap;
    private ProgressBar progressBar;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private OkHttpClient httpClient;
    private String filterYear;
    private String filterLeague;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);

        // Get filter parameters from intent
        Intent intent = getIntent();
        filterYear = intent.getStringExtra("year");
        filterLeague = intent.getStringExtra("league");

        initViews();
        initHttpClient();
        checkNetworkAndLoadData();
    }


    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        RecyclerView recyclerView = findViewById(R.id.matchRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MatchAdapter(new ArrayList<>(), this); // 初始化为空列表
        recyclerView.setAdapter(adapter);
    }

    private void initHttpClient() {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();
    }

    private void checkNetworkAndLoadData() {
        if (!isNetworkAvailable()) {
            showToast("请检查网络连接");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        executorService = Executors.newFixedThreadPool(4);
        loadInitialData();
    }

    // 移除fetchTimelineData()方法调用，只加载基本信息
    private void loadInitialData() {
        executorService.execute(() -> {
            try {
                // 只需要加载参与者数据和比赛基本信息
                participantMap = ParticipantCSVParser.parseParticipantCSV(this);

                try (InputStream inputStream = getResources().openRawResource(R.raw.match_info)) {
                    List<MatchSummary> summaries = CSVParser.parseMatchSummaries(inputStream);
                    List<MatchSummary> filteredSummaries = filterMatches(summaries);

                    runOnUiThread(() -> {
                        if (filteredSummaries.isEmpty()) {
                            showToast("没有找到匹配的比赛");
                            progressBar.setVisibility(View.GONE);
                            return;
                        }

                        matchSummaries = filteredSummaries;
                        adapter.updateData(matchSummaries);
                        progressBar.setVisibility(View.GONE); // 隐藏进度条
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "数据加载失败", e);
                showToast("数据加载失败: " + e.getMessage());
            }
        });
    }
    private List<MatchSummary> filterMatches(List<MatchSummary> allMatches) {
        List<MatchSummary> filtered = new ArrayList<>();
        Log.d(TAG, "Applying filters - Year: " + filterYear + ", League: " + filterLeague);

        for (MatchSummary match : allMatches) {
            boolean yearMatch = filterYear == null ||
                    (match.getYear() != null && match.getYear().contains(filterYear));
            boolean leagueMatch = filterLeague == null ||
                    (match.getLeague() != null && match.getLeague().equalsIgnoreCase(filterLeague));

            Log.d(TAG, "Match ID: " + match.getMatchId() +
                    " | Year: " + match.getYear() +
                    " | League: " + match.getLeague() +
                    " | Passes: " + (yearMatch && leagueMatch));

            if (yearMatch && leagueMatch) {
                filtered.add(match);
            }
        }

        Log.d(TAG, "Filtered matches count: " + filtered.size());
        return filtered;
    }
    private void fetchTimelineData() {
        for (int i = 0; i < matchSummaries.size(); i++) {
            final int position = i;
            mainHandler.postDelayed(() ->
                            fetchMatchWithRetry(matchSummaries.get(position), position, MAX_RETRIES),
                    i * REQUEST_DELAY_MS
            );
        }
    }

    private void fetchMatchWithRetry(MatchSummary summary, int position, int retriesLeft) {
        String url = WIKI_BASE_URL + summary.getMatchId() + URL_SUFFIX;
        Log.d(TAG, "请求URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0")
                .header("Accept", "text/html")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "请求失败: " + summary.getMatchId(), e);
                if (retriesLeft > 0) {
                    mainHandler.postDelayed(() ->
                                    fetchMatchWithRetry(summary, position, retriesLeft - 1),
                            2000
                    );
                } else {
                    showToast(summary.getMatchId() + " 数据加载失败");
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "HTTP错误: " + response.code() + " - " + summary.getMatchId());
                    if (retriesLeft > 0) {
                        mainHandler.postDelayed(() ->
                                        fetchMatchWithRetry(summary, position, retriesLeft - 1),
                                2000
                        );
                    }
                    return;
                }

                try {
                    String html = response.body().string();
                    processResponse(html, summary, position);
                } catch (Exception e) {
                    Log.e(TAG, "数据处理错误: " + summary.getMatchId(), e);
                }
            }
        });
    }

    private void processResponse(String html, MatchSummary summary, int position) {
        try {
            String jsonData = extractJsonFromHtml(html);
            if (jsonData == null) {
                Log.e(TAG, "未找到JSON数据: " + summary.getMatchId());
                return;
            }

            MatchSummary detailedSummary = JsonParser.parseMatchJson(
                    jsonData, participantMap, summary.getMatchId());

            // 保留基础信息
            detailedSummary.setMatchId(summary.getMatchId());
            detailedSummary.setGame(summary.getGame());
            detailedSummary.setStartTime(summary.getStartTime());
            detailedSummary.setBlueTeamFullName(summary.getBlueTeamFullName());
            detailedSummary.setRedTeamFullName(summary.getRedTeamFullName());
            detailedSummary.setWinningTeam(summary.getWinningTeam());

            updateUI(position, detailedSummary);
        } catch (Exception e) {
            Log.e(TAG, "JSON解析错误: " + summary.getMatchId(), e);
        }
    }

    private String extractJsonFromHtml(String html) {
        int start = html.indexOf("<textarea");
        if (start == -1) return null;

        start = html.indexOf(">", start);
        if (start == -1) return null;
        start++;

        int end = html.indexOf("</textarea>", start);
        if (end == -1) return null;

        return html.substring(start, end).trim();
    }

    private void updateUI(int position, MatchSummary summary) {
        runOnUiThread(() -> {
            matchSummaries.set(position, summary);
            adapter.notifyItemChanged(position);

            // 检查是否全部加载完成
            if (position == matchSummaries.size() - 1) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void showToast(String message) {
        mainHandler.post(() -> Toast.makeText(
                MatchListActivity.this,
                message,
                Toast.LENGTH_LONG
        ).show());
    }

    @Override
    public void onMatchClick(String matchId) {
        Toast.makeText(this, "点击了比赛: " + matchId, Toast.LENGTH_SHORT).show();
        Log.d("MatchListActivity", "点击比赛: " + matchId);

        // 查找对应的MatchSummary
        for (MatchSummary summary : matchSummaries) {
            if (summary.getMatchId().equals(matchId)) {
                Intent intent = new Intent(this, ChoiceActivity.class);
                intent.putExtra("match_summary", summary);
                startActivity(intent);
                return;
            }
        }
        Toast.makeText(this, "未找到比赛数据", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdownNow();
        }
        mainHandler.removeCallbacksAndMessages(null);
    }
}