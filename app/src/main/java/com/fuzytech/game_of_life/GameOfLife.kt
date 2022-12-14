package com.fuzytech.game_of_life

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import java.io.Serializable
import kotlin.streams.asSequence

class GameOfLife(val size: Int): Serializable {

    constructor(string: String) : this(string.takeWhile {it != ','}.toInt()) {
        string.takeWhile {it != '\n'}.split(",").also {aliveColor = it[1].toInt(); deadColor = it[2].toInt()}
        cells = string.split("\n").drop(1).map {it.split(",")}.map {it[0].toInt() to it[1].toInt()}.toHashSet()
    }

    override fun toString() = listOf(size, aliveColor, deadColor).map {it.toString()}.joinToString(",") + "\n" + cells.map {"${it.first},${it.second}"}.joinToString("\n")

    var cells: HashSet<Pair<Int, Int>> = HashSet()
    var pause: Boolean = true
    var aliveColor = Color.GREEN
    var deadColor =  Color.GRAY


    fun secondAliveColor(): Color{
        val ogColor = Color.valueOf(aliveColor)
        return Color.valueOf(255 - ogColor.red(), 255 - ogColor.blue(), 255 - ogColor.green() )
    }

    fun cellCount() = size * size

    fun isAlive(x: Int, y: Int): Boolean {
        return  cells.contains(Pair(x, y))
    }

    fun getPoint(x:Int, y:Int):Pair<Int, Int>{
        var x = x
        var y = y

        if(x < 0){
            x += size
        }
        if(y<0){
            y += size
        }

        return Pair(x%size, y%size)
    }

    fun togglePause(){
        pause = !pause
    }

    fun getAdjacent(point:Pair<Int, Int>): List<Pair<Int, Int>>{
        return listOf(
            getPoint(point.first-1, point.second-1),
            getPoint(point.first+1, point.second-1),
            getPoint(point.first-1, point.second+1),
            getPoint(point.first+1, point.second+1),
            getPoint(point.first-1, point.second),
            getPoint(point.first+1, point.second),
            getPoint(point.first ,point.second-1),
            getPoint(point.first ,point.second+1)
        )
    }

    fun aliveNeighbors(point:Pair<Int, Int>): Int{
        val neighbors = getAdjacent(point)
        return neighbors.filter { cells.contains(it) }.size
    }

    fun toIndex(coord: Pair<Int, Int>) = coord.first + coord.second * size

    fun click(coord: Pair<Int, Int>){
        if (cells.contains(coord)){
            cells.remove(coord)
        }else{
            cells.add(coord)
        }
    }

    fun update(updateGrid: (Int)->Unit) {
        if (pause){
            return
        }
        var toUpdate: HashSet<Pair<Int, Int>> = HashSet()
        var nextGenAdd: MutableList<Pair<Int, Int>> = mutableListOf()
        var nextGenRemove: MutableList<Pair<Int, Int>> = mutableListOf()
        for (p in cells){
            nextGenAdd.add(p)
            toUpdate.add(p)
            for(n in getAdjacent(p)){
                toUpdate.add(n)
            }
        }
        for (p in toUpdate){
            if(cells.contains(p)){
                val n = aliveNeighbors(p)
                if(n < 2 || n > 3 ){
                    nextGenRemove.add(p)
                }
            }else{
                if(aliveNeighbors(p) == 3){
                    nextGenAdd.add(p)
                }
            }
        }
        for (p in nextGenAdd){
            cells.add(p)
        }
        for (p in nextGenRemove){
            cells.remove(p)
        }
        for (p in nextGenRemove){
            updateGrid(toIndex(p))
        }
        for (p in nextGenAdd){
            updateGrid(toIndex(p))
        }
    }

}