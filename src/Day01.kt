fun main() {

    fun splitByCondition(input: List<String>, condition: (String) -> Boolean): List<Int> {
        val elements = mutableListOf<Int>()
        return input.fold(mutableListOf<Int>()) { acc, calories ->
            acc.apply {
                if (condition(calories)) {
                    add(elements.sum())
                    elements.clear()
                } else {
                    elements.add(calories.toInt())
                }
            }
        } + elements.sum()
    }

    fun part1(input: List<String>): Int {
        val caloriesPerElf = splitByCondition(input, String::isBlank)
        return caloriesPerElf.maxOrNull() ?: -1
    }

    fun part2(input: List<String>): Int {
        val caloriesSums = splitByCondition(input, String::isBlank)
        return caloriesSums.sortedDescending().take(3).sum()
    }

    //val input = readInput("Day01_test")
    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
