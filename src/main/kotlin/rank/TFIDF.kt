package rank

import dictionary.Dictionary
import dictionary.DocumentNumber
import dictionary.Posting
import dictionary.emptyPostings

object TFIDF : RankingStrategy {

    override fun rank(dictionary: Dictionary,
                      documentLength: Map<DocumentNumber, Int>,
                      terms: Iterable<String>): List<Result> {

        if (terms.count() == 0) {
            return emptyList()
        }

        return terms.map { term ->
            val postings = dictionary.getOrDefault(term, emptyPostings())
            postings.map { posting ->
                val tf = termDensity(posting) {
                    documentLength[posting.documentNumber]
                            ?: error("Accessing length of document not present in dictionary.")
                }
                val idf = inverseDocumentFrequency(term, documentLength.count()) { postings.size }
                Result(posting.documentNumber, tf * idf)
            }
        }.reduce { acc, results ->
            val p1 = acc.associateBy({ it.document }, { it.weight })
            val p2 = results.associateBy({ it.document }, { it.weight })
            p1.keys.union(p2.keys).map {
                Result(it, (p1[it] ?: 0.0) + (p2[it] ?: 0.0))
            }
        }.sortedByDescending { it.weight }
    }

    private inline fun termDensity(posting: Posting, documentLength: (DocumentNumber) -> Int): Double {
        return posting.termFrequency.toDouble() / documentLength.invoke(posting.documentNumber).toDouble()
    }

    private inline fun inverseDocumentFrequency(term: String, documents: Int, documentsContainingTerm: (String) -> Int): Double {
        return 1/(documentsContainingTerm(term) / documents.toDouble())
    }
}