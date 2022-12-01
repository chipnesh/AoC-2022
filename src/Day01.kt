fun main() {
    fun elfCalories(input: List<String>): MutableMap<Int, MutableList<Int>> {
        var idx = 1
        return input.fold(mutableMapOf()) { acc, calories ->
            if (calories.isBlank()) {
                idx++
            } else {
                acc.getOrPut(idx, ::mutableListOf).add(calories.toInt())
            }
            acc
        }
    }

    fun part1(input: List<String>): Int {
        val caloriesPerElf = elfCalories(input)
        return caloriesPerElf.values.maxOfOrNull { it.sum() } ?: -1
    }

    fun part2(input: List<String>): Int {
        val caloriesPerElf = elfCalories(input)
        val caloriesSums = caloriesPerElf.mapValues { it.value.sum() }.values
        return caloriesSums.sortedDescending().take(3).sum()
    }

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
