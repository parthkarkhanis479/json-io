package com.cedarsoftware.util.io;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.cedarsoftware.util.io.JsonObject.ITEMS;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class JsonReaderTest {

    //test adJustOutputMapMethod when Object passed to adjustMap is nether array nor object

    /**
     * testMethod to check control flow of adjustOutputMap when object passed is neither array nor map.
     */
    @Test
    void testAdjustOutputMap() {
        try {
            JsonReader obj = new JsonReader();
            Method privateMethod = JsonReader.class.getDeclaredMethod("adjustOutputMap", Object.class);
            privateMethod.setAccessible(true);
            JsonObject returnValue = (JsonObject) privateMethod.invoke(obj, 5);
            Object[] objects = (Object[]) returnValue.get(ITEMS);
            assertTrue((int) objects[0] == 5);
        }
        catch (Exception e) {
            fail("Exception thrown :"+e);
        }
    }

    /**
     * testMethod to test different control flows of jsonToMaps Method and array control flow of adjustOutputMapMethod
     */
    @Test
    void testJsonToMaps()
    {
        String jsonString  = "[{\"a\":[1,2,3]}]";
        InputStream targetStream = new ByteArrayInputStream(jsonString.getBytes());
        JsonObject jsonObject =(JsonObject) JsonReader.jsonToMaps(targetStream,null,JsonReader.DEFAULT_MAX_PARSE_DEPTH);
        int mLen = jsonObject.getLength();
        long id = jsonObject.getId();
        assertTrue(mLen==1 && id==-1);
    }

    /**
     * test Method to test different control flows of JsonReader's jsonToJava Method
     */
    @Test
    void testJsonToJava()
    {
        String jsonString  = "{\"a\":{\"b\":\"c\"}}";
       JsonObject object = (JsonObject) JsonReader.jsonToJava(jsonString,null,
                JsonReader.DEFAULT_MAX_PARSE_DEPTH);
        assertTrue(object.type.equals("java.util.Map"));
    }
}

