import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) =
    File("src", "$name.txt")
        .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() =
    BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
        .toString(16)
        .padStart(32, '0')

fun readInts(name: String) =
    readInput(name)
        .map(String::toInt)

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