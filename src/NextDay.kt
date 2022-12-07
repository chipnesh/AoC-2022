import java.io.File
import java.time.LocalDate
import kotlin.io.FileWalkDirection.TOP_DOWN

fun main() {
    val now = LocalDate.now()
    if (now.isAfter(LocalDate.of(2022, 12, 31))) {
        println("Happy new year!")
        return
    }
    val lastDay = lastSolutionDay()
    val solutionDay = "day${lastDay + 1}"
    val nextDayDir = File("src", solutionDay)
    nextDayDir.mkdir()
    File("src/template").copyRecursively(nextDayDir)
    val solution = nextDayDir.resolve("Solution.kt")
    val fixedPackageFile = solution.readText().replace("package template", "package $solutionDay")
    solution.writeText(fixedPackageFile)
}

private fun lastSolutionDay(): Int {
    return File("src").walk(TOP_DOWN)
        .filter { it.isDirectory && it.path.startsWith("src/day") }
        .map { it.path.substringAfter("/").dropWhile { it.isLetter() }.toInt() }
        .maxOrNull() ?: 1
}
