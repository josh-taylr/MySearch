package encode

object VariableByte : NumberEncoder {

    private const val MAX_BYTE: UByte = 0b1000_0000u

    override fun decode(bytes: Iterable<Byte>): List<Long> {
        val numbers = mutableListOf<Long>()
        var n = 0L
        bytes.forEach { byte ->
            val uByte = byte.toUByte()
            if (uByte < MAX_BYTE) {
                n = MAX_BYTE.toLong() * n + uByte.toLong()
            } else {
                n = MAX_BYTE.toLong() * n + (uByte xor MAX_BYTE).toLong()
                numbers.add(n)
                n = 0L
            }
        }
        return numbers
    }

    override fun encode(numbers: Iterable<Long>): List<Byte> = numbers.flatMap(this::encode)

    internal fun encode(number: Long): List<Byte> {
        var num = number
        val bytes = mutableListOf<UByte>()
        do {
            bytes += (num % MAX_BYTE.toLong()).toUByte()
            num /= MAX_BYTE.toLong()
        } while (num > 0)
        bytes[0] = bytes[0] or MAX_BYTE
        bytes.reverse()
        return bytes.map(UByte::toByte)
    }
}
