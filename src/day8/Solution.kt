package day8

import readInput

fun main() {

    fun part1(input: List<String>): Int {
        val forest = input.toMatrix()
        return forest.asIndexesSequence()
            .count(forest::isVisible)
    }

    fun part2(input: List<String>): Int {
        val forest = input.toMatrix()
        return forest
            .asIndexesSequence()
            .maxOf(forest::getScore)
    }

    //val input = readInput("test")
    val input = readInput("prod")
    println(part1(input))
    println(part2(input))
}

private fun List<List<Int>>.isVisible(coords: Pair<Int, Int>): Boolean {
    val (r, c) = coords
    if (r == 0 || c == 0 || r == lastIndex || c == this[0].lastIndex) {
        return true
    }
    val current = this[r][c]
    val openFromRight = (r + 1..lastIndex).all { this[it][c] < current }
    val openFromLeft = (r - 1 downTo 0).all { this[it][c] < current }
    val openFromBottom = (c + 1..this[0].lastIndex).all { this[r][it] < current }
    val openFromUp = (c - 1 downTo 0).all { this[r][it] < current }
    if (openFromRight || openFromLeft || openFromBottom || openFromUp) {
        return true
    }
    return false
}

private fun List<List<Int>>.getScore(coords: Pair<Int, Int>): Int {
    val (r, c) = coords
    val current = this[r][c]
    val openFromBottom = (r + 1..lastIndex).takeWhileInclusive { this[it][c] < current }.size
    val openFromUp = (r - 1 downTo 0).takeWhileInclusive { this[it][c] < current }.size
    val openFromRight = (c + 1..this[0].lastIndex).takeWhileInclusive { this[r][it] < current }.size
    val openFromLeft = (c - 1 downTo 0).takeWhileInclusive { this[r][it] < current }.size
    return openFromBottom * openFromUp * openFromRight * openFromLeft
}

inline fun <T> Iterable<T>.takeWhileInclusive(predicate: (T) -> Boolean): List<T> {
    val list = ArrayList<T>()
    for (item in this) {
        if (!predicate(item)) {
            list.add(item)
            break
        }
        list.add(item)
    }
    return list
}

private fun List<String>.toMatrix(): List<List<Int>> {
    return map { it.map { it.digitToInt() } }
}

private fun <T> List<List<T>>.asIndexesSequence() = sequence {
    for (i in this@asIndexesSequence.indices) {
        for (j in this@asIndexesSequence[0].indices) {
            yield(i to j)
        }
    }
}
