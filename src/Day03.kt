fun main() {

    var i = 1
    val lowerItems = ('a'..'z').associateWith { i++ }
    val upperItems = ('A'..'Z').associateWith { i++ }
    val items = lowerItems + upperItems

    fun String.halfSplit(): Pair<Set<Char>, Set<Char>> {
        val array = this.toCharArray()
        val half = length / 2
        return substring(0 until half).toSet() to substring(half until length).toSet()
    }

    fun commonItem(pair: Pair<Set<Char>, Set<Char>>): Char {
        return pair.first.intersect(pair.second).single()
    }

    fun commonItems(first: Set<Char>, second: Set<Char>): Set<Char> {
        return first.intersect(second)
    }

    fun part1(input: List<String>): Int {
        return input
            .map(String::halfSplit)
            .map(::commonItem)
            .mapNotNull(items::get)
            .sum()
    }

    fun part2(input: List<String>): Int {
        return input
            .chunked(3)
            .map { group ->
                group
                    .map(String::toSet)
                    .reduce(::commonItems)
                    .single()
            }
            .mapNotNull(items::get)
            .sum()
    }

    //val input = readInput("Day03_test")
    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
