import java.io.InputStream
import java.io.OutputStream
import java.util.*

class DictionaryFileStream {

    fun write(dictionary: Dictionary, stream: OutputStream) {
        for ((term, postings) in dictionary) {
            stream.write(term.toByteArray())
            for (posting in postings) {
                stream.write(separator.toInt())
                stream.write(posting.toByteArray())
            }
            stream.write(separator.toInt())
        }
    }

    fun read(stream: InputStream): Dictionary {
        val map: TreeMap<String, Dictionary.Postings> = TreeMap()

        val scanner = Scanner(stream).useDelimiter("$separator")
        val termPattern = "\\p{Lower}+"
        val postingPattern = "WSJ\\p{Digit}{6}-\\p{Digit}{4}"

        while (scanner.hasNext(termPattern)) {
            val term = scanner.next()
            val postings = Dictionary.Postings()
            while (scanner.hasNext(postingPattern)) {
                val posting = scanner.next()
                postings.add(posting)
            }
            map[term] = postings
        }

        return Dictionary(map)
    }

    companion object {
        const val separator = ' '
    }
}
