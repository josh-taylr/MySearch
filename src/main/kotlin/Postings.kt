interface Postings : Iterable<DocumentNumber> {
    fun and(other: InMemoryPostings): InMemoryPostings
    fun or(other: InMemoryPostings): InMemoryPostings
}