package day11

import readInput
import splitBy

fun main() {
    fun part1(input: List<String>): Long {
        val monkeys = Monkeys.ofNotes(input)
        return monkeys.start(20, 3).sumOfTwoMostActive()
    }

    fun part2(input: List<String>): Long {
        val monkeys = Monkeys.ofNotes(input)
        return monkeys.start(10000, 1).sumOfTwoMostActive()
    }

    //val input = readInput("test")
    val input = readInput("prod")
    println(part1(input))
    println(part2(input))
}

class Monkeys(
    private val monkeys: List<Monkey>
) {

    private val commonDivisor = monkeys.map { it.throwCondition.divisible }.reduce(Int::times)

    fun start(rounds: Int, divisor: Int): Monkeys {
        repeat(rounds) {
            monkeys.forEach {
                it.takeTurn(monkeys, divisor, commonDivisor)
            }
        }
        return this
    }

    fun sumOfTwoMostActive() = monkeys
        .sortedByDescending(Monkey::inspected)
        .take(2)
        .map(Monkey::inspected)
        .reduce(Long::times)

    companion object {
        fun ofNotes(notes: List<String>): Monkeys {
            return Monkeys(
                notes.splitBy(String::isBlank).map(Monkey::ofNotes)
            )
        }
    }
}

data class ItemToThrow(val level: Long, val toMonkey: Int)

data class ThrowCondition(
    val divisible: Int,
    val onTrue: Int,
    val onFalse: Int,
) {
    fun chooseMonkeyAndItem(level: Long): ItemToThrow {
        return if (level.rem(divisible) == 0L) ItemToThrow(level, onTrue) else ItemToThrow(level, onFalse)
    }
}

data class Monkey(
    val id: Int,
    val items: ArrayDeque<Long>,
    val inspect: (Long) -> Long,
    val throwCondition: ThrowCondition,
    var inspected: Long = 0
) {
    fun takeTurn(monkeys: List<Monkey>, divisor: Int, commonDivisor: Int) {
        fun throwToOther(item: ItemToThrow) =
            monkeys[item.toMonkey].items.addLast(item.level)
        inspected += items
            .asSequence()
            .map(inspect)
            .map { bored(it, divisor, commonDivisor) }
            .map(throwCondition::chooseMonkeyAndItem)
            .onEach(::throwToOther)
            .count()
        items.clear()
    }

    private fun bored(level: Long, divisor: Int, commonDivisor: Int): Long {
        return (level / divisor) % commonDivisor
    }

    companion object {
        fun ofNotes(notes: List<String>): Monkey {
            val id = notes.first().substringAfter(" ").substringBefore(":").toInt()
            val (st, op, tst, tr, fl) = notes.slice(1..notes.lastIndex)
            val startingItems = st.substringAfter(": ").split(", ").mapTo(ArrayDeque()) { it.toLong() }
            val operation = op.substringAfter(": ")
            val test = tst.substringAfter(": ")
            val condition = ThrowCondition(
                test.substringAfter("divisible by ").toInt(),
                tr.substringAfter(": ").substringAfter("throw to monkey ").toInt(),
                fl.substringAfter(": ").substringAfter("throw to monkey ").toInt()
            )
            return Monkey(
                id = id,
                items = startingItems,
                inspect = operation.toWorryOp(),
                throwCondition = condition
            )
        }
    }
}

private fun String.toWorryOp(): (Long) -> Long {
    val assignment = substringAfter(" = ")
    val op = assignment[assignment.indexOfAny("*+-/".toCharArray())]
    val first = substringAfter(" = ").substringBefore(op).trim()
    val second = substringAfter(op).trim()
    fun toVar(old: Long, variable: String): Long = when (variable) {
        "old" -> old
        else -> variable.toLong()
    }
    return { old ->
        when (op) {
            '*' -> toVar(old, first) * toVar(old, second)
            '+' -> toVar(old, first) + toVar(old, second)
            '-' -> toVar(old, first) - toVar(old, second)
            '/' -> toVar(old, first) / toVar(old, second)
            else -> error(op)
        }
    }
}
