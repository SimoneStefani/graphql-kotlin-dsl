import dev.simonestefani.Kraph
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RequestTest {
    private val query = Kraph {
        query("GetUserId") {
            field("user", args = mapOf("name" to variable("name", "User", "{\"name\": \"UserName\"}"))) {
                field("id")
            }
        }
    }

    @Test
    fun `should print the entire document`() {
        // given
        val expected =
            "{\"query\": \"query GetUserId (\$name: User) { user (name: \$name) { id } }\", \"variables\": {\"name\": {\"name\": \"UserName\"}}, \"operationName\": \"GetUserId\"}"

        // when
        val result = query.toRequestString()

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `should print just the query portion of the document`() {
        // given
        val expected = "query GetUserId (\$name: User) { user (name: \$name) { id } }"

        // when
        val result = query.requestQueryString()

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `should print just the variable portion of the document`() {
        // given
        val expected = "{\"name\": {\"name\": \"UserName\"}}"

        // when
        val result = query.requestVariableString()

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `should print the name of the query`() {
        // given
        val expected = "GetUserId"

        // when
        val result = query.requestOperationName()

        // then
        assertEquals(expected, result)
    }
}
