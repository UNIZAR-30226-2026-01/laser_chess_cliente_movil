package com.gracehopper.laserchessapp.data.model.game

/**
 * Configuraciones CSV de los tableros disponibles.
 * Formato: cada celda es Tipo-Equipo-Orientación.
 *   Tipo: L=Laser, K=King, S=Switcher, D=Deflector, E=Defender
 *   Equipo: A=Azul, R=Rojo
 *   Orientación: U=Arriba, R=Derecha, D=Abajo, L=Izquierda
 */
object BoardLayouts {

    const val ACE = """LAD,,,,EAD,KA,EAD,DAL,,
                    ,,DAU,,,,,,,
                    ,,,DRR,,,,,,
                    DAD,,DRU,,SAD,SAL,,DAL,,DRR
                    DAL,,DRR,,SRR,SRU,,DAD,,DRU
                    ,,,,,,DAL,,,
                    ,,,,,,,DRD,,
                    ,,DRR,ERU,KR,ERU,,,,LRU"""

    const val CURIOSITY = """LAD,,,,EAD,KA,EAD,SAL,,
                            ,,,,,,,,,
                            ,,,DRR,,,DAD,,,
                            DAD,DRU,,,DRL,SAL,,,DAL,DRR
                            DAL,DRR,,,SRR,DAR,,,DAD,DRU
                            ,,,DRU,,,DAL,,,
                            ,,,,,,,,,
                            ,,SRR,ERU,KR,ERU,,,,LRU"""

    const val GRAIL = """LAD,,,,DAU,EAD,DAL,,,
                        ,,,,,KA,,,,
                        DAD,,,,DAU,EAD,SAL,,,
                        DAL,,SAD,,DRR,,DRL,,,
                        ,,,DAR,,DAL,,SRD,,DRR
                        ,,,SRL,ERU,DRD,,,,DRU
                        ,,,,KR,,,,,
                        ,,,DRR,ERU,DRD,,,,LRU"""

    const val SOPHIE = """LAD,,,,KA,DRR,DAL,,,
                        ,,,EAD,,EAR,,,,DRU
                        DAD,,,,DAU,DAL,,SRL,,DRR
                        ,,,,,,,SAD,,
                        ,,SRD,,,,,,,
                        DAL,,SAL,,DRR,DRD,,,,DRU
                        DAD,,,,ERL,,ERU,,,
                        ,,,DRR,DAL,KR,,,,LRU"""

    const val MERCURY = """LAR,,,,DAU,KA,DAL,,,SRL
                        ,,,,,EAD,DAL,,,
                        DAL,,,SAL,,EAD,,,,
                        DAD,,,,DRR,,,,DRU,
                        ,DAD,,,,DAL,,,,DRU
                        ,,,,ERU,,SRL,,,DRR
                        ,,,DRR,ERU,,,,,
                        SAL,,,DRR,KR,DRD,,,,LRL"""

    /**
     * Devuelve el CSV del tablero por su nombre.
     * El nombre debe coincidir con los de BoardType.
     */
    fun getCsvForBoard(boardName: String): String {
        return when (boardName.uppercase()) {
            "ACE"      -> ACE
            "CURIOSITY" -> CURIOSITY
            "GRAIL"    -> GRAIL
            "SOPHIE"   -> SOPHIE
            "MERCURY"  -> MERCURY
            else       -> ACE
        }
    }

    /**
     * Lista de nombres en el mismo orden que game_constants.go (0-based):
     * 0=ACE, 1=CURIOSITY, 2=GRAIL, 3=MERCURY, 4=SOPHIE
     * (Fuente real: constantes Board_T del engine de Go, no boards.go)
     */
    val ALL_BOARD_NAMES = listOf("Ace", "Curiosity", "Grail", "Mercury", "Sophie")
}