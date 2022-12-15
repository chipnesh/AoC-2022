package day14

import Coords
import above
import below
import day14.PointType.*
import get
import leftDown
import readInput
import rightDown
import splitEach
import toPair

fun main() {

    fun part1(input: List<String>): Int {
        return PathScanner.of(input)
            .drawMap()
            .drawSand(Mode.ABYSS)
            .sandCapacity()

    }

    fun part2(input: List<String>): Int {
        return PathScanner.of(input)
            .drawMap()
            .drawSand(Mode.FLOOR)
            .sandCapacity()
    }

    //val input = readInput("test")
    val input = readInput("prod")
    println(part1(input))
    println(part2(input))
}

fun String.toCoords(): List<Coords> {
    return split(" -> ")
        .splitEach(",")
        .map { it.toPair(String::toInt) }
        .map(::Coords)
}

enum class PointType {
    ROCK, AIR, SOURCE, SAND, ABYSS
}

data class Tile(val coords: Coords, var type: PointType) {
    fun isBlocked() = isRock() || isSand()
    fun isRock() = type == ROCK
    fun isSand() = type == SAND

    fun print() = when (type) {
        ROCK -> "#"
        AIR -> "."
        SOURCE -> "+"
        SAND -> "o"
        ABYSS -> "x"
    }
}

enum class Mode {
    ABYSS, FLOOR
}

class PathScanner(
    private val linesCoords: List<List<Coords>> = mutableListOf()
) {
    private val map: MutableList<MutableList<Tile>> = mutableListOf()
    private val sourceCoords = Coords(500, 0)
    private val minX = linesCoords.minOf { it.minOf { it.x } }
    private val maxX = linesCoords.maxOf { it.maxOf { it.x } }
    private val maxY = linesCoords.maxOf { it.maxOf { it.y } }
    private var sands: Int = 0
    private var floorSand = mutableSetOf<Coords>()
    private var finished = false

    fun drawMap(): PathScanner {
        drawAir()
        drawSource()
        drawRock()
        return this
    }

    private fun drawAir() {
        for (i in 0..maxX - minX) {
            val yList = mutableListOf<Tile>()
            map.add(yList)
            for (j in 0..maxY) {
                yList.add(Tile(Coords(i, j), AIR))
            }
        }
    }

    private fun drawSource() {
        map[sourceCoords.adjust().x][sourceCoords.y] = Tile(sourceCoords.adjust(), SOURCE)
    }

    private fun drawRock() {
        linesCoords.forEach { lineCoords ->
            lineCoords
                .map { it.adjust() }
                .zipWithNext { (x, y), (b, c) ->
                    if (x == b) {
                        if (y > c) {
                            (y downTo c).forEach { i -> map[x][i] = Tile(Coords(x, i), ROCK) }
                        } else {
                            (y..c).forEach { i -> map[x][i] = Tile(Coords(x, i), ROCK) }
                        }
                    } else if (y == c) {
                        if (x > b) {
                            (x downTo b).forEach { i -> map[i][y] = Tile(Coords(i, y), ROCK) }
                        } else {
                            (x..b).forEach { i -> map[i][y] = Tile(Coords(i, y), ROCK) }
                        }
                    }
                }
        }
    }

    private fun Coords.adjust(): Coords {
        return copy(x = x - minX)
    }

    fun drawSand(mode: Mode): PathScanner {
        val coords = sourceCoords.adjust()
        while (!finished) {
            stepTo(coords, mode)
        }
        return this
    }

    private fun stepTo(coords: Coords, mode: Mode) {
        var current = coords
        while (!finished) {
            val belowTile = getTile(current.below(), mode)
            when (belowTile.type) {
                ROCK, SAND -> {
                    val diagonalLeft = getTile(current.leftDown(), mode)
                    val diagonalRight = getTile(current.rightDown(), mode)
                    if (diagonalLeft.isSand() && diagonalRight.isSand()) {
                        if (sourceCoords.adjust() == belowTile.coords.above()) {
                            finished = true
                        }
                    }
                    current = when {
                        !diagonalLeft.isBlocked() -> diagonalLeft.coords
                        !diagonalRight.isBlocked() -> diagonalRight.coords
                        else -> {
                            setSand(current, mode)
                            sands++
                            break
                        }
                    }
                }

                AIR, SOURCE -> current = current.below()
                ABYSS -> {
                    finished = true
                    break
                }
            }
        }
    }

    private fun setSand(current: Coords, mode: Mode) {
        getTile(current, mode).type = SAND
        floorSand.add(current)
    }

    private fun getTile(coords: Coords, mode: Mode) = when (mode) {
        Mode.ABYSS -> map[coords] ?: Tile(coords, ABYSS)
        Mode.FLOOR -> map[coords] ?: when {
            isFloor(coords) -> Tile(coords, ROCK)
            isSand(coords) -> Tile(coords, SAND)
            else -> Tile(coords, AIR)
        }
    }

    private fun isFloor(coords: Coords) = coords.y >= maxY + 2

    private fun isSand(coords: Coords) = floorSand.contains(coords)

    // print to see what is going on
    private fun print(): String {
        return buildString {
            for (y in 0..maxY) {
                for ((x, tiles) in map.withIndex()) {
                    append(map[x][y].print())
                }
                appendLine()
            }
        }
    }

    fun sandCapacity(): Int {
        return sands
    }

    companion object {
        fun of(input: List<String>): PathScanner {
            return PathScanner(input.map { it.toCoords() })
        }
    }
}