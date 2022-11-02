package com.fuzytech.game_of_life

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.fuzytech.game_of_life.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.textboxthing.setOnClickListener{
            startActivity(Intent(
                this,
                GameOfLifeActivity::class.java
            ))
        }

        setContentView(binding.root)
    }
}