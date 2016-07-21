package edu.csh.chase.kjson

import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.util.*

class JsonArray() : JsonBase(), Iterable<Any?> {

    private val array = ArrayList<Any?>()

    override val size: Int
        get() {
            return array.size
        }

    val indices: IntRange
        get() {
            return array.indices
        }

    constructor(tokener: JsonTokener) : this() {
        if (tokener.nextClean() != '[') {
            throw tokener.syntaxError("A JSONArray text must start with '['")
        }
        if (tokener.nextClean() != ']') {
            tokener.back()
            while (true) {
                if (tokener.nextClean() == ',') {
                    tokener.back()
                    array.add(null)
                } else {
                    tokener.back()
                    array.add(tokener.nextValue())
                }
                when (tokener.nextClean()) {
                    ',' -> {
                        if (tokener.nextClean() == ']') {
                            return
                        }
                        tokener.back()
                    }
                    ']' -> return
                    else -> throw tokener.syntaxError("Expected a ',' or ']'")
                }
            }
        }
    }

    constructor(jsonString: String) : this(JsonTokener(jsonString))

    constructor(list: Collection<Any?>) : this() {
        array.addAll(list.filter { it.isValidJsonType() })
    }

    constructor(vararg array: Any?) : this() {
        this.array.addAll(array.filter { it.isValidJsonType() })
    }

    private fun getValue(index: Int): Any? {
        if (index !in array.indices) {
            return null
        }
        return array[index]
    }

    private fun setValue(index: Int, value: Any?) {
        if (index !in array.indices) {
            return//Throws exception?
        }
        if (!value.isValidJsonType()) {
            throw JsonException("$value is not a valid JsonException")
        }
        array [index] = value
    }

    //Setters

    fun set(index: Int, value: Any?) {
        setValue(index, value)
    }

    //Putters

    fun put(value: Any?): JsonArray {
        if (!value.isValidJsonType()) {
            throw JsonException("$value is not a valid JsonException")
        }
        array.add(value)
        return this
    }

    fun putNotNull(value: Any?): JsonArray {
        if (value == null) {
            return this
        }
        return put(value)
    }

    //Getters

    /**
     * Gets a value from this JsonArray
     * If the index is out of bounds of the array it will return null
     *
     * @param index The index of the array to get the value from
     * @return The value found at the given index, or null if the index is out of bounds
     */
    operator fun get(index: Int): Any? = getValue(index)

    /**
     * Gets a value from this JsonArray
     * If no value is found, or the value is null, will return default instead
     *
     * @param index The index of the array to get the value from
     * @param default The default value
     * @return The value found at the given index, or default if the value is null or index out of bounds
     */
    operator fun get(index: Int, default: Any): Any = getValue(index) ?: default

    /**
     * Gets a Boolean from this JsonArray
     * If the index is out of bounds of the array it will return null
     *
     * @param index The index of the array to get the value from
     * @return The Boolean found at the given index, or null if the index is out of bounds
     */
    fun getBoolean(index: Int): Boolean? = getValue(index) as? Boolean

    /**
     * Gets a Boolean from this JsonArray
     * If no value is found, or the value is null, will return default instead
     *
     * @param index The index of the array to get the value from
     * @param default The default value
     * @return The Boolean found at the given index, or default if the value is null or index out of bounds
     */
    fun getBoolean(index: Int, default: Boolean): Boolean = getBoolean(index) ?: default

    /**
     * Gets an Int from this JsonArray
     * If the index is out of bounds of the array it will return null
     *
     * @param index The index of the array to get the value from
     * @return The Int found at the given index, or null if the index is out of bounds
     */
    fun getInt(index: Int): Int? = getValue(index) as? Int

    /**
     * Gets an Int from this JsonArray
     * If no value is found, or the value is null, will return default instead
     *
     * @param index The index of the array to get the value from
     * @param default The default value
     * @return The Int found at the given index, or default if the value is null or index out of bounds
     */
    fun getInt(index: Int, default: Int): Int = getInt(index) ?: default

    /**
     * Gets a Double from this JsonArray
     * If the index is out of bounds of the array it will return null
     *
     * @param index The index of the array to get the value from
     * @return The Double found at the given index, or null if the index is out of bounds
     */
    fun getDouble(index: Int): Double? = getValue(index) as? Double

    /**
     * Gets a Double from this JsonArray
     * If no value is found, or the value is null, will return default instead
     *
     * @param index The index of the array to get the value from
     * @param default The default value
     * @return The Double found at the given index, or default if the value is null or index out of bounds
     */
    fun getDouble(index: Int, default: Double): Double = getDouble(index) ?: default

    /**
     * Gets a String from this JsonArray
     * If the index is out of bounds of the array it will return null
     *
     * @param index The index of the array to get the value from
     * @return The String found at the given index, or null if the index is out of bounds
     */
    fun getString(index: Int): String? = getValue(index) as? String

    /**
     * Gets a String from this JsonArray
     * If no value is found, or the value is null, will return default instead
     *
     * @param index The index of the array to get the value from
     * @param default The default value
     * @return The String found at the given index, or default if the value is null or index out of bounds
     */
    fun getString(index: Int, default: String): String = getString(index) ?: default

    /**
     * Gets a JsonObject from this JsonArray
     * If the index is out of bounds of the array it will return null
     *
     * @param index The index of the array to get the value from
     * @return The JsonObject found at the given index, or null if the index is out of bounds
     */
    fun getJsonObject(index: Int): JsonObject? = getValue(index) as? JsonObject

    /**
     * Gets a JsonObject from this JsonArray
     * If no value is found, or the value is null, will return default instead
     *
     * @param index The index of the array to get the value from
     * @param default The default value
     * @return The JsonObject found at the given index, or default if the value is null or index out of bounds
     */
    fun getJsonObject(index: Int, default: JsonObject): JsonObject = getJsonObject(index) ?: default

    fun getJsonArray(index: Int): JsonArray? = getValue(index) as? JsonArray

    fun getJsonArray(index: Int, default: JsonArray): JsonArray = getJsonArray(index) ?: default

    /**
     * Gets the value from a given index if the value is a number
     *
     * @param index The index to pull the value from
     * @return The Float from the given index, null if no value or not a number
     */
    fun getFloat(index: Int): Float? {
        val v = get(index)
        return if (v is Number) {
            v.toFloat()
        } else {
            null
        }
    }

    /**
     * Gets the value from the given index if the value is a number
     *
     * @param index The index to pull a value from
     * @param default The default value is no value is found
     * @return The Float from the given index, default if no value or not a number
     */
    fun getFloat(index: Int, default: Float): Float {
        val v = get(index)
        return if (v is Number) {
            v.toFloat()
        } else {
            default
        }
    }

    /**
     * Gets the value from a given index if the value is a number
     *
     * @param index The index to pull the value from
     * @return The Float from the given index, null if no value or not a number
     */
    fun getLong(index: Int): Long? {
        val v = get(index)
        return if (v is Number) {
            v.toLong()
        } else {
            null
        }
    }

    /**
     * Gets the value from the given index if the value is a number
     *
     * @param index The index to pull a value from
     * @param default The default value is no value is found
     * @return The Float from the given index, default if no value or not a number
     */
    fun getLong(index: Int, default: Long): Long {
        val v = get(index)
        return if (v is Number) {
            v.toLong()
        } else {
            default
        }
    }

    //Other Functions

    /**
     * Gets an immutable copy of this JsonArray's internal array
     *
     * @return An immutable array
     */
    fun getInternalArray(): List<Any?> = Collections.unmodifiableList(array)

    fun remove(index: Int): Any? = array.remove(index)

    operator fun contains(index: Int): Boolean {
        return index in array.indices
    }

    override fun jsonSerialize(): String {
        return this.toString(false)
    }

    override fun iterator(): Iterator<Any?> {
        return array.iterator()
    }

    override fun equals(other: Any?): Boolean {
        return other is JsonArray && array == other.array
    }

    override fun toString(): String {
        return toString(false)
    }

    override fun toString(shouldIndent: Boolean, depth: Int): String {
        val sw = StringWriter()
        synchronized (sw.buffer) {
            return this.write(sw, shouldIndent, depth).toString()
        }
    }

    /**
     * Write the contents of the JSONArray as JSON text to a writer. For
     * compactness, no whitespace is added.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return The writer.
     * @throws JSONException
     */
    fun write(writer: Writer): Writer {
        return this.write(writer, false)
    }


    /**
     * Write the contents of the JSONArray as JSON text to a writer. For
     * compactness, no whitespace is added.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param indentFactor
     *            The number of spaces to add to each level of indentation.
     * @param indent
     *            The indention of the top level.
     * @return The writer.
     * @throws JSONException
     */
    fun write(writer: Writer, shouldIndent: Boolean, depth: Int = 1): Writer {
        try {
            var addComa = false

            writer.write("[")

            for (value in array) {

                if (addComa) {
                    writer.write(",")
                }
                if (shouldIndent) {
                    writer.write("\n")
                    writer.indent(depth)
                }
                writer.write(JsonValues.toString(value, shouldIndent, depth + 1))
                addComa = true
            }
            if (shouldIndent) {
                writer.write("\n")
                writer.indent(depth - 1)
            }
            writer.write("]")
            return writer
        } catch (e: IOException) {
            throw JsonException(e)
        }
    }

}