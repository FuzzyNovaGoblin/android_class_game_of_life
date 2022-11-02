package com.fuzytech.game_of_life

import android.util.Log

class GameOfLife(val size: Int) {

    var cells: HashSet<Pair<Int, Int>> = HashSet()

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

    fun click(coord: Pair<Int, Int>){
        if (cells.contains(coord)){
            cells.remove(coord)
        }else{
            cells.add(coord)
        }
    }

    fun update(onDone: ()->Unit){
        var toUpdate: HashSet<Pair<Int, Int>> = HashSet()
        for (p in cells.toArray()){
//            val adj = getAdjacent(p as Pair<Int, Int>)
            toUpdate.add(p as Pair<Int, Int>)
            for(n in getAdjacent(p)){
                toUpdate.add(n)
            }
        }
        for (p in toUpdate){
            if(aliveNeighbors(p) < 2){
                cells.remove(p)
            }
            else if(aliveNeighbors(p) > 3){
                cells.remove(p)
            }
            else if(aliveNeighbors(p) == 3){
                cells.add(p)
            }
        }
//        click(Pair((Math.random() * size).toInt(), (Math.random() * size).toInt()))
        onDone()
    }

}