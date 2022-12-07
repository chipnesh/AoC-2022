package day2

import day2.Result.DRAW
import day2.Result.LOOSE
import day2.Result.WIN
import readInput

fun main() {

    fun part1(input: List<String>): Int {
        return input.sumOf { round ->
            val (f, s) = round.split(" ")
            Figure.of(s).fightClassic(Figure.of(f)).score
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { round ->
            val (f, s) = round.split(" ")
            Figure.of(s).fightElfStrategy(Figure.of(f)).score
        }
    }

    //val input = readInput("test")
    val input = readInput("prod")
    println(part1(input))
    println(part2(input))
}

sealed class Result(val score: Int) {
    class WIN(score: Int) : Result(score + 6)
    class DRAW(score: Int) : Result(score + 3)
    class LOOSE(score: Int) : Result(score)
}

enum class Figure(private val score: Int) {
    ROCK(1) {
        override val beats by lazy { SCISSOR }
        override val beatenBy by lazy { PAPER }
    },
    PAPER(2) {
        override val beats by lazy { ROCK }
        override val beatenBy by lazy { SCISSOR }
    },
    SCISSOR(3) {
        override val beats by lazy { PAPER }
        override val beatenBy by lazy { ROCK }
    };

    abstract val beats: Figure
    abstract val beatenBy: Figure

    fun fightClassic(other: Figure): Result {
        return when (this) {
            other.beatenBy -> WIN(score)
            other.beats -> LOOSE(score)
            else -> DRAW(score)
        }
    }

    fun fightElfStrategy(other: Figure): Result {
        return when (this) {
            ROCK -> LOOSE(other.beats.score)
            PAPER -> DRAW(other.score)
            SCISSOR -> WIN(other.beatenBy.score)
        }
    }

    companion object {
        fun of(s: String): Figure {
            return when (s) {
                "A", "X" -> ROCK
                "B", "Y" -> PAPER
                "C", "Z" -> SCISSOR
                else -> error("oops")
            }
        }
    }
}