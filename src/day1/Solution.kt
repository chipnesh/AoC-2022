package day1

import readInput
import sumBySplitting

fun main() {

    fun part1(input: List<String>): Int {
        val caloriesPerElf = input.sumBySplitting(String::isBlank)
        return caloriesPerElf.maxOrNull() ?: -1
    }

    fun part2(input: List<String>): Int {
        val caloriesSums = input.sumBySplitting(String::isBlank)
        return caloriesSums.sortedDescending().take(3).sum()
    }

    //val input = readInput("test")
    val input = readInput("prod")
    println(part1(input))
    println(part2(input))
}
