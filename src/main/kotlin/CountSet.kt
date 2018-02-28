class CountSet<T> : AbstractMutableSet<T>() {

    private val map = mutableMapOf<T, Int>()

    override val size: Int
        get() = map.size

    fun count(element: T): Int = map.getOrDefault(element, 0)

    override fun add(element: T): Boolean = null == map.put(element, count(element) + 1)

    override fun iterator(): MutableIterator<T> = map.keys.iterator()
}