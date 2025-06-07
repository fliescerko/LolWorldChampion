package com.example.lolworldchampion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;


import com.example.lolworldchampion.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 使用binding获取视图，而不是findViewById
        binding.btnMatchList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MatchListActivity.class);
                startActivity(intent);
            }
        });

        binding.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoriteActivity.start(MainActivity.this);
            }
        });

        binding.btnThirdChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 第三个通道的跳转逻辑
            }
        });

        // 底部导航设置

    }
}