import java.io.File

class Parse constructor(private val index: Index) {

    fun parse(file: File) {
        val regex = Regex("<([a-zA-Z][a-zA-Z0-9]*)>|</([a-zA-Z][a-zA-Z0-9]*)>")
        file.bufferedReader().use {
            val document = it.readText()
            var result = regex.find(document)
            while (null != result) {
                val (startTag, endTag) = result.destructured
                if (startTag.isNotEmpty()) index.startTag(startTag)
                if (endTag.isNotEmpty()) index.endTag(endTag)
                result = result.next()
            }
        }
    }
}