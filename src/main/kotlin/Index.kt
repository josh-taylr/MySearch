open class Index {

    fun beginIndexing() {

    }

    fun endIndexing() {

    }

    open fun startTag(tag: String) {
        println("<$tag>")
    }

    open fun endTag(tag: String) {
        println("</$tag>")

        // leave a blank line between each document
        if ("DOC" == tag) println()
    }

    fun word(tag: String) {
        print(tag)
    }
}