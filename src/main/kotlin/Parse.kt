import java.io.File

const val EOF = -1

class Parse {

    fun parse(file: File) {
        file.bufferedReader().use {
            while (true) {
                val c = it.read()
                if (c == EOF) break
                println(c.toChar())
            }
        }
    }
}