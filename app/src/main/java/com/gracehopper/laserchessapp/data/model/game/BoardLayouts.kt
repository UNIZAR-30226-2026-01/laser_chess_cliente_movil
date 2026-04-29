package com.gracehopper.laserchessapp.data.model.game

/**
 * Configuraciones CSV de los tableros disponibles.
 */
object BoardLayouts {

    val ACE = """LAD,,,,EAD,KA,EAD,DAL,,
,,DAU,,,,,,,
,,,DRR,,,,,,
DAD,,DRU,,SAD,SAL,,DAL,,DRR
DAL,,DRR,,SRR,SRU,,DAD,,DRU
,,,,,,DAL,,,
,,,,,,,DRD,,
,,DRR,ERU,KR,ERU,,,,LRU"""

    val CURIOSITY = """LAD,,,,EAD,KA,EAD,SAL,,
,,,,,,,,,
,,,DRR,,,DAD,,,
DAD,DRU,,,DRL,SAL,,,DAL,DRR
DAL,DRR,,,SRR,DAR,,,DAD,DRU
,,,DRU,,,DAL,,,
,,,,,,,,,
,,SRR,ERU,KR,ERU,,,,LRU"""

    val GRAIL = """LAD,,,,DAU,EAD,DAL,,,
,,,,,KA,,,,
DAD,,,,DAU,EAD,SAL,,,
DAL,,SAD,,DRR,,DRL,,,
,,,DAR,,DAL,,SRD,,DRR
,,,SRL,ERU,DRD,,,,DRU
,,,,KR,,,,,
,,,DRR,ERU,DRD,,,,LRU"""

    val SOPHIE = """LAD,,,,KA,DRR,DAL,,,
,,,EAD,,EAR,,,,DRU
DAD,,,,DAU,DAL,,SRL,,DRR
,,,,,,,SAD,,
,,SRD,,,,,,,
DAL,,SAL,,DRR,DRD,,,,DRU
DAD,,,,ERL,,ERU,,,
,,,DRR,DAL,KR,,,,LRU"""

    val MERCURY = """LAR,,,,DAU,KA,DAL,,,SRL
,,,,,EAU,DAL,,,
DAL,,,SAL,,EAU,,,,
DAD,,,,DRR,,,,DRU,
,DAD,,,,DAL,,,,DRU
,,,,ERD,,SRL,,,DRR
,,,DRR,ERD,,,,,
SAL,,,DRR,KR,DRD,,,,LRL"""

    /**
     * Devuelve el CSV del tablero por su nombre.
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

    val ALL_BOARD_NAMES = listOf("Ace", "Curiosity", "Grail", "Sophie", "Mercury")
}