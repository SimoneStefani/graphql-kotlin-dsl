package dev.simonestefani.types

data class GraphQLVariableType(val value: String)

data class GraphQLVariable(val name: String, val type: GraphQLVariableType, val jsonValue: String) {
    val dollarName: String
        get() = "\$$name"
}
