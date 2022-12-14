package day4

import readInput

fun main() {

    fun part1(input: List<String>): Int {
        return input.count {
            val (first, second) = RangePair(it)
            first in second || second in first
        }
    }

    fun part2(input: List<String>): Int {
        return input.count {
            val (first, second) = RangePair(it)
            first intersect second || second intersect first
        }
    }

    //val input = readInput("test")
    val input = readInput("prod")
    println(part1(input))
    println(part2(input))
}

@JvmInline
value class RangePair(private val value: String) {
    operator fun component1() = Range(value.substringBefore(","))
    operator fun component2() = Range(value.substringAfter(","))
}

@JvmInline
value class Range(private val value: String) {

    val start get() = value.substringBefore("-").toInt()
    val end get() = value.substringAfter("-").toInt()

    operator fun contains(value: Int) =
        value in start..end

    operator fun contains(other: Range) =
        start in other && end in other

    infix fun intersect(other: Range) =
        start in other || end in other
}
