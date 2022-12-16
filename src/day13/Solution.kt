package day13

import readInput

sealed interface Element : Comparable<Element>

@JvmInline
value class IntElement(val value: Int) : Element {
    override fun compareTo(other: Element): Int = when (other) {
        is IntElement -> compareValues(value, other.value)
        is ListElement -> compareValues(ListElement(this), other)
    }
}

@JvmInline
value class ListElement(val value: MutableList<Element>) : Element {
    constructor(vararg elements: Element) : this(mutableListOf(*elements))

    override fun compareTo(other: Element): Int = when (other) {
        is IntElement -> compareValues(this, ListElement(other))
        is ListElement -> value.zip(other.value, Element::compareTo)
            .firstOrNull { it != 0 }
            ?: value.size.compareTo(other.value.size)
    }
}

fun toItem(string: String): ListElement = buildList<ListElement> {
    add(ListElement())
    for (s in string.split(',')) {
        var token = s
        while (token.isNotEmpty()) {
            var drop = 1
            when (token.take(1)) {
                "[" -> add(ListElement())
                "]" -> removeLast().let { last().value.add(it) }
                else -> {
                    val number = token.takeWhile(Char::isDigit)
                    drop = number.length
                    last().value.add(IntElement(number.toInt()))
                }
            }
            token = token.drop(drop)
        }
    }
}.last()

fun List<String>.toItemList() =
    filter(String::isNotBlank).map(::toItem)

fun main() {
    fun part1(input: List<String>): Int {
        return input
            .toItemList()
            .chunked(2)
            .withIndex()
            .filter { it.value.reduce(::compareValues) == -1 }
            .sumOf { it.index + 1 }
    }

    fun part2(input: List<String>): Int {
        val dividers = setOf(
            ListElement(ListElement(IntElement(2))),
            ListElement(ListElement(IntElement(6)))
        )
        return input
            .toItemList()
            .plus(dividers)
            .sorted()
            .mapIndexedNotNull { i, e -> if (e in dividers) i + 1 else null }
            .reduce(Int::times)
    }

    //val input = readInput("test")
    val input = readInput("prod")
    println(part1(input))
    println(part2(input))
}
