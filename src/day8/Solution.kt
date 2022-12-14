package day8

import Coords
import asCoordsSequence
import readInput
import takeWhileInclusive
import toIntMatrix

fun main() {

    fun part1(input: List<String>): Int {
        val forest = input.toIntMatrix()
        return forest
            .asCoordsSequence()
            .count(forest::isVisible)
    }

    fun part2(input: List<String>): Int {
        val forest = input.toIntMatrix()
        return forest
            .asCoordsSequence()
            .maxOf(forest::getScore)
    }

    //val input = readInput("test")
    val input = readInput("prod")
    println(part1(input))
    println(part2(input))
}

private fun List<List<Int>>.isVisible(coords: Coords): Boolean {
    val (r, c) = coords
    if (r == 0 || c == 0 || r == lastIndex || c == this[0].lastIndex) {
        return true
    }
    val current = this[r][c]
    val openFromRight = (r + 1..lastIndex).all { this[it][c] < current }
    val openFromLeft = (r - 1 downTo 0).all { this[it][c] < current }
    val openFromBottom = (c + 1..this[0].lastIndex).all { this[r][it] < current }
    val openFromUp = (c - 1 downTo 0).all { this[r][it] < current }
    return openFromRight || openFromLeft || openFromBottom || openFromUp
}

private fun List<List<Int>>.getScore(coords: Coords): Int {
    val (r, c) = coords
    val current = this[r][c]
    val openFromBottom = (r + 1..lastIndex).takeWhileInclusive { this[it][c] < current }.size
    val openFromUp = (r - 1 downTo 0).takeWhileInclusive { this[it][c] < current }.size
    val openFromRight = (c + 1..this[0].lastIndex).takeWhileInclusive { this[r][it] < current }.size
    val openFromLeft = (c - 1 downTo 0).takeWhileInclusive { this[r][it] < current }.size
    return openFromBottom * openFromUp * openFromRight * openFromLeft
}
