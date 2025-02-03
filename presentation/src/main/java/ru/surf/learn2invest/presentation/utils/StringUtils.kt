package ru.surf.learn2invest.presentation.utils

fun String.isThisContainsSequenceIfIsNotEmpty(): Boolean {
    for (number in 0..6) {
        if (contains(
                "$number${number + 1}${number + 2}${number + 3}"
            ) || isEmpty()
        ) return true
    }
    return false
}

fun String.isThisContains3NumbersIfIsNotEmpty(): Boolean {
    for (number in 0..9) {
        if (contains("$number".repeat(3)) || isEmpty()) {
            return true
        }
    }
    return false
}

