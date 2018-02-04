import Parse.State.*
import Parse.TERM_STATE.*
import java.io.File

class Parse constructor(private val index: Index) {

    private val EOF = (-1).toChar()

    fun parse(file: File) {
        var sb = StringBuilder()

        file.bufferedReader().use {
            var state = INITIAL
            do {
                val c = it.read().toChar()
                if (EOF == c) break
                state = state.next(c)
                when (state) {
                    WORD -> acceptTerm(c)
                    TAG -> {
                        acceptTerm(' ') // hack to
                        sb = StringBuilder()
                    }
                    END_TAG_READ -> sb.append(c)
                    END_TAG_ACCEPT -> index.endTag(sb.toString())
                    START_TAG_READ -> sb.append(c)
                    START_TAG_ACCEPT -> index.startTag(sb.toString())
                    ESCAPE_AMP_TERMINAL -> acceptTerm('&')
                    ESCAPE_LT_T -> acceptTerm('<')
                    FAILURE -> throw ParseException("Error parsing file.")
                    else -> Unit
                }
            } while (state != FAILURE)
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
                else -> FAILURE
            }
        },

        START_TAG_READ {
            override fun next(c: Char) = when {
                c.isLetterOrDigit() -> START_TAG_READ
                c == '>' -> START_TAG_ACCEPT
                else -> FAILURE
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
                else -> FAILURE
            }
        },

        END_TAG_READ {
            override fun next(c: Char): State = when {
                c.isLetterOrDigit() -> END_TAG_READ
                c == '>' -> END_TAG_ACCEPT
                else -> FAILURE
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
            override fun next(c: Char): State = when (c) {
                'a' -> ESCAPE_AMP_A
                'l' -> ESCAPE_LT_L
                else -> FAILURE
            }
        },

        ESCAPE_AMP_A {
            override fun next(c: Char): State = when (c) {
                'm' -> ESCAPE_AMP_M
                else -> FAILURE
            }
        },

        ESCAPE_AMP_M {
            override fun next(c: Char): State = when (c) {
                'p' -> ESCAPE_AMP_P
                else -> FAILURE
            }
        },

        ESCAPE_AMP_P {
            override fun next(c: Char): State = when (c) {
                ';' -> ESCAPE_AMP_TERMINAL
                else -> FAILURE
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
                else -> FAILURE
            }
        },

        ESCAPE_LT_T {
            override fun next(c: Char): State = when (c) {
                ';' -> ESCAPE_LT_TERMINAL
                else -> FAILURE
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
        },

        FAILURE {
            override fun next(c: Char): State = FAILURE
        };

        abstract fun next(c: Char): State

    }

    private enum class TERM_STATE {
        NOT_IN_WORD, IN_WORD;
    }

    private var termState = NOT_IN_WORD
    private var termBuilder = StringBuilder()

    private fun acceptTerm(c: Char) {
        when (termState) {
            NOT_IN_WORD -> {
                if (!c.isWhitespace()) {
                    termBuilder = StringBuilder().append(c)
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
    }
}
