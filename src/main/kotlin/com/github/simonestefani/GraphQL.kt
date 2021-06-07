package com.github.simonestefani

import com.github.simonestefani.lang.Argument
import com.github.simonestefani.lang.Document
import com.github.simonestefani.lang.Field
import com.github.simonestefani.lang.Operation
import com.github.simonestefani.lang.OperationType
import com.github.simonestefani.lang.PrintFormat
import com.github.simonestefani.lang.SelectionSet
import com.github.simonestefani.lang.Variables
import com.github.simonestefani.types.GraphQLVariable
import com.github.simonestefani.types.GraphQLVariableType

typealias FieldBlock = GraphQL.FieldBuilder.() -> Unit

class GraphQL(f: GraphQL.() -> Unit) {

    internal lateinit var document: Document
    internal val variables: Variables = Variables()

    init {
        f.invoke(this)
    }

    fun query(name: String? = null, builder: FieldBlock) {
        val set = createSelectionSet("query", builder)
        document = Document(
            Operation(
                OperationType.QUERY,
                selectionSet = set,
                name = name,
                arguments = variables.asArgument()
            ), variables
        )
    }

    fun mutation(name: String? = null, builder: FieldBlock) {
        val set = createSelectionSet("mutation", builder)
        document = Document(
            Operation(
                OperationType.MUTATION,
                selectionSet = set,
                name = name,
                arguments = variables.asArgument()
            ), variables
        )
    }

    private fun createSelectionSet(name: String, f: FieldBlock): SelectionSet {
        val builder = FieldBuilder().apply(f)
        val set = SelectionSet(builder.fields)
        if (set.fields.isEmpty()) {
            throw NoFieldsInSelectionSetException("No field elements inside \"$name\" block")
        }
        return set
    }

    fun toGraphQueryString() = document.operation.print(PrintFormat.PRETTY, 0)
    fun toRequestString() = document.print(PrintFormat.JSON, 0)

    fun requestQueryString() = document.operation.print(PrintFormat.NORMAL, 0)
    fun requestVariableString() = document.variables.print(PrintFormat.JSON, 0)
    fun requestOperationName() = document.operation.name

    open inner class FieldBuilder {
        internal val fields = arrayListOf<Field>()

        fun fieldObject(name: String, alias: String? = null, args: Map<String, Any>? = null, builder: FieldBlock) {
            addField(name, alias, args, builder)
        }

        fun field(name: String, alias: String? = null, args: Map<String, Any>? = null, builder: FieldBlock? = null) {
            addField(name, alias, args, builder)
        }

        fun fragment(name: String) {
            fragments[name]?.invoke(this)
                ?: throw NoSuchFragmentException("No fragment named \"$name\" has been defined.")
        }

        fun variable(name: String, type: String, jsonValue: String): GraphQLVariable =
            GraphQLVariable(name, GraphQLVariableType(type), jsonValue).also {
                variables.variables[name] = it
            }

        protected fun addField(
            name: String,
            alias: String? = null,
            args: Map<String, Any>? = null,
            builder: FieldBlock? = null
        ) {
            val argNode = args?.let(::Argument)
            val selectionSet = builder?.let {
                createSelectionSet(name, builder)
            }
            fields += Field(name, alias, arguments = argNode, selectionSet = selectionSet)
        }
    }

    companion object {
        private var fragments: Map<String, FieldBlock> = emptyMap()
        fun defineFragment(name: String, builder: FieldBlock) {
            fragments = fragments.plus(name to builder)
        }
    }
}
