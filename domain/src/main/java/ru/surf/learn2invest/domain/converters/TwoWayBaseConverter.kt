package ru.surf.learn2invest.domain.converters

/**
 * Interface for bidirectional conversion between two types.
 *
 * @param A The type of the first object.
 * @param B The type of the second object.
 */
interface TwoWayBaseConverter<A, B> {

    /**
     * Converts an object of type A to an object of type B.
     *
     * @param a The source object of type A.
     * @return The converted object of type B.
     */
    fun convertAB(a: A): B

    /**
     * Converts an object of type B to an object of type A.
     *
     * @param b The source object of type B.
     * @return The converted object of type A.
     */
    fun convertBA(b: B): A

    /**
     * Converts a list of objects of type A to a list of objects of type B.
     *
     * @param listA The list of objects of type A.
     * @return The list of converted objects of type B.
     */
    fun convertABList(listA: List<A>): List<B> = listA.map {
        convertAB(it)
    }

    /**
     * Converts a list of objects of type B to a list of objects of type A.
     *
     * @param listB The list of objects of type B.
     * @return The list of converted objects of type A.
     */
    fun convertBAList(listB: List<B>): List<A> = listB.map {
        convertBA(it)
    }
}
