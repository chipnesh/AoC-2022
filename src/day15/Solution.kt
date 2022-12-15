@file:OptIn(ExperimentalStdlibApi::class)

package day15

import Coords
import MAX
import length
import plus
import readInput
import toBounds
import kotlin.math.absoluteValue

fun main() {

    fun part1Naive(input: List<String>): Int {
        return Sensors.ofInput(input)
            .coverages()
            .print()
            .map { it.coordsWithoutBeaconAt(10) }
            .reduce(Set<Coords>::union)
            .size
    }

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

private fun List<Coverage>.print(): List<Coverage> {
    val beacons = mapTo(mutableSetOf()) { it.sensor.beacon }
    val sensors = mapTo(mutableSetOf()) { it.sensor.coords }
    val coords = flatMap { it.coords }.toSet() - beacons - sensors
    val minX = coords.minOf { it.x }
    val maxX = coords.maxOf { it.x }
    val minY = coords.minOf { it.y }
    val maxY = coords.maxOf { it.y }

    var row = minX
    var col = minY
    print(" ".padStart(2))
    for (x in minX..maxX) {
        print("${row++}".padStart(3))
    }
    println()
    for (y in minY..maxY) {
        print("${col++}".padStart(3))
        for (x in minX..maxX) {
            val c = Coords(x, y)
            when (c) {
                in coords -> print(" # ")
                in sensors -> print(" S ")
                in beacons -> print(" B ")
                else -> print(" . ")
            }
        }
        println()
    }
    return this
}

data class Sensor(
    val coords: Coords,
    val beacon: Coords
) {
    private val distance = ((beacon.x - coords.x).absoluteValue + (beacon.y - coords.y).absoluteValue)

    fun coverage(): Coverage {
        val scanned = mutableSetOf<Coords>()
        val bounds = coords.toBounds(distance)
        var wide = 0
        for (x in bounds.minX until coords.x) {
            for (y in coords.y - wide..coords.y + wide) {
                scanned.add(Coords(x, y))
            }
            wide += 1
        }
        for (x in coords.x..bounds.maxX) {
            for (y in coords.y - wide..coords.y + wide) {
                scanned.add(Coords(x, y))
            }
            wide -= 1
        }
        return Coverage(this, scanned)
    }

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
            .filter { it != maxRange }
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

    fun coverages(): List<Coverage> {
        return sensors.map(Sensor::coverage)
    }

    fun getXRange() =
        sensors.sortedBy { it.coords.x }.run { first().coords.x..last().coords.x }

    fun getYRange() =
        sensors.sortedBy { it.coords.y }.run { first().coords.y..last().coords.y }

    companion object {
        fun ofInput(input: List<String>): Sensors {
            return Sensors(input.map(Sensor::ofInput))
        }
    }
}

data class Coverage(
    val sensor: Sensor,
    val coords: Set<Coords>
) {
    fun coordsWithoutBeaconAt(y: Int): Set<Coords> {
        return (coords - sensor.beacon).filterTo(mutableSetOf()) { it.y == y }
    }
}
