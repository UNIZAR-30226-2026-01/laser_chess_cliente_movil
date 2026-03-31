package com.gracehopper.laserchessapp.gameLogic.laser

import com.gracehopper.laserchessapp.gameLogic.move.CoordsConverter

object LaserUtils {

    fun parseLaserPath(path: String?): List<Pair<Int, Int>> {
        if (path.isNullOrEmpty()) return emptyList()

        val corners = path.split(",").map {
            CoordsConverter.notationToPosition(it)
        }

        return buildLaserPath(corners)
    }

    private fun buildLaserPath(corners: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()

        for (i in 0 until corners.size - 1) {
            var (r, c) = corners[i]
            val (r2, c2) = corners[i + 1]

            result.add(Pair(r, c))

            while (r != r2 || c != c2) {
                if (r < r2) r++
                else if (r > r2) r--

                if (c < c2) c++
                else if (c > c2) c--

                result.add(Pair(r, c))
            }
        }

        return result
            .filter { (r, c) -> r in 0..9 && c in 0..7 }
            .distinct()
    }

}
