package dictionary

data class DocumentNumber(val value: Long) {

    override fun toString(): String {
        val match = Companion.numberPattern.matchEntire(value.toString()) ?: throw error("Matching document number.")

        return match.destructured.let { (start, end) ->
            "WSJ$start-$end"
        }
    }

    companion object {
        private val numberPattern = Regex("(\\p{Digit}{6})(\\p{Digit}{4})")
        private val stringPattern = Regex("WSJ(\\p{Digit}{6})-(\\p{Digit}{4})")

        fun parse(str: String): DocumentNumber {
            val match = Companion.stringPattern.matchEntire(str) ?: throw error("Parsing document number.")

            val value = match.destructured.let { (first, last) ->
                first + last
            }.toLong()

            return DocumentNumber(value)
        }
    }
}