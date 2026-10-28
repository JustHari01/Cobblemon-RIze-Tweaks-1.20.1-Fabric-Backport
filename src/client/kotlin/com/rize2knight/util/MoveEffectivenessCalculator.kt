package com.rize2knight.util

import com.cobblemon.mod.common.api.moves.MoveTemplate
import com.cobblemon.mod.common.api.types.ElementalType

object MoveEffectivenessCalculator {
    private val typeChart: HashMap<String, HashMap<String, Float>> = HashMap()
    private val graalTypeChart: GraalTypeChart = GraalTypeChart()

    init {
        graalTypeChart.openConnection()
        graalTypeChart.getTypeChart(typeChart)
    }

    // Calculates the damage multiplier of a move against a defender with up to two types
    fun getMoveDamageMult(move: MoveTemplate, defenderType1: ElementalType?, defenderType2: ElementalType?): Float {
        val moveType = move.elementalType.name
        var damageMult = 1f

        listOfNotNull(defenderType1, defenderType2).forEach { type ->
            damageMult *= getDamageMult(moveType, type.name)
        }

        return damageMult
    }

    private fun getDamageMult(move: String, defendType: String): Float {
        val attackingType = move.lowercase()
        val defendingType = defendType.lowercase()
        return typeChart[defendingType]?.get(attackingType) ?: 1f
    }
}
