package encode

@ExperimentalUnsignedTypes
object VariableByte : NumberEncoder {

    private const val MAX_BYTE: UByte = 0b1000_0000u

    override fun decode(bytes: Iterable<Byte>): List<Long> {
        val numbers = mutableListOf<Long>()
        var n = 0uL
        bytes.asSequence()
                .map(Byte::toUByte)
                .forEach { byte ->
                    if (byte < MAX_BYTE) {
                        n = MAX_BYTE * n + byte
                    } else {
                        n = MAX_BYTE * n + (byte xor MAX_BYTE)
                        numbers.add(n.toLong())
                        n = 0uL
                    }
                }
        return numbers
    }

    override fun encode(numbers: Iterable<Long>): List<Byte> = numbers.flatMap(this::encode)

    internal fun encode(number: Long): List<Byte> {
        var num = number.toULong()
        val bytes = mutableListOf<UByte>()
        do {
            bytes += (num % MAX_BYTE).toUByte()
            num /= MAX_BYTE
        } while (num > 0u)
        bytes[0] = bytes[0] or MAX_BYTE
        bytes.reverse()
        return bytes.map(UByte::toByte)
    }
}
