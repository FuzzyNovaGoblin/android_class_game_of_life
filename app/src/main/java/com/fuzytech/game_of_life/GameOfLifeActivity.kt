package com.fuzytech.game_of_life

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
    var delay = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameOfLifeBinding.inflate(layoutInflater)
        game = GameOfLife(20)
        binding.recycler.layoutManager = GridLayoutManager(this, game.size)
        adapter = GameOfLifeAdapter({i:Int, size: Int -> Pair(i%size, i/size)}, game)
        binding.recycler.adapter = adapter

        binding.pause.setOnClickListener {
            game.togglePause()
        }
        setContentView(binding.root)
    }

    override fun onResume() {
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())
            game.update { adapter.notifyItemChanged(it) }
        }.also { runnable = it }, delay.toLong())
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable!!)
    }

    class GameOfLifeAdapter(val converter: (Int, Int) -> Pair<Int, Int>, val game: GameOfLife): RecyclerView.Adapter<GameOfLifeAdapter.GameOfLifeViewHolder>() {

        class GameOfLifeViewHolder(val frame: FrameLayout): RecyclerView.ViewHolder(frame) {

            fun setColor(color: Int, duration: Long) {
                val oldColor = (frame.background as ColorDrawable?)?.color ?: Color.GRAY
                val animator = ValueAnimator.ofObject(ArgbEvaluator(), oldColor, color)
                animator.duration = duration
                animator.addUpdateListener {
                    frame.setBackgroundColor(it.animatedValue as Int? ?: Color.GRAY)
                }
                animator.start()
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameOfLifeViewHolder {
            val frame = FrameLayout(parent.context)
            frame.minimumHeight = parent.width/game.size
            frame.minimumWidth = parent.width/game.size

            return GameOfLifeViewHolder(frame)
        }

        override fun onBindViewHolder(holder: GameOfLifeViewHolder, position: Int) {
            val coords = converter(position, game.size)
            val alive = game.isAlive(coords.first, coords.second)
            holder.setColor(if (alive) Color.GREEN else Color.GRAY, 250)
            holder.frame.setOnClickListener{
                game.click(coords)
                notifyItemChanged(position)
            }
        }

        override fun getItemCount() = game.cellCount()
    }

}