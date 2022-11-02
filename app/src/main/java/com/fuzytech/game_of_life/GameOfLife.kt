package com.fuzytech.game_of_life

class GameOfLife(val size: Int) {

    var cells: HashSet<Pair<Int, Int>> = HashSet()

    fun cellCount() = size * size

    fun isAlive(x: Int, y: Int): Boolean {
        return  cells.contains(Pair(x, y))
    }

    fun click(coord: Pair<Int, Int>){
        if (cells.contains(coord)){
            cells.remove(coord)
        }else{
            cells.add(coord)
        }
    }

    fun update(onDone: ()->Unit){
        click(Pair((Math.random() * size).toInt(), (Math.random() * size).toInt()))
        onDone()
    }

}