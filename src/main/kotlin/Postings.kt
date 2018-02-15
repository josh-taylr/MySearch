interface Postings : Iterable<DocumentNumber> {

    fun and(other: Postings): Postings

    fun or(other: Postings): Postings
}