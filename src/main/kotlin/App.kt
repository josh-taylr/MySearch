import java.io.File

private val indexWriter: (Dictionary) -> Unit = {
    it.forEach { (term, postings) -> println("$term -> $postings") }
}

fun main(args: Array<String>) {
    Parse(Index(indexWriter), documentCount = 1).parse(File("/Users/Josh/Documents/wsj.xml"))
}
