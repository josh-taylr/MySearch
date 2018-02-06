import Parse.State.*
import Parse.TERM_STATE.*
import java.io.File
import java.util.*

class Parse constructor(private val index: Index, val documentCount: Int = Int.MAX_VALUE) {

    private val EOF = (-1).toChar()
    private val tags = Stack<String>()

    private var documentsParsed = 0

    fun parse(file: File) {
        val sb = StringBuilder()

        file.bufferedReader().use {
            var state = INITIAL
            index.beginIndexing()
            while (documentsParsed < documentCount) {
                val c = it.read().toChar()
                if (EOF == c) break
                state = state.next(c)
                when (state) {
                    WORD -> acceptTerm(c)
                    TAG -> {
                        acceptTerm(' ') // hack to
                        sb.setLength(0)
                    }
                    END_TAG_READ -> sb.append(c)
                    END_TAG_ACCEPT -> {
                        val new = sb.toString()
                        val current = tags.pop()
                        if (new == "DOC") documentsParsed++
                        if (new != current) throw TagMismatchException("Expected '$current' new, but encountered '$new'")
                        index.endTag(new)
                    }
                    START_TAG_READ -> sb.append(c)
                    START_TAG_ACCEPT -> {
                        val tag = sb.toString()
                        index.startTag(tag)
                        tags.push(tag)
                    }
                    ESCAPE_AMP_TERMINAL -> acceptTerm('&')
                    ESCAPE_LT_T -> acceptTerm('<')
                }
            }
            index.endIndexing()
        }
    }

    enum class State {

        INITIAL {
            override fun next(c: Char) = when (c) {
                '<' -> TAG
                '&' -> ESCAPE_INITIAL
                else -> WORD
            }
        },

        TAG {
            override fun next(c: Char) = when {
                '/' == c -> END_TAG
                c.isLetter() -> START_TAG_READ
                else -> throw ParseException("Parse error. Expected '/' or letter. Got '$c'.")
            }
        },

        START_TAG_READ {
            override fun next(c: Char) = when {
                c.isLetterOrDigit() -> START_TAG_READ
                c == '>' -> START_TAG_ACCEPT
                else -> throw ParseException("Parse error. Expected letter, digit, or '>'. Got '$c'.")
            }
        },

        START_TAG_ACCEPT {
            override fun next(c: Char) = when (c) {
                '<' -> TAG
                '&' -> ESCAPE_INITIAL
                else -> WORD
            }
        },

        END_TAG {
            override fun next(c: Char) = when {
                c.isLetter() -> END_TAG_READ
                else -> throw ParseException("Parse error. Expected a letter. Got '$c'.")
            }
        },

        END_TAG_READ {
            override fun next(c: Char): State = when {
                c.isLetterOrDigit() -> END_TAG_READ
                c == '>' -> END_TAG_ACCEPT
                else -> throw ParseException("Parse error. Expected letter, digit, or '>'. Got '$c'.")
            }
        },

        END_TAG_ACCEPT {
            override fun next(c: Char): State = when (c) {
                '<' -> TAG
                '&' -> ESCAPE_INITIAL
                else -> WORD
            }
        },

        ESCAPE_INITIAL {
            override fun next(c: Char): State = when {
                'a' == c -> ESCAPE_AMP_A
                'l' == c -> ESCAPE_LT_L
                c.isWhitespace() -> WORD
                else -> throw ParseException("Parse error. Expected 'a' or 'l'. Got '$c'.")
            }
        },

        ESCAPE_AMP_A {
            override fun next(c: Char): State = when (c) {
                'm' -> ESCAPE_AMP_M
                else -> throw ParseException("Parse error. Expected 'm'. Got '$c'.")
            }
        },
        ESCAPE_AMP_M {
            override fun next(c: Char): State = when (c) {
                'p' -> ESCAPE_AMP_P
                else -> throw ParseException("Parse error. Expected 'p'. Got '$c'.")
            }
        },

        ESCAPE_AMP_P {
            override fun next(c: Char): State = when (c) {
                ';' -> ESCAPE_AMP_TERMINAL
                else -> throw ParseException("Parse error. Expected ';'. Got '$c'.")
            }
        },

        ESCAPE_AMP_TERMINAL {
            override fun next(c: Char): State = when (c) {
                '<' -> TAG
                '&' -> ESCAPE_INITIAL
                else -> WORD
            }
        },

        ESCAPE_LT_L {
            override fun next(c: Char): State = when(c) {
                't' -> ESCAPE_LT_T
                else -> throw ParseException("Parse error. Expected 't'. Got '$c'.")
            }
        },

        ESCAPE_LT_T {
            override fun next(c: Char): State = when (c) {
                ';' -> ESCAPE_LT_TERMINAL
                else -> throw ParseException("Parse error. Expected ';'. Got '$c'.")
            }
        },

        ESCAPE_LT_TERMINAL {
            override fun next(c: Char): State = when (c) {
                '<' -> TAG
                '&' -> ESCAPE_INITIAL
                else -> WORD
            }
        },

        WORD {
            override fun next(c: Char) = when (c) {
                '<' -> TAG
                '&' -> ESCAPE_INITIAL
                else -> WORD
            }
        };

        abstract fun next(c: Char): State

    }

    private enum class TERM_STATE {
        NOT_IN_WORD, IN_WORD;
    }

    private var termState = NOT_IN_WORD
    private val termBuilder = StringBuilder()

    private fun acceptTerm(c: Char) {
        when (termState) {
            NOT_IN_WORD -> {
                if (!c.isWhitespace()) {
                    termBuilder.setLength(0)
                    termBuilder.append(c)
                    termState = IN_WORD
                }
            }
            IN_WORD -> {
                if (!c.isWhitespace()) {
                    termBuilder.append(c)
                } else {
                    index.word(termBuilder.toString())
                    termState = NOT_IN_WORD
                }
            }
        }
    }

    companion object {
        class ParseException(message: String?) : Exception(message)
        class TagMismatchException(message: String?) : Exception(message)
    }
}
