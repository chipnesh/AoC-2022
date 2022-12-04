fun main() {

    fun part1(input: List<String>): Int {
        return input.sumOf { Rucksack(it).duplicates().priority ?: 0 }
    }

    fun part2(input: List<String>): Int {
        return input
            .chunked(3)
            .mapNotNull { group ->
                group
                    .map(::Rucksack)
                    .reduce(Rucksack::duplicates)
                    .single()
                    ?.priority
            }.sum()
    }

    val input = readInput("Day03_test")
    //val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}

@JvmInline
value class Item(private val value: Char) {
    val priority: Int? get() = priorities[value]

    companion object {
        val priorities = (('a'..'z') + ('A'..'Z'))
            .withIndex()
            .associate { it.value to it.index + 1 }
    }
}

@JvmInline
value class Compartment(private val value: String) {
    fun duplicates(other: Compartment): Item {
        val otherSet = other.value.toSet()
        return value.first { it in otherSet }.let(::Item)
    }
}

@JvmInline
value class Rucksack(private val value: String) {

    private val left get() = Compartment(value.substring(0 until value.length / 2))
    private val right get() = Compartment(value.substring(value.length / 2, value.length))

    fun duplicates() = left.duplicates(right)

    fun duplicates(other: Rucksack): Rucksack {
        val otherSet = other.value.toSet()
        return value
            .mapNotNullTo(mutableSetOf()) { item -> if (item in otherSet) item else null }
            .toCharArray()
            .concatToString()
            .let(::Rucksack)
    }

    fun single() =
        value
            .singleOrNull()
            ?.let(::Item)
}
