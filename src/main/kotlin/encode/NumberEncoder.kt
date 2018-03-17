package encode

interface NumberEncoder {
    fun encode(numbers: Iterable<Long>): List<Byte>
    fun decode(bytes: Iterable<Byte>): List<Long>
}