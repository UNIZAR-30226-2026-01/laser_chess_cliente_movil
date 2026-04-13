package com.gracehopper.laserchessapp.gameLogic.laser

import com.gracehopper.laserchessapp.gameLogic.move.CoordsConverter

/**
 * Objeto encargado de procesar la trayectoria del láser recibida del servidor.
 *
 * Convierte una representación en formato string
 * en una lista de coordenadas del tablero para su renderizado.
 */
object LaserUtils {

    /**
     * Parsea la trayectoria del láser desde el formato del servidor.
     *
     * @param path Trayectoria del láser en formato string
     * @return Lista de coordenadas del tablero que recorre el láser
     */
    fun parseLaserPath(path: String?): List<Pair<Int, Int>> {
        if (path.isNullOrEmpty()) return emptyList()

        val corners = path.split(",").map {
            CoordsConverter.notationToPosition(it)
        }

        return buildLaserPath(corners)
    }

    /**
     * Construye el camino completo del láser a partir de sus puntos clave.
     *
     * El láser se define por esquinas,
     * por lo que hay que interpolar todos los puntos intermedios.
     *
     * @param corners Lista de puntos clave del láser
     * @return Lista completa de posiciones por donde pasa el láser
     */
    private fun buildLaserPath(corners: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()

        for (i in 0 until corners.size - 1) {
            var (r, c) = corners[i]
            val (r2, c2) = corners[i + 1]

            // Añadir punto inicial del segmento
            result.add(Pair(r, c))

            /**
             * Avanza paso a paso hasta el siguiente punto:
             * - Movimiento en línea recta
             */
            while (r != r2 || c != c2) {
                if (r < r2) r++
                else if (r > r2) r--

                if (c < c2) c++
                else if (c > c2) c--

                result.add(Pair(r, c))
            }
        }

        /**
         * Filtrado final:
         * - Se eliminan posiciones fuera del tablero
         * - Se eliminan duplicados
         */
        return result
            .filter { (r, c) -> r in 0..9 && c in 0..7 }
            .distinct()
    }

}
