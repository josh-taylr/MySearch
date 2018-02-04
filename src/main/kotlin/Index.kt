open class Index {

    fun beginIndexing() {

    }

    fun endIndexing() {

    }

    open fun startTag(tag: String) {
        print("<$tag>")
    }

    open fun endTag(tag: String) {
        print("</$tag>")

        // leave a blank line between each document
        if ("DOC" == tag) println()
    }

    open fun word(term: String) {
        print(term)
    }
}