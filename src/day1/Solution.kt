package day1

import readInput
import splitBy

fun main() {

    fun part1(input: List<String>): Int {
        return input
            .splitBy(String::isBlank)
            .maxOfOrNull { it.sumOf(String::toInt) } ?: -1
    }

    fun part2(input: List<String>): Int {
        return input
            .splitBy(String::isBlank)
            .map { it.sumOf(String::toInt) }
            .sortedDescending()
            .take(3)
            .sum()
    }

    //val input = readInput("test")
    val input = readInput("prod")
    println(part1(input))
    println(part2(input))
}
