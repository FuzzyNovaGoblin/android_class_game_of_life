package com.fuzytech.game_of_life

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
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
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.Base64

class GameOfLifeActivity : AppCompatActivity() {
    lateinit var binding: ActivityGameOfLifeBinding
    lateinit var game: GameOfLife
    lateinit var adapter: GameOfLifeAdapter
    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 500
    var size = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameOfLifeBinding.inflate(layoutInflater)
        game = intent.extras?.getSerializable("board") as GameOfLife? ?: GameOfLife(size)
        setBoard(game)
    }

    private fun setBoard(board: GameOfLife) {
        game = board
        binding.recycler.layoutManager = GridLayoutManager(this, game.size)
        adapter = GameOfLifeAdapter({i:Int, size: Int -> Pair(i%size, i/size)}, game)
        binding.recycler.adapter = adapter

        binding.pause.setOnClickListener {
            game.togglePause()
        }
        binding.reset.setOnClickListener {
            game.cells.clear()
            binding.recycler.adapter = adapter
            game.pause = true
        }
        binding.clone.setOnClickListener {
            game.pause = true
            val intent = Intent(this@GameOfLifeActivity, GameOfLifeActivity::class.java)
            intent.putExtra("board", game)
            startActivity(intent)
        }
        binding.save.setOnClickListener {
            DialogUtil.showDialog(this, "Filename") {
                if (it.isEmpty()) {
                    DialogUtil.showAlert(this, "Filename cannot be empty")
                    return@showDialog
                }
                Files.write(dataDir.toPath().resolve(it), game.toString().toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
            }
        }
        binding.load.setOnClickListener {
            DialogUtil.showDialog(this, "Filename") {
                if (it.isEmpty() || !dataDir.resolve(it).exists()) {
                    DialogUtil.showAlert(this, "Invalid file")
                    return@showDialog
                }
                val str = String(Files.readAllBytes(dataDir.toPath().resolve(it)))
                val game = GameOfLife(str)
                setBoard(game)
            }
        }
        setContentView(binding.root)
    }

    override fun onResume() {
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())
            game.update { adapter.notifyItemChanged(it) }
            game.cells.forEach { adapter.notifyItemChanged(game.toIndex(it)) }
        }.also { runnable = it }, delay.toLong())
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable!!)
    }

    class GameOfLifeAdapter(val converter: (Int, Int) -> Pair<Int, Int>, val game: GameOfLife): RecyclerView.Adapter<GameOfLifeAdapter.GameOfLifeViewHolder>() {

        class GameOfLifeViewHolder(val frame: FrameLayout): RecyclerView.ViewHolder(frame) {

            fun setColor(oldColor: Int, color: Int, duration: Long) {
                val animator = ValueAnimator.ofObject(ArgbEvaluator(), oldColor, color)
                animator.duration = duration
                animator.addUpdateListener {
                    frame.setBackgroundColor(it.animatedValue as Int? ?: Color.GRAY)
                }
                animator.start()
            }

            fun setColor(color: Int, duration: Long) {
                val oldColor = (frame.background as ColorDrawable?)?.color ?: Color.GRAY
                setColor(oldColor, color, duration)
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
            val duration = 250L
            if (alive) {
                holder.setColor(Color.GREEN, Color.parseColor("#90EE90"), duration)
            } else {
                holder.setColor(Color.GRAY, duration)
            }
            holder.frame.setOnClickListener{
                game.click(coords)
                notifyItemChanged(position)
            }
        }

        override fun getItemCount() = game.cellCount()
    }

}