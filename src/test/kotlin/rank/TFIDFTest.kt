package rank

import dictionary.Dictionary
import dictionary.DocumentNumber
import dictionary.postingsOf
import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Test

class TFIDFTest {

    /**
        Structure of documents indexed into dictionary:

        <DOC>
        <DOCNO>WSJ920100-0001</DOCNO>
        <TEXT>
            search engine search
        </TEXT>
        </DOC>

        <DOC>
        <DOCNO>WSJ920100-0002</DOCNO>
        <TEXT>
            search
        </TEXT>
        </DOC>
     */
    private val dictionary: Dictionary = mapOf(
        "search" to postingsOf("WSJ920100-0001" to 2, "WSJ920100-0002" to 1),
        "engine" to postingsOf("WSJ920100-0001" to 1)
    )
    private val documentLengths: Map<DocumentNumber, Int> = mapOf(
        DocumentNumber.parse("WSJ920100-0001") to 3,
        DocumentNumber.parse("WSJ920100-0002") to 1
    )

    @Test
    fun `empty query returns empty result`() {
        // when
        val rank = TFIDF.rank(dictionary, documentLengths, emptyList())
        // then
        assertThat(rank, `is`(empty<Result>()))
    }

    @Test
    fun `result has predicted weight of term in single document`() {
        // when
        val rank = TFIDF.rank(dictionary, documentLengths, listOf("engine"))
        // then
        assertThat(rank, contains(
            allOf(
                hasProperty("document", `is`(DocumentNumber.parse("WSJ920100-0001"))),
                hasProperty("weight", `is`(closeTo(0.66666, ErrorDelta)))
            )
        ))
    }

    @Test
    fun `result has predicted weight of term in all documents`() {
        // when
        val rank = TFIDF.rank(dictionary, documentLengths, listOf("search"))
        // then
        assertThat(rank, contains(
            allOf(
                hasProperty("document", `is`(DocumentNumber.parse("WSJ920100-0002"))),
                hasProperty("weight", `is`(closeTo(1.0, ErrorDelta)))
            ),
            allOf(
                hasProperty("document", `is`(DocumentNumber.parse("WSJ920100-0001"))),
                hasProperty("weight", `is`(closeTo(0.66666, ErrorDelta)))
            )
        ))
    }

    @Test
    fun `result has summed multiple terms`() {
        // when
        val rank = TFIDF.rank(dictionary, documentLengths, listOf("search", "engine"))
        // then
        assertThat(rank, contains(
            allOf(
                hasProperty("document", `is`(DocumentNumber.parse("WSJ920100-0001"))),
                hasProperty("weight", `is`(closeTo(1.33333, ErrorDelta)))
            ),
            allOf(
                hasProperty("document", `is`(DocumentNumber.parse("WSJ920100-0002"))),
                hasProperty("weight", `is`(closeTo(1.0, ErrorDelta)))
            )
        ))
    }

    private companion object {
        private const val ErrorDelta = 0.00001
    }
}