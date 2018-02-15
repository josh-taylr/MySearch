interface Index {
    fun beginIndexing()

    fun endIndexing()

    fun startTag(tag: String)

    fun endTag(tag: String)

    fun word(term: String)
}