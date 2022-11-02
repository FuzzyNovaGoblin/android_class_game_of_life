package com.fuzytech.game_of_life

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fuzytech.game_of_life.databinding.ActivityGameOfLifeBinding

class GameOfLifeActivity : AppCompatActivity() {
    lateinit var binding: ActivityGameOfLifeBinding
    lateinit var game: GameOfLife
    lateinit var adapter: GameOfLifeAdapter
    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameOfLifeBinding.inflate(layoutInflater)
        game = GameOfLife(7)
        binding.recycler.layoutManager = GridLayoutManager(this, game.size)
        adapter = GameOfLifeAdapter({i:Int, size: Int -> Pair(i%size, i/size)}, game)
        binding.recycler.adapter = adapter

        setContentView(binding.root)
    }

    override fun onResume() {
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())
            game.update { adapter.notifyDataSetChanged() }
        }.also { runnable = it }, delay.toLong())
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable!!)
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