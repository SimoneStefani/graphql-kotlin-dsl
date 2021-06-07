package com.github.simonestefani

import assertk.tableOf
import com.github.simonestefani.lang.OperationType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MutationBuilderTest {
    private val mutation = GraphQL {
        mutation {
            field("registerUser", args = mapOf("email" to "abcd@efgh.com", "password" to "abcd1234", "age" to 30)) {
                field("id")
                field("token")
            }
        }
    }

    @Test
    fun `should have mutation operation type`() {
        // given
        val expected = OperationType.MUTATION

        // when
        val result = mutation.document.operation.type

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `should have only one mutation`() {
        // given
        val expected = 1

        // when
        val result = mutation.document.operation.selectionSet.fields.size

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `should have mutation named registerUser`() {
        // given
        val expected = "registerUser"

        // when
        val result = mutation.document.operation.selectionSet.fields[0].name

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `should have 3 arguments in registerUser mutation`() {
        // given
        val expected = 3

        // when
        val result = mutation.document.operation.selectionSet.fields[0].arguments!!.args.entries.size

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `should have argument in registerUser mutation with named and value`() {
        // given
        tableOf("key", "expected")
            .row("email", "abcd@efgh.com")
            .row("password", "abcd1234")
            .forAll { key, expected ->
                // when
                val result = mutation.document.operation.selectionSet.fields[0].arguments!!.args[key] as String

                // then
                assertEquals(expected, result)
            }
    }

    @Test
    fun `should have argument in registerUser mutation with named age and value as 30`() {
        // given
        val expected = 30

        // when
        val result = mutation.document.operation.selectionSet.fields[0].arguments!!.args["age"] as Int

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `should contains 2 field in registerUser payload`() {
        // given
        val expected = 2

        // when
        val result = mutation.document.operation.selectionSet.fields[0].selectionSet!!.fields.size

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `should have field in registerUser payload`() {
        // given
        tableOf("index", "expected")
            .row(0, "id")
            .row(1, "token")
            .forAll { index, expected ->
                // when
                val result = mutation.document.operation.selectionSet.fields[0].selectionSet!!.fields[index].name

                // then
                assertEquals(expected, result)
            }
    }

    @Test
    fun `should be able to print the request for network call with aliases`() {
        // given
        val mutationWithAliases = GraphQL {
            mutation {
                field("registerUser", alias = "aliasedRegisterUser", args = mapOf("email" to "abcd@efgh.com")) {
                    field("id")
                }
            }
        }

        val expected =
            "{\"query\": \"mutation { aliasedRegisterUser: registerUser (email: \\\"abcd@efgh.com\\\") { id } }\", \"variables\": null, \"operationName\": null}"

        // when
        val result = mutationWithAliases.toRequestString()

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `should throw NoFieldsInSelectionSetException with no field in selection set`() {
        // then
        assertThrows<NoFieldsInSelectionSetException> {
            // when
            GraphQL {
                mutation { }
            }
        }
    }
}
