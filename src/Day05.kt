fun main() {

    fun part1(input: List<String>): String {
        val crates = Crates.ofDrawing(input.readDrawing())
        CrateMover9000().execute(
            Instructions.ofList(input.readInstructions()),
            crates
        )
        return crates.top()
    }

    fun part2(input: List<String>): String {
        val crates = Crates.ofDrawing(input.readDrawing())
        CrateMover9001().execute(
            Instructions.ofList(input.readInstructions()),
            crates
        )
        return crates.top()
    }

    //val input = readInput("Day05_test")
    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

fun List<String>.readDrawing(): List<String> =
    takeWhile { it.isNotBlank() }

fun List<String>.readInstructions(): List<String> =
    takeLastWhile { it.startsWith("move") }

@JvmInline
value class Crates(val stacks: List<ArrayDeque<Char>>) {

    fun top(): String {
        return stacks.joinToString("") { it.last().toString() }
    }

    companion object {
        fun ofDrawing(drawing: List<String>): Crates {
            val height = drawing.lastIndex - 1
            var stack = 0
            val numbers = drawing.last()
            val stackCount = numbers.split(" ").count { it.trim().toIntOrNull() != null }
            val stacks = MutableList(stackCount) { ArrayDeque<Char>() }
            for ((i, stackNumber) in numbers.withIndex()) {
                var count = height
                if (stackNumber.isDigit()) {
                    while (count >= 0) {
                        val letter = drawing[count].getOrNull(i)
                        if (letter?.isLetter() == true) {
                            stacks[stack].add(letter)
                        }
                        count -= 1
                    }
                    stack += 1
                }
            }
            return Crates(stacks)
        }
    }
}

data class Instruction(
    private val count: Int,
    private val from: Int,
    private val to: Int,
) {
    fun execute(crateMover: CrateMover, crates: Crates) {
        crateMover.move(from, to, count, crates)
    }

    companion object {
        fun of(line: String): Instruction {
            val (count, from, to) = line.split(" ").mapNotNull { it.trim().toIntOrNull() }
            return Instruction(count, from, to)
        }
    }
}

@JvmInline
value class Instructions(private val instructions: List<Instruction>) {

    fun execute(crateMover: CrateMover, crates: Crates) {
        instructions.forEach { it.execute(crateMover, crates) }
    }

    companion object {
        fun ofList(lines: List<String>): Instructions {
            return lines
                .map(Instruction::of)
                .let(::Instructions)
        }
    }
}

interface CrateMover {
    fun move(from: Int, to: Int, count: Int, crates: Crates)
}

class CrateMover9000 : CrateMover {

    fun execute(instructions: Instructions, crates: Crates) {
        instructions.execute(this, crates)
    }

    override fun move(from: Int, to: Int, count: Int, crates: Crates) {
        val stacks = crates.stacks
        repeat(count) {
            val item = stacks[from - 1].removeLast()
            stacks[to - 1].add(item)
        }
    }
}

class CrateMover9001 : CrateMover {

    fun execute(instructions: Instructions, crates: Crates) {
        instructions.execute(this, crates)
    }

    override fun move(from: Int, to: Int, count: Int, crates: Crates) {
        val stacks = crates.stacks
        val fromStack = stacks[from - 1]
        val toMove = List(count) { fromStack.removeLast() }.reversed()
        stacks[to - 1].addAll(toMove)
    }
}