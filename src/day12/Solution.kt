package day12

import Coords
import findAll
import findFirst
import get
import readInput
import toCharMatrix

fun main() {
    fun part1(input: List<String>): Int {
        return HeightMap.ofInput(input).minStepsFromStart()
    }

    fun part2(input: List<String>): Int {
        return HeightMap.ofInput(input).minStepsFromAnyA()
    }

    //val input = readInput("test")
    val input = readInput("prod")
    println(part1(input))
    println(part2(input))
}

data class Step(val coords: Coords, val distance: Int = 0) {
    fun moveTo(coords: Coords): Step {
        return Step(coords, distance + 1)
    }
}

data class HeightMap(
    val map: List<List<Char>>
) {
    val start = setOf(map.findFirst('S'))
    val anyA = map.findAll('a').toSet()
    val end = map.findFirst('E')

    private fun distanceTo(destination: Set<Coords>): Int {
        val steps = ArrayDeque(listOf(Step(end)))
        val visited = mutableSetOf(end)
        while (steps.isNotEmpty()) {
            val current = steps.removeFirst()
            val coords = current.coords
            if (coords in destination) return current.distance
            coords.getNeighbours()
                .filterNot(visited::contains)
                .filter { coords.canClimbTo(it) }
                .onEach(visited::add)
                .map(current::moveTo)
                .forEach(steps::add)
        }
        error("oops")
    }

    private fun Coords.getNeighbours() = buildList {
        if (x + 1 <= map.lastIndex) add(Coords(x + 1, y))
        if (y + 1 <= map[0].lastIndex) add(Coords(x, y + 1))
        if (x - 1 >= 0) add(Coords(x - 1, y))
        if (y - 1 >= 0) add(Coords(x, y - 1))
    }

    fun minStepsFromStart() = distanceTo(start)
    fun minStepsFromAnyA() = distanceTo(anyA)

    private fun Coords.canClimbTo(other: Coords): Boolean {
        val thisHeight = getHeight(this)
        val otherHeight = getHeight(other)
        return otherHeight >= thisHeight - 1
    }

    private fun getHeight(coords: Coords) =
        when (val char = map[coords]) {
            'S' -> 'a'
            'E' -> 'z'
            else -> char
        }.code

    companion object {
        fun ofInput(input: List<String>): HeightMap {
            return HeightMap(input.toCharMatrix())
        }
    }
}
