package com.fuzytech.game_of_life

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView

class GameOfLifeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_of_life)
    }

    class GameOfLifeViewHolder(val item: FrameLayout): RecyclerView.ViewHolder(item) {}

    class GameOfLifeAdapter(val context: Context, val converter: (Int) -> Pair<Int, Int>, val game: GameOfLife): RecyclerView.Adapter<GameOfLifeViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameOfLifeViewHolder {
            val layout = FrameLayout(context)
            layout.setBackgroundColor(Int.MAX_VALUE)
            return GameOfLifeViewHolder(layout)
        }

        override fun onBindViewHolder(holder: GameOfLifeViewHolder, position: Int) {
            val coords = converter(position)
            val alive = game.isAlive(coords.first, coords.second)
            holder.item.setBackgroundColor(if (alive) 0 else Int.MAX_VALUE)
        }

        override fun getItemCount() = game.cellCount()
    }
}