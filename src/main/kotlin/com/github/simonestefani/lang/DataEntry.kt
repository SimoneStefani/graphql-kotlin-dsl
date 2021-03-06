package com.github.simonestefani.lang

import com.github.simonestefani.types.GraphQLVariable
import com.github.simonestefani.types.GraphQLVariableType

internal sealed class DataEntry {
    abstract fun print(format: PrintFormat): String

    class NonDecimalNumberData(private val value: Long) : DataEntry() {
        constructor(value: Int) : this(value.toLong())

        override fun print(format: PrintFormat) = value.toString()
    }

    class DecimalNumberData(private val value: Double) : DataEntry() {
        override fun print(format: PrintFormat) = value.toString()
    }

    class BooleanData(private val value: Boolean) : DataEntry() {
        override fun print(format: PrintFormat) = value.toString()
    }

    class VariableData(private val value: GraphQLVariable) : DataEntry() {
        override fun print(format: PrintFormat) = value.dollarName
    }

    class VariableType(private val value: GraphQLVariableType) : DataEntry() {
        override fun print(format: PrintFormat) = value.value
    }

    class StringData(private val value: String) : DataEntry() {
        override fun print(format: PrintFormat) =
            if (format == PrintFormat.JSON) {
                "\\\"$value\\\""
            } else {
                "\"$value\""
            }
    }

    class ArrayData(private val values: List<DataEntry>) : DataEntry() {
        override fun print(format: PrintFormat) =
            "[${values.joinToString(", ") { it.print(format) }}]"
    }

    class ObjectData(private val values: List<Pair<String, DataEntry>>) : DataEntry() {
        override fun print(format: PrintFormat) =
            "{${
                values.joinToString(", ") { (k, v) ->
                    "${k}: ${v.print(format)}"
                }
            }}"
    }

    class EnumData(private val value: Enum<*>) : DataEntry() {
        override fun print(format: PrintFormat): String = value.name
    }
}
