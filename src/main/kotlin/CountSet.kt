class CountSet<T> : AbstractMutableSet<Pair<T, Int>>() {

    private val map = mutableMapOf<T, Int>()

    override val size: Int
        get() = map.size

    fun count(element: T): Int = map.getOrDefault(element, 0)

    fun addElement(element: T): Boolean = null == map.put(element, count(element) + 1)

    override fun add(element: Pair<T, Int>): Boolean {
        val (e, count) = element
        return null == map.put(e, count)
    }

    override fun iterator(): MutableIterator<Pair<T, Int>> = map.entries
            .map { (e, count) -> e to count }
            .toMutableList()
            .iterator()
}