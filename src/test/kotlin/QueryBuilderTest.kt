import assertk.tableOf
import dev.simonestefani.Kraph
import dev.simonestefani.NoSuchFragmentException
import dev.simonestefani.lang.OperationType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QueryBuilderTest {
    private val query = Kraph {
        query("getAllNotes") {
            fieldObject("notes") {
                field("id")
                field("content")
                fieldObject("author") {
                    field("name")
                    field("email")
                }
                field("avatarUrl", args = mapOf("size" to 100))
            }
        }
    }

    @Test
    fun `should have query operation type`() {
        // given
        val expected = OperationType.QUERY

        // when
        val result = query.document.operation.type

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `should have only one field inside query`() {
        // given
        val expected = 1

        // when
        val result = query.document.operation.selectionSet.fields.size

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `should have field named notes inside query`() {
        // given
        val expected = "notes"

        // when
        val result = query.document.operation.selectionSet.fields[0].name

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `should have four fields inside note object`() {
        // given
        val expected = 4

        // when
        val result = query.document.operation.selectionSet.fields[0].selectionSet!!.fields.size

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `should have field inside notes object`() {
        // given
        tableOf("index", "expected")
            .row(0, "id")
            .row(1, "content")
            .row(2, "author")
            .row(3, "avatarUrl")
            .forAll { index, expected ->
                // when
                val result = query.document.operation.selectionSet.fields[0].selectionSet!!.fields[index].name

                // then
                assertEquals(expected, result)
            }
    }

    @Test
    fun `should have size argument with value 100 for avatarUrl field`() {
        // given
        val expected = 100

        // when
        val result =
            query.document.operation.selectionSet.fields[0].selectionSet!!.fields[3].arguments!!.args["size"] as Int

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `should have two fields inside author object`() {
        // given
        val expected = 2

        // when
        val result = query.document.operation.selectionSet.fields[0].selectionSet!!.fields[2].selectionSet!!.fields.size

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `should have field inside author object`() {
        // given
        tableOf("index", "expected")
            .row(0, "name")
            .row(1, "email")
            .forAll { index, expected ->
                // when
                val result = query.document.operation.selectionSet
                    .fields[0].selectionSet!!.fields[2].selectionSet!!.fields[index].name

                // then
                assertEquals(expected, result)
            }
    }

    @Test
    fun `should be able to print the request for network call`() {
        // given
        val expected =
            "{\"query\": \"query getAllNotes { notes { id content author { name email } avatarUrl (size: 100) } }\", \"variables\": null, \"operationName\": \"getAllNotes\"}"

        // when
        val result = query.toRequestString()

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `should be able to print GraphQL query content with pretty format`() {
        // given
        val expected =
            "query getAllNotes {\n  notes {\n    id\n    content\n    author {\n      name\n      email\n    }\n    avatarUrl (size: 100)\n  }\n}"

        // when
        val result = query.toGraphQueryString()

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `should be able to print the request for network call with aliases`() {
        // given
        val queryWithAliases = Kraph {
            query("getAllNotes") {
                fieldObject("notes", alias = "aliasedNotes") {
                    field("id", alias = "aliasedId")
                }
            }
        }

        val expected =
            "{\"query\": \"query getAllNotes { aliasedNotes: notes { aliasedId: id } }\", \"variables\": null, \"operationName\": \"getAllNotes\"}"

        // when
        val result = queryWithAliases.toRequestString()

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `should throw a NoSuchFragmentException when the fragment doesn't exist`() {
        // then
        assertThrows<NoSuchFragmentException> {
            // when
            Kraph {
                query {
                    fieldObject("user") {
                        fragment("FakeFragment")
                    }
                }
            }
        }
    }

    @Test
    fun `should expand the fields in the fragment when the fragment exists`() {
        // given
        Kraph.defineFragment("UserFragment") {
            field("name")
            field("email")
        }
        val query = Kraph {
            query {
                fieldObject("user") {
                    fragment("UserFragment")
                }
            }
        }

        // then
        assertEquals("user", query.document.operation.selectionSet.fields[0].name)
        assertEquals("name", query.document.operation.selectionSet.fields[0].selectionSet!!.fields[0].name)
        assertEquals("email", query.document.operation.selectionSet.fields[0].selectionSet!!.fields[1].name)
    }
}
