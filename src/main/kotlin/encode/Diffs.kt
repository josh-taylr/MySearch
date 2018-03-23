package encode

fun Collection<Long>.diffs(): List<Long> {
    if (isEmpty()) return emptyList()
    return listOf(first()) + zipWithNext { a, b -> b - a }
}

fun Collection<Long>.expandDiffs(): List<Long> {
    if (isEmpty()) return emptyList()
    return drop(1).fold(listOf(first())) { list, n -> list + (list.last() + n) }
}