package day10

import day10.Op.AddX
import day10.Op.Noop
import readInput

fun main() {

    fun part1(input: List<String>): Int {
        val summarizer = SignalStrengthSummarizer()
        Cpu.execute(input, summarizer::update)
        return summarizer.getSum()
    }

    fun part2(input: List<String>): Int {
        val crt = Crt()
        Cpu.execute(input, crt::update)
        crt.printScreen(::println)
        return -1
    }

    //val input = readInput("test")
    val input = readInput("prod")
    println(part1(input))
    println(part2(input))
}

class SignalStrengthSummarizer {
    private val strengths = mutableMapOf<Int, Int>()

    fun update(cycle: Int, x: Int) {
        val level = getStrengthLevel(cycle)
        if (cycle == level) {
            strengths[level] = x
        }
    }

    fun getSum(): Int {
        return strengths.map { it.key * it.value }.sum()
    }

    private fun getStrengthLevel(cycle: Int): Int {
        return when (cycle) {
            in 0..20 -> 20
            in 20..60 -> 60
            in 60..100 -> 100
            in 100..140 -> 140
            in 140..180 -> 180
            in 180..220 -> 220
            else -> 0
        }
    }
}

class Crt {
    private val crt = mutableListOf<MutableList<Char>>()

    fun update(cycle: Int, x: Int) {
        val line = getCrtLine(cycle)
        crt.init(line).apply {
            val idx = maxOf((cycle - 1) % 40, 0)
            if (idx in (x - 1..x + 1)) add('#') else add('.')
        }
    }

    fun printScreen(print: (String) -> Unit) {
        val screenData = crt.joinToString(System.lineSeparator()) {
            it.joinToString("") { it.toString() }
        }
        print(screenData)
    }

    private fun MutableList<MutableList<Char>>.init(line: Int): MutableList<Char> {
        while (size <= line) {
            val lineChars = mutableListOf<Char>()
            add(lineChars)
            return lineChars
        }
        return get(line)
    }

    private fun getCrtLine(cycle: Int): Int {
        return when (cycle) {
            in 1..40 -> 0
            in 41..80 -> 1
            in 81..120 -> 2
            in 121..160 -> 3
            in 161..200 -> 4
            in 201..240 -> 5
            else -> -1
        }
    }
}

data class Cpu(
    private var x: Int = 1,
    private var cycle: Int = 1,
    private var cycles: Int = cycle,
    private var strength: Int = 1,
    val onCycle: (Int, Int) -> Unit
) {
    private val executing: ArrayDeque<Op> = ArrayDeque()
    val isRunning get() = cycle >= 0

    fun run() {
        while (isRunning) execute()
    }

    fun schedule(op: Op): Cpu {
        executing.add(op)
        return this
    }

    private fun execute() {
        onCycle(cycles, x)
        when (val op = executing.firstOrNull() ?: return) {
            is AddX -> execute(op) { x += op.x }
            Noop -> execute(op)
        }
        when {
            executing.isEmpty() -> cycle = -1
            else -> nextCycle()
        }
    }

    private fun nextCycle() {
        cycle++
        cycles++
    }

    private fun execute(op: Op, onRun: () -> Unit = {}) {
        if (cycle >= op.cycles) {
            cycle = 0
            executing.removeFirst()
            onRun()
        }
    }

    companion object {
        fun execute(input: List<String>, onCycle: (Int, Int) -> Unit) {
            input
                .toOps()
                .fold(Cpu(onCycle = onCycle), Cpu::schedule)
                .run()
        }
    }
}

sealed class Op(val cycles: Int) {
    object Noop : Op(1)
    data class AddX(val x: Int) : Op(2)
}

fun String.toOp() = when {
    startsWith("noop") -> Noop
    else -> AddX(substringAfter(" ").toInt())
}

fun List<String>.toOps(): List<Op> {
    return map(String::toOp)
}