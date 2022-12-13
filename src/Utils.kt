import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String): List<String> {
    val packageName = Thread.currentThread().stackTrace.last().className.substringBefore(".")
    val inputFile = File("src", "$packageName/$name.txt")
    return inputFile.readLines()
}

/**
 * Converts string to md5 hash.
 */
fun String.md5() =
    BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
        .toString(16)
        .padStart(32, '0')

fun List<String>.sumBySplitting(condition: (String) -> Boolean): List<Int> {
    return splitBy(condition).map { it.sumOf { it.toInt() } }
}

fun List<String>.splitBy(predicate: (String) -> Boolean): List<List<String>> {
    val elements = mutableListOf<String>()
    return fold(mutableListOf<MutableList<String>>()) { acc, string ->
        acc.apply {
            if (predicate(string)) {
                acc.add(elements.toMutableList())
                elements.clear()
            } else {
                elements.add(string)
            }
        }
    }.also {
        if (elements.isNotEmpty()) it.add(elements)
    }
}

fun String.hasDuplicates(): Boolean {
    val duplicates = IntArray(255)
    for (i in indices) {
        duplicates[get(i).code]++
        if (duplicates[get(i).code] > 1) {
            return true
        }
    }
    return false
}

fun String.noDuplicates() = !hasDuplicates()
