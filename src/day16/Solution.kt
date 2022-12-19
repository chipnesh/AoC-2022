package day16

import readInput

fun main() {
    fun part1(input: List<String>): Int {
        return Valves
            .parse(input)
            .mostPressure()
    }

    fun part2(input: List<String>): Int {
        return -1
    }

    val input = readInput("test")
    //val input = readInput("prod")
    println(part1(input))
    println(part2(input))
}

data class Valves(
    val defs: List<ValveDef>,
    val byId: Map<String, ValveDef>
) {

    val valves get() = defs.map { Valve.of(it, byId) }

    fun mostPressure(): Int {
        val start = valves.first()
        val released = mutableSetOf<String>()
        val current = start
        val leads = ArrayDeque<ValveDef>()
        return 0
    }

    companion object {
        fun parse(input: List<String>): Valves {
            val defs = input.map(ValveDef::parse)
            val byId = defs.associateBy(ValveDef::id)
            return Valves(defs, byId)
        }
    }
}

data class Valve(
    val id: String,
    val rate: Int,
    val tunnelsTo: List<String>,
    val byId: Map<String, ValveDef>
) {
    //val leads get() = tunnelsTo.map {
    //    Valve(it)
    //}

    companion object {
        fun of(def: ValveDef, byId: Map<String, ValveDef>): Valve {
            return Valve(def.id, def.rate, def.leads, byId)
        }
    }
}

data class ValveDef(
    val id: String,
    val rate: Int,
    val leads: List<String>
) {
    companion object {
        fun parse(input: String): ValveDef {
            val nameString = input.substringAfter("Valve ")
            val id = nameString.take(2)
            val rateString = nameString.drop(2).substringAfter(" has flow rate=")
            val rate = rateString.substringBefore(";")
            val leads = rateString.substring(rateString.indexOf(";")).substringAfter(" tunnels lead to valves ").split(", ")
            return ValveDef(id, rate.toInt(), leads)
        }
    }
}