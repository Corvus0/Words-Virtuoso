package wordsvirtuoso
import java.io.File
import kotlin.random.Random

fun main(args: Array<String>) {
    if (args.size != 2) {
        println("Error: Wrong number of arguments.")
        return
    }
    val wordsLines = validateFile(args[0])
    if (wordsLines == null) return
    val candsLines = validateFile(args[1], true)
    if (candsLines == null) return
    var notContained = 0
    val lowerWords = wordsLines.map { it.lowercase() }
    for (cand in candsLines) {
        if (cand.lowercase() !in lowerWords) {
            notContained++
        }
    }
    if (notContained != 0) {
        println("Error: $notContained candidate words are not included in the ${args[0]} file.")
        return
    } else println("Words Virtuoso")
    val index = Random.Default.nextInt(candsLines.size)
    game(candsLines[index], wordsLines)
}

fun validateFile(fileName: String, candidate: Boolean = false): List<String>? {
    val valFile = File(fileName)
    if (!valFile.exists()) {
        println("Error: The ${if (candidate) "candidate " else ""}words file $fileName doesn't exist.")
        return null
    }
    val lines = valFile.readLines()
    var invalidWords = 0
    for (line in lines) {
        if (!validateWord(line)) {
            invalidWords++
        }
    }
    if (invalidWords != 0) {
        println("Error: $invalidWords invalid words were found in the $fileName file.")
        return null
    }
    return lines
}

fun validateWord(word: String, words: List<String>? = null, print: Boolean = false): Boolean {
    val message = when {
        word.length != 5 -> "The input isn't a 5-letter word."
        word.toSet().size != 5 -> "The input has duplicate letters."
        !Regex("[a-zA-Z]+").matches(word) -> "One or more letters of the input aren't valid."
        words != null && word !in words -> "The input word isn't included in my words list."
        else -> ""
    }
    if (print) println(message)
    return message.length == 0
}

fun game(secret: String, words: List<String>) {
    val start = System.currentTimeMillis()
    var guess = ""
    var guesses = 0
    var hints = emptyList<String>()
    var wrongChars = ""
    while (guess != secret) {
        println("Input a 5-letter word:")
        guess = readln()
        if (guess == "exit") {
            println("\nThe game is over.")
            return
        }
        if (!validateWord(guess, words, true)) continue
        guesses++
        var (hint, wrong) = generateHint(guess, secret)
        hints += hint
        wrongChars = (wrongChars + wrong).toSet().toCharArray().sorted().joinToString("")
        for (hint in hints) {
            println(hint)
        }
        if (wrong.length != 0) {
            println("\n\u001B[48:5:14m$wrongChars\u001B[0m\n")
        }
    }
    val end = System.currentTimeMillis()
    println("Correct!")
    if (guesses == 1) {
        println("Amazing luck! The solution was found at once.")
    } else {
        println("The solution was found after $guesses tries in ${end - start} seconds.")
    }
}

fun generateHint(guess: String, secret: String): Pair<String, String> {
    var hint = ""
    var wrongChars = ""
    for (i in guess.indices) {
        val letter = guess[i].uppercase()
        if (guess[i] == secret[i]) {
            hint += "\u001B[48:5:10m$letter\u001B[0m"
        } else if (guess[i] in secret) {
            hint += "\u001B[48:5:11m$letter\u001B[0m"
        } else {
            hint += "\u001B[48:5:7m$letter\u001B[0m"
            wrongChars += letter
        }
    }
    return Pair(hint, wrongChars)
}
