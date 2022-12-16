@file:OptIn(ExperimentalStdlibApi::class)

package day15

import Coords
import MAX
import length
import plus
import readInput
import kotlin.math.absoluteValue

fun main() {

    fun part1(input: List<String>): Long {
        val sensors = Sensors.ofInput(input)
        return sensors
            .scanRangesAt(2000000)
            .sumOf(IntRange::length)
            .minus(sensors.uniqueBeacons(2000000))
    }

    fun tuningFrequency(pair: Pair<Int, Set<IntRange>>): Long {
        val (beaconY, range) = pair
        val beaconX = range.first().endExclusive
        return beaconX * 4000000L + beaconY
    }

    fun part2(input: List<String>): Long? {
        val sensors = Sensors.ofInput(input)
        val maxRange = 0..4000000
        return maxRange
            .firstNotNullOfOrNull { y ->
                sensors.scanRangesAt(y, maxRange)
                    .let { if (it.size == 2) y to it else null }
            }?.let(::tuningFrequency)
    }

    //val input = readInput("test")
    val input = readInput("prod")
    println(part1(input))
    println(part2(input))
}

data class Sensor(
    val coords: Coords,
    val beacon: Coords
) {
    private val distance = ((beacon.x - coords.x).absoluteValue + (beacon.y - coords.y).absoluteValue)

    fun rangeAt(y: Int, maxRange: IntRange): IntRange? {
        val diff = distance - (y - coords.y).absoluteValue
        if (diff < 0) return null
        return IntRange(
            maxOf(maxRange.first, coords.x - diff),
            minOf(maxRange.last, coords.x + diff)
        )
    }

    companion object {
        fun ofInput(input: String): Sensor {
            val coordsLine = input.substringAfter("Sensor at ").substringBefore(":")
            val (xLine, yLine) = coordsLine.split(", ")
            val x = xLine.substringAfter("x=").toInt()
            val y = yLine.substringAfter("y=").toInt()
            val sensorCoords = Coords(x, y)
            val beaconLine = input.drop(coordsLine.length).substringAfter(" closest beacon is at ")
            val (bLine, cLine) = beaconLine.split(", ")
            val b = bLine.substringAfter("x=").toInt()
            val c = cLine.substringAfter("y=").toInt()
            val beaconCoords = Coords(b, c)
            return Sensor(sensorCoords, beaconCoords)
        }
    }
}

data class Sensors(
    val sensors: List<Sensor>
) {

    fun scanRangesAt(y: Int, maxRange: IntRange = IntRange.MAX) =
        sensors
            .mapNotNull { it.rangeAt(y, maxRange) }
            .sortedBy(IntRange::first)
            .fold(mutableSetOf<IntRange>()) { acc, next ->
                acc.apply {
                    val previous = lastOrNull()?.also(::remove) ?: next
                    addAll((previous + next)?.let(::setOf) ?: setOf(previous, next))
                }
            }

    fun uniqueBeacons(y: Int): Int {
        return sensors
            .mapTo(mutableSetOf(), Sensor::beacon)
            .count { it.y == y }
    }

    companion object {
        fun ofInput(input: List<String>): Sensors {
            return Sensors(input.map(Sensor::ofInput))
        }
    }
}
