import java.io.InputStream

interface DictionaryReader {

    fun read(stream: InputStream): Dictionary
}