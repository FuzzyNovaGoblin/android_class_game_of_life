package com.fuzytech.game_of_life

class GameOfLife(val size: Int) {

    fun cellCount() = size * size

    fun isAlive(x: Int, y: Int): Boolean {
        if (x == 1 && y == 1){
            return true
        }
        return false
    }

}