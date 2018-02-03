import java.io.File

const val EOF = -1

class Parse constructor(private val index: Index) {

    fun parse(file: File) {
        val startTag = Regex("<[a-zA-Z][a-zA-Z0-9]*>")
        file.bufferedReader().use {
            val buffer = StringBuffer()
            while (true) {
                val c = it.read()
                if (c == EOF) break
                buffer.append(c.toChar())
                if (buffer.matches(startTag)) {
                    index.startTag(buffer.subSequence(1, buffer.length-1) as String)
                }
            }
        }
    }
}