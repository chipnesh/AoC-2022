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

fun List<String>.toIntMatrix(): List<List<Int>> {
    return map { it.map { it.digitToInt() } }
}

fun <T> List<List<T>>.asCoordsSequence() = sequence {
    for (i in this@asCoordsSequence.indices) {
        for (j in this@asCoordsSequence[0].indices) {
            yield(Coords(i, j))
        }
    }
}

fun List<String>.toCharMatrix(): List<List<Char>> {
    return map { it.map { it } }
}

data class Coords(val x: Int = 0, val y: Int = 0)

operator fun <T> List<List<T>>.get(coords: Coords): T {
    return this[coords.x][coords.y]
}

fun <T> List<List<T>>.findFirst(toFind: T): Coords {
    for ((r, rc) in this.withIndex()) {
        for ((c, cc) in rc.withIndex()) {
            if (cc == toFind) return Coords(r, c)
        }
    }
    return Coords()
}

fun <T> List<List<T>>.findAll(toFind: T): List<Coords> {
    val result = mutableListOf<Coords>()
    for ((r, rc) in this.withIndex()) {
        for ((c, cc) in rc.withIndex()) {
            if (cc == toFind) result += Coords(r, c)
        }
    }
    return result
}

inline fun <T> Iterable<T>.takeWhileInclusive(predicate: (T) -> Boolean): List<T> {
    val list = ArrayList<T>()
    for (item in this) {
        if (!predicate(item)) {
            list.add(item)
            break
        }
        list.add(item)
    }
    return list
}
