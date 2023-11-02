package com.cedarsoftware.util.io;

import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author John DeRegnaucourt (jdereg@gmail.com)
 * <br>
 * Copyright (c) Cedar Software LLC
 * <br><br>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <br><br>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <br><br>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class JsonObjectTest {
    @Test
    void testNewPrimitiveWrapper() {
        assertTrue(MetaUtils.isLogicalPrimitive(Byte.class));
        assertTrue(MetaUtils.isLogicalPrimitive(Byte.TYPE));
        assertTrue(MetaUtils.isLogicalPrimitive(Short.class));
        assertTrue(MetaUtils.isLogicalPrimitive(Short.TYPE));
        assertTrue(MetaUtils.isLogicalPrimitive(Integer.class));
        assertTrue(MetaUtils.isLogicalPrimitive(Integer.TYPE));
        assertTrue(MetaUtils.isLogicalPrimitive(Long.class));
        assertTrue(MetaUtils.isLogicalPrimitive(Long.TYPE));
        assertTrue(MetaUtils.isLogicalPrimitive(Float.class));
        assertTrue(MetaUtils.isLogicalPrimitive(Float.TYPE));
        assertTrue(MetaUtils.isLogicalPrimitive(Double.class));
        assertTrue(MetaUtils.isLogicalPrimitive(Double.TYPE));
        assertTrue(MetaUtils.isLogicalPrimitive(Boolean.class));
        assertTrue(MetaUtils.isLogicalPrimitive(Boolean.TYPE));
        assertTrue(MetaUtils.isLogicalPrimitive(Character.class));
        assertTrue(MetaUtils.isLogicalPrimitive(Character.TYPE));
        // quasi-primitives (Date, String, BigInteger, BigDecimal) as defined by json-io (not true primitive wrappers)
        assertTrue(MetaUtils.isLogicalPrimitive(Date.class));
        assertTrue(MetaUtils.isLogicalPrimitive(String.class));
        assertTrue(MetaUtils.isLogicalPrimitive(BigInteger.class));
        assertTrue(MetaUtils.isLogicalPrimitive(BigDecimal.class));
        assertTrue(MetaUtils.isLogicalPrimitive(Number.class));

        assertThrows(NullPointerException.class, () -> {
            MetaUtils.isLogicalPrimitive(null);
        });
    }

    @Test
    void testGetId() {
        JsonObject jObj = new JsonObject();
        assert -1L == jObj.getId();
    }

    @Test
    void testGetPrimitiveValue() {
        JsonObject jObj = new JsonObject();
        jObj.setType("long");
        jObj.setValue(10L);
        assertEquals(jObj.getPrimitiveValue(), 10L);

        jObj.setType("phoney");
        try {
            jObj.getPrimitiveValue();
        } catch (JsonIoException e) {
            assert e.getMessage().toLowerCase().contains("invalid primitive type");
        }

        try {
            jObj.getLength();
        } catch (JsonIoException e) {
            assert e.getMessage().toLowerCase().contains("called");
            assert e.getMessage().toLowerCase().contains("non-collection");
        }

        jObj.moveCharsToMate();
    }

    /**
     * Method to pass different array types as parameters to testJsonObjectArraySize Methods
     * @return
     */
    private static Stream<Arguments> argumentsForJsonSizeTesting() {
        Integer a[] = new Integer[2];
        a[0] = 0;
        a[1] = 1;
        return Stream.of(
                Arguments.of((Object) new Integer[]{1}),
                Arguments.of((Object) new Boolean[]{true}));
    }

    /**
     * Method to validate JsonObject.size() method when array is passed inside JsonObject
     * @param o item being passed inside JsonObject
     */
    @ParameterizedTest
    @MethodSource("argumentsForJsonSizeTesting")
    void testJsonObjectArraySize(Object o) {
        Object objArr[] = (Object[]) o;
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(JsonObject.ITEMS, objArr);
        assertEquals(jsonObject.size(), objArr.length,"JsonObject's size method should return size of array " +
                "contained inside ITEMS key when ITEMS is present");
    }

    /**
     *  testMethod to check if JsonIoException is thrown while calling size method when neither items
     *  nor ref is passed inside JsonObject
     */
    @Test
    void testJsonObjectThrowsJsonIoException() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(JsonObject.ITEMS, 1);
        assertThrows(JsonIoException.class, jsonObject::size,"JsonObject's size method should throw error" +
                "when ITEMS key contains a non-array object and ITEMS key is present");

    }

    /**
     * testMethod to validate JsonObject.size() when only Ref is passed inside JsonObject and items is not present.
     */
    @Test
    void testJsonObjectRefSize() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(JsonObject.REF, 1);
        assertEquals(jsonObject.size(),0,"JsonObject's size method should return size 0 when ITEMS key is" +
                "not present and REF key is present");

    }




}
