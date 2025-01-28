package ru.surf.learn2invest.domain.converters

/**
 * Interface for converting objects of one type to another.
 *
 * @param A The type of the source object.
 * @param B The type of the target object.
 */
interface OneWayBaseConverter<A, B> {
    /**x
     * Converts an object of type A to an object of type B.
     *
     * @param a The source object of type A.
     * @return The converted object of type B.
     */
    fun convert(a: A): B

    /**
     * Converts a list of objects of type A to a list of objects of type B.
     *
     * @param listA A list of objects of type A.
     * @return A list of converted objects of type B.
     */
    fun convertList(listA: List<A>): List<B> = listA.map {
        convert(it)
    }
}