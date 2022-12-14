package day7

import day7.CMDLine.Command
import day7.CMDLine.Command.CD
import day7.CMDLine.Command.CD.Back
import day7.CMDLine.Command.CD.Root
import day7.CMDLine.Command.CD.To
import day7.CMDLine.Command.LS
import day7.CMDLine.Output
import day7.CMDLine.Output.DIR
import day7.CMDLine.Output.FileInfo
import readInput

sealed interface CMDLine {
    sealed interface Command : CMDLine {
        sealed interface CD : Command {
            object Root : CD
            object Back : CD
            data class To(val dir: String) : CD

            companion object {
                fun parse(command: String) = when (command) {
                    "/" -> Root
                    ".." -> Back
                    else -> To(command)
                }
            }
        }

        object LS : Command

        companion object {
            fun parse(command: String) = when {
                command.startsWith("cd") -> CD.parse(command.drop(3))
                command.startsWith("ls") -> LS
                else -> throw UnsupportedOperationException(command)
            }
        }
    }

    sealed interface Output : CMDLine {
        data class DIR(val name: String) : Output
        data class FileInfo(val name: String, val size: Int) : Output
        companion object {
            fun parse(line: String) = when {
                line.startsWith("dir") -> DIR(line.drop(4))
                else -> FileInfo(line.substringAfter(" "), line.substringBefore(" ").toInt())
            }
        }
    }

    companion object {
        fun parse(line: String): CMDLine {
            return when {
                line.startsWith("$") -> Command.parse(line.drop(2))
                else -> Output.parse(line)
            }
        }
    }
}

class File(
    val name: String,
    private val fileSize: Int = 0,
    val children: MutableList<File> = mutableListOf()
) {
    val isRoot get() = name == "/"

    val isNotRoot get() = !isRoot

    val isDirectory get() = fileSize == 0

    val size: Int get() = fileSize + children.sumOf { it.size }

    fun firstChildren(predicate: (File) -> Boolean): File = children.first(predicate)

    fun findChildren(name: String): File = firstChildren { it.name == name }

    fun add(dir: File) = children.add(dir)

    override fun toString(): String {
        val type = if (fileSize == 0) "dir" else "file"
        return "$type $name $size"
    }
}

class FileTree {

    private val stack: ArrayDeque<File> = ArrayDeque()

    init {
        stack.add(File("/"))
    }

    fun getRoot() = stack.first()

    fun goBack() {
        if (stack.size == 1) return
        stack.removeLast()
    }

    fun currentDir() = stack.last()

    fun goTo(name: String) {
        stack.add(currentDir().findChildren(name))
    }

    fun addFile(file: FileInfo) {
        currentDir().add(File(file.name, file.size))
    }

    fun addDir(dir: DIR) {
        currentDir().add(File(dir.name))
    }

    fun goToRoot() {
        while (stack.lastOrNull()?.isNotRoot == true) goBack()
    }

    companion object {
        fun fromHistory(history: List<String>): FileTree {
            val tree = FileTree()
            tree.applyHistory(history)
            tree.goToRoot()
            return tree
        }
    }

    private fun applyHistory(input: List<String>) {
        for (cmd in input.map(CMDLine::parse)) {
            when (cmd) {
                is Command -> when (cmd) {
                    is CD -> when (cmd) {
                        Root -> goToRoot()
                        Back -> goBack()
                        is To -> goTo(cmd.dir)
                    }

                    is LS -> Unit // ignore
                }

                is Output -> when (cmd) {
                    is FileInfo -> addFile(cmd)
                    is DIR -> addDir(cmd)
                }
            }
        }
    }

    fun visitDirectories(action: (File) -> Unit) {
        return stack.visit(File::isDirectory, action)
    }

    private fun Iterable<File>.visit(predicate: (File) -> Boolean = { true }, action: (File) -> Unit) {
        for (file in this@visit) {
            if (predicate(file)) {
                action(file)
                file.children.visit(predicate, action)
            }
        }
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val tree = FileTree.fromHistory(input)
        return buildList {
            tree.visitDirectories {
                if (it.size < 100000) add(it)
            }
        }.sumOf { it.size }
    }

    fun part2(input: List<String>): Int {
        val tree = FileTree.fromHistory(input)
        return buildList {
            val usedSpace = tree.getRoot().size
            tree.visitDirectories { file ->
                if (70000000 - 30000000 - usedSpace + file.size > 0) {
                    add(file)
                }
            }
        }.minByOrNull { it.size }?.size ?: -1
    }

    //val input = readInput("test")
    val input = readInput("prod")
    println(part1(input))
    println(part2(input))
}
