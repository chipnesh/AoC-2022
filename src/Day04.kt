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

    //val input = readInput("Day04_test")
    val input = readInput("Day04")
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

    operator fun component1() = value.substringBefore("-").toInt()
    operator fun component2() = value.substringAfter("-").toInt()

    operator fun contains(value: Int) =
        value in component1()..component2()

    operator fun contains(other: Range) =
        component1() in other && component2() in other

    infix fun intersect(other: Range) =
        component1() in other || component2() in other
}
