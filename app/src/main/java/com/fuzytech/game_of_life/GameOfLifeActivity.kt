package com.fuzytech.game_of_life

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.size
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fuzytech.game_of_life.databinding.ActivityGameOfLifeBinding

class GameOfLifeActivity : AppCompatActivity() {
    lateinit var binding: ActivityGameOfLifeBinding
    lateinit var game: GameOfLife
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameOfLifeBinding.inflate(layoutInflater)
        game = GameOfLife(7)
        binding.recycler.layoutManager = GridLayoutManager(this, game.size)
        binding.recycler.adapter = GameOfLifeAdapter({i:Int, size: Int -> Pair(i%size, i/size)}, game)

        setContentView(binding.root)
    }


    class GameOfLifeAdapter(val converter: (Int, Int) -> Pair<Int, Int>, val game: GameOfLife): RecyclerView.Adapter<GameOfLifeAdapter.GameOfLifeViewHolder>() {

        class GameOfLifeViewHolder(val frame: FrameLayout): RecyclerView.ViewHolder(frame) {}


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameOfLifeViewHolder {
            val frame = FrameLayout(parent.context)

            frame.minimumHeight = parent.width/game.size
            frame.minimumWidth = parent.width/game.size

            return GameOfLifeViewHolder(frame)
        }

        override fun onBindViewHolder(holder: GameOfLifeViewHolder, position: Int) {
            val coords = converter(position, game.size)
            val alive = game.isAlive(coords.first, coords.second)
            holder.frame.setBackgroundColor(if (alive) Color.GREEN else Color.GRAY)
            holder.frame.setOnClickListener{
                game.click(coords)
                this.notifyDataSetChanged()}
        }

        override fun getItemCount() = game.cellCount()
    }
}