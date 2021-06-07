import assertk.tableOf
import dev.simonestefani.lang.DataEntry
import dev.simonestefani.lang.PrintFormat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DataEntryTest {

    @Test
    fun `should print data correctly`() {
        // given
        tableOf("data", "normal", "pretty", "json")
            .row(DataEntry.NonDecimalNumberData(5) as DataEntry, "5", "5", "5")
            .row(DataEntry.DecimalNumberData(5.0) as DataEntry, "5.0", "5.0", "5.0")
            .row(DataEntry.BooleanData(true) as DataEntry, "true", "true", "true")
            .row(
                DataEntry.StringData("Hello world") as DataEntry,
                "\"Hello world\"",
                "\"Hello world\"",
                "\\\"Hello world\\\""
            )
            .row(
                DataEntry.ArrayData(listOf(1, 2, 3).map { DataEntry.NonDecimalNumberData(it) }) as DataEntry,
                "[1, 2, 3]",
                "[1, 2, 3]",
                "[1, 2, 3]"
            )
            .row(
                DataEntry.ObjectData(
                    listOf(
                        "name" to DataEntry.StringData("John Doe"),
                        "age" to DataEntry.NonDecimalNumberData(18)
                    )
                ) as DataEntry,
                "{name: \"John Doe\", age: 18}",
                "{name: \"John Doe\", age: 18}",
                "{name: \\\"John Doe\\\", age: 18}"
            )
            .row(DataEntry.EnumData(Type.SHORT) as DataEntry, "SHORT", "SHORT", "SHORT")
            .forAll { data: DataEntry, normal, pretty, json ->
                // then
                assertEquals(normal, data.print(PrintFormat.NORMAL))
                assertEquals(pretty, data.print(PrintFormat.PRETTY))
                assertEquals(json, data.print(PrintFormat.JSON))
            }
    }

    private enum class Type {
        SHORT
    }
}
