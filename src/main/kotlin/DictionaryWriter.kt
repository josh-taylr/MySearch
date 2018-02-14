import java.io.InputStream
import java.io.OutputStream

interface DictionaryWriter {

    fun write(dictionary: Dictionary, stream: OutputStream)

    fun read(stream: InputStream): Dictionary
}