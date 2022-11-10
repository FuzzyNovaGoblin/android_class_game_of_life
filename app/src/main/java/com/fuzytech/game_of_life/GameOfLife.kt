package com.fuzytech.game_of_life

import java.io.Serializable
import kotlin.streams.asSequence

class GameOfLife(val size: Int): Serializable {

    constructor(string: String) : this(string.takeWhile {it != '\n'}.toInt()) {
        cells = string.split("\n").stream().skip(1).map {it.split(",")}.map {Pair(it[0].toInt(), it[1].toInt())}.asSequence().toHashSet()
    }

    var cells: HashSet<Pair<Int, Int>> = HashSet()
    var pause: Boolean = true

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

    override fun toString() = size.toString() + "\n" + cells.map {"${it.first},${it.second}"}.joinToString("\n")

}