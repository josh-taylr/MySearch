import java.io.File

const val EOF = -1

class Parse constructor(private val index: Index) {

    private fun CharSequence.trimStartTag() = subSequence(1, length-1) as String

    private fun CharSequence.trimEndTag() = subSequence(2, length-1) as String

    private fun StringBuffer.clear() = removeRange(0, length)


    fun parse(file: File) {
        val startTag = Regex("<[a-zA-Z][a-zA-Z0-9]*>")
        val endTag = Regex("</[a-zA-Z][a-zA-Z0-9]*>")
        file.bufferedReader().use {
            val buffer = StringBuffer()
            while (true) {
                val c = it.read()
                if (c == EOF) break
                buffer.append(c.toChar())
                if (buffer.contains(startTag)) {
                    index.startTag(buffer.trimStartTag())
                    buffer.setLength(0)
                } else if (buffer.contains(endTag)) {
                    index.endTag(buffer.trimEndTag())
                    buffer.setLength(0)
                }
            }
        }
    }
}