fun cleanTerm(term: String): String? = term
        .toLowerCase()
        .filter(Char::isLetter)
        .takeIf { it.length > 1 }