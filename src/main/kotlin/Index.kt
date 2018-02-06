interface Index {
    open fun beginIndexing()

    open fun endIndexing()

    open fun startTag(tag: String)

    open fun endTag(tag: String)

    open fun word(term: String)
}