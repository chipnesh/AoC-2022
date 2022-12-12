package day9

import day9.Step.Down
import day9.Step.Left
import day9.Step.Right
import day9.Step.Up
import readInput
import kotlin.math.absoluteValue

fun main() {

    fun part1(input: List<String>): Int {
        val tail = Pos()
        return runRopes(input, tail::follow)
    }

    fun part2(input: List<String>): Int {
        val tail = List(9) { Pos() }
        fun follow(head: Pos) = tail.fold(head) { lead, follower -> follower.follow(lead) }
        return runRopes(input, ::follow)
    }

    val input = readInput("test")
    //val input = readInput("prod")
    println(part1(input))
    println(part2(input))
}


data class Pos(var x: Int = 0, var y: Int = 0) {

    fun follow(other: Pos) =
        (other.x - x to other.y - y).let { (xDist, yDist) ->
            if (maxOf(xDist.absoluteValue, yDist.absoluteValue) > 1) {
                move(xDist.coerceIn(-1..1), yDist.coerceIn(-1..1))
            } else this
        }

    fun move(step: Step) = when (step) {
        is Down -> move(y = -1)
        is Left -> move(x = -1)
        is Right -> move(x = +1)
        is Up -> move(y = +1)
    }

    private fun move(x: Int = 0, y: Int = 0): Pos {
        this.x += x
        this.y += y
        return this
    }
}

fun runRopes(input: List<String>, onHeadMove: (Pos) -> Pos): Int {
    val head = Pos()
    val visited = mutableMapOf<Pair<Int, Int>, Boolean>()
    fun visit(pos: Pos) = run { visited[pos.x to pos.y] = true }
    input.toSteps()
        .asSequence()
        .flatMap(Step::flatten)
        .map(head::move)
        .map(onHeadMove)
        .forEach(::visit)
    return visited.count()
}

sealed class Step(val times: Int) {
    fun flatten() =
        List(times) {
            when (this) {
                is Down -> Down()
                is Left -> Left()
                is Right -> Right()
                is Up -> Up()
            }
        }

    data class Up(val steps: Int = 1) : Step(steps)
    data class Down(val steps: Int = 1) : Step(steps)
    data class Right(val steps: Int = 1) : Step(steps)
    data class Left(val steps: Int = 1) : Step(steps)
}

fun List<String>.toSteps() = map { direction ->
    when (direction.first()) {
        'U' -> Up(direction.stepCount())
        'D' -> Down(direction.stepCount())
        'R' -> Right(direction.stepCount())
        'L' -> Left(direction.stepCount())
        else -> error("Oops")
    }
}

private fun String.stepCount() = substringAfter(" ").toInt()
