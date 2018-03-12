interface RankingStrategy {

    fun rank(dictionary: Dictionary, documentLength: Map<DocumentNumber, Int>, terms: Iterable<String>): List<Result>
}