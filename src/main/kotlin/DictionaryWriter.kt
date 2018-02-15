import java.io.OutputStream

interface DictionaryWriter {

    fun write(dictionary: Dictionary, stream: OutputStream)
}