package day6

import readInput

fun main() {

    fun part1(input: List<String>): Int {
        return input
            .first()
            .windowed(4)
            .withIndex()
            .firstNotNullOf { (i, data) -> data.toSet().size.takeIf { it == 4 }?.plus(i) }
    }

    fun part2(input: List<String>): Int {
        return input
            .first()
            .windowed(14)
            .takeWhile { it.toSet().size != 14 }
            .count() + 14
    }

    //val input = readInput("test")
    val input = readInput("prod")
    println(part1(input))
    println(part2(input))
}
