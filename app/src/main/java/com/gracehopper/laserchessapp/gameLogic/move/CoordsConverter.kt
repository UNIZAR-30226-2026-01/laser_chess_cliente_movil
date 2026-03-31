package com.gracehopper.laserchessapp.gameLogic.move

import android.util.Log

object CoordsConverter {

    fun notationToPosition(pos: String): Pair<Int, Int> {
        Log.d("NOTATION", "Notation: $pos")
        val row = pos[0] - 'a'                  // a-j -> 0-9
        val col = pos[1].digitToInt()-1      // 0-7
        return Pair(row, col)
    }

    fun positionToNotation(pos: Pair<Int, Int>): String {
        val (row, col) = pos
        val rowLetter = ('a' + row)
        val colNumber = col + 1
        return "$rowLetter$colNumber"
    }

}