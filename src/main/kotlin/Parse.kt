import java.io.File

const val EOF = -1

class Parse constructor(private val index: Index) {

    fun parse(file: File) {
        val regex = Regex("<([a-zA-Z][a-zA-Z0-9]*)>|</([a-zA-Z][a-zA-Z0-9]*)>")
        file.bufferedReader().use {
            val buffer = StringBuffer()
            while (true) {
                val c = it.read()
                if (c == EOF) break
                buffer.append(c.toChar())
                val result = regex.find(buffer)
                if (null != result) {
                    val (startTag, endTag) = result.destructured
                    if (startTag.isNotEmpty()) index.startTag(startTag)
                    if (endTag.isNotEmpty()) index.endTag(endTag)
                    buffer.setLength(0)
                }
            }
        }
    }
}