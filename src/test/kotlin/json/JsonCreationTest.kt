package json

import edu.csh.chase.kjson.JsonArray
import edu.csh.chase.kjson.JsonObject
import edu.csh.chase.kjson.json
import org.junit.Test
import kotlin.test.assertEquals

class JsonCreationTest {

    @Test fun testObjectCreationEmpty() {
        val obj = JsonObject()
        assertEquals(obj, json ())
    }

    @Test fun testObjectCreation() {
        val obj = JsonObject("key" to "value")
        assertEquals(obj,
                json (
                        "key" to "value"
                )
        )
    }


    @Test fun testArrayCreationEmpty() {
        val arr = JsonArray()
        assertEquals(arr, json.get())
    }

    @Test fun testArrayCreation() {
        val arr = JsonArray().put("value")
        assertEquals(arr, json[
                "value"
                ])
    }

}