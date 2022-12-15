import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.Int.Companion
import kotlin.math.absoluteValue

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

data class Coords(val x: Int = 0, val y: Int = 0) {
    constructor(pair: Pair<Int, Int>) : this(pair.first, pair.second)
}

fun Coords.above(): Coords = copy(y = y - 1)
fun Coords.below(): Coords = copy(y = y + 1)
fun Coords.leftDown(): Coords = copy(x = x - 1, y = y + 1)
fun Coords.rightDown(): Coords = copy(x = x + 1, y = y + 1)

@JvmName("get")
operator fun <T> List<List<T>>.get(coords: Coords): T {
    return this[coords.x][coords.y]!!
}

@JvmName("getOrNull")
operator fun <T> MutableList<MutableList<T>>.get(coords: Coords) = getOrNull(coords.x)?.getOrNull(coords.y)

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

fun Coords.toBounds(distance: Int): Bounds {
    return Bounds(x - distance, x + distance, y - distance, y + distance)
}

data class Bounds(
    val minX: Int,
    val maxX: Int,
    val minY: Int,
    val maxY: Int,
)

fun <T> Coords.getNeighbours(matrix: List<List<T>>) = getNeighbours(0, matrix.lastIndex, 0, matrix[0].lastIndex)

fun Coords.getNeighbours(bounds: Bounds) = getNeighbours(bounds.minX, bounds.maxX, bounds.minY, bounds.maxY)

fun Coords.getNeighbours(minX: Int, maxX: Int, minY: Int, maxY: Int): List<Coords> {
    val neighbours = mutableListOf<Coords>()
    if (x + 1 <= maxX) neighbours.add(Coords(x + 1, y))
    if (y + 1 <= maxY) neighbours.add(Coords(x, y + 1))
    if (x - 1 >= 0) neighbours.add(Coords(x - 1, y))
    if (y - 1 >= 0) neighbours.add(Coords(x, y - 1))
    return neighbours
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

fun List<String>.splitEach(delimiter: String): List<List<String>> {
    return map { it.split(delimiter) }
}

fun <T, R> List<T>.toPair(mapper: (T) -> R = { it as R }) = mapper(get(0)) to mapper(get(1))

val IntRange.length get() = last.toLong() - first + 1

fun IntRange.intersects(other: IntRange) =
    first in other || last in other || other.first in this || other.last in this

operator fun IntRange.contains(other: IntRange): Boolean {
    return other.first in this && other.last in this
}

operator fun IntRange.times(other: IntRange) =
    IntRange(minOf(first, other.first), maxOf(last, other.last))

operator fun IntRange.plus(other: IntRange) =
    other.takeIf(this::intersects)?.let { this * other }

val IntRange.Companion.MAX: IntRange get() = Int.MIN_VALUE..Int.MAX_VALUE
