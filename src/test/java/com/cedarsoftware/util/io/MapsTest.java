package com.cedarsoftware.util.io;

import java.awt.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import com.cedarsoftware.util.DeepEquals;
import com.cedarsoftware.util.ReturnType;
import com.cedarsoftware.util.io.models.ModelHoldingSingleHashMap;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author John DeRegnaucourt (jdereg@gmail.com)
 * <br>
 * Copyright (c) Cedar Software LLC
 * <br><br>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <br><br>
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">License</a>
 * <br><br>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class MapsTest
{
    @Test
    public void testMap()
    {
        ManyMaps obj = new ManyMaps();
        obj.init();
        String jsonOut = TestUtil.toJson(obj);
        ManyMaps root = TestUtil.toObjects(jsonOut, null);
        assertMap(root);
        assert DeepEquals.deepEquals(obj, root);
    }

    @Test
    void testObject_holdingMap_withStringOfStrings_andAlwaysShowingType() {
        HashMap<String, String> map = new HashMap<>();
        map.put("cn", "name");
        map.put("on", "unger");
        map.put("en", "us");

        ModelHoldingSingleHashMap model = new ModelHoldingSingleHashMap(map);
        String json = TestUtil.toJson(model, new WriteOptions().showTypeInfoAlways());
        ModelHoldingSingleHashMap actual = TestUtil.toObjects(json, new ReadOptions(), null);

        Map<String, String> deserialized = actual.getMap();
        assertThat(deserialized)
                .hasSize(3)
                .containsEntry("cn", "name")
                .containsEntry("on", "unger")
                .containsEntry("en", "us");
    }

    @Test
    void testObject_holdingMap_withStringOfStrings_andMinimalType() {
        HashMap<String, String> map = new HashMap<>();
        map.put("cn", "name");
        map.put("on", "unger");
        map.put("en", "us");

        ModelHoldingSingleHashMap model = new ModelHoldingSingleHashMap(map);
        String json = TestUtil.toJson(model, new WriteOptions());
        ModelHoldingSingleHashMap actual = TestUtil.toObjects(json, new ReadOptions(), null);

        Map<String, String> deserialized = actual.getMap();
        assertThat(deserialized)
                .hasSize(3)
                .containsEntry("cn", "name")
                .containsEntry("on", "unger")
                .containsEntry("en", "us");
    }

    private static void assertMap(ManyMaps root)
    {
        assertEquals(3, root._strings_a.size());
        assertEquals("tiger", root._strings_a.get("woods"));
        assertEquals("phil", root._strings_a.get("mickleson"));
        assertEquals("sergio", root._strings_a.get("garcia"));
        assertTrue(root._strings_b.isEmpty());
        assertNull(root._strings_c);

        assertEquals(2, root._testobjs_a.size());
        assertEquals(root._testobjs_a.get(new TestObject("one")), new TestObject("alpha"));
        assertEquals(root._testobjs_a.get(new TestObject("two")), new TestObject("bravo"));

        assertEquals(1, root._map_col.size());
        Iterator i = root._map_col.keySet().iterator();
        TestObject[] key = (TestObject[]) i.next();
        key[0]._name.equals("earth");
        key[1]._name.equals("jupiter");
        i = root._map_col.values().iterator();
        Collection list = (Collection) i.next();
        list.contains("andromeda");

        // Check value-side of Map with Object[] (special case because Object[]'s @type is never written)
        Object[] catTypes = (Object[]) root._map_col_2.get("cat");
        assertEquals("tiger", catTypes[0]);
        assertEquals("lion", catTypes[1]);
        assertEquals("cheetah", catTypes[2]);
        assertEquals("jaguar", catTypes[3]);

        assertEquals(1, root._map_col_3.size());
        i = root._map_col_3.keySet().iterator();
        Object[] key_a = (Object[]) i.next();
        key_a[0].equals("composite");
        key_a[1].equals("key");
        String value = (String) root._map_col_3.get(key_a);
        assertEquals("value", value);

        assertEquals(2, root._map_obj.size());
        assertEquals(root._map_obj.get(99), 0.123d);
        assertNull(root._map_obj.get(null));

        assertEquals(2, root._map_con.size());
        assertTrue(root._map_con instanceof ConcurrentHashMap);
        i = root._map_con.entrySet().iterator();
        while (i.hasNext())
        {
            Map.Entry e = (Map.Entry) i.next();
            TestObject key1 = (TestObject) e.getKey();
            TestObject value1 = (TestObject) e.getValue();
            if (key1.equals(new TestObject("alpha")))
            {
                assertEquals("one", value1.getName());
            }
            else if (key1.equals(new TestObject("bravo")))
            {
                assertEquals("two", value1.getName());
            }
            else
            {
                fail("Unknown Key");
            }
        }

        assertNotNull(root._typedMap);
        assertEquals(2, root._typedMap.size());
        assertEquals("alpha", root._typedMap.get("a"));
        assertEquals("bravo", root._typedMap.get("b"));
    }

    @Test
    public void testReconstituteMap()
    {
        ManyMaps testMap = new ManyMaps();
        testMap.init();
        String json0 = TestUtil.toJson(testMap);
        TestUtil.printLine("json0=" + json0);
        Map testMap2 = TestUtil.toObjects(json0, new ReadOptions().returnType(ReturnType.JSON_VALUES), null);

        String json1 = TestUtil.toJson(testMap2);
        TestUtil.printLine("json1=" + json1);

        ManyMaps testMap3 = TestUtil.toObjects(json1, null);
        assertMap(testMap3);// Re-written from Map of Maps and re-loaded correctly
        assertEquals(json0, json1);
    }

    @Test
    public void testMap2()
    {
        TestObject a = new TestObject("A");
        TestObject b = new TestObject("B");
        TestObject c = new TestObject("C");
        a._other = b;
        b._other = c;
        c._other = a;

        Map map = new HashMap<>();
        map.put(a, b);
        String json = TestUtil.toJson(map);
        TestUtil.printLine("json = " + json);
        map = TestUtil.toObjects(json, null);
        assertNotNull(map);
        assertEquals(1, map.size());
        TestObject bb = (TestObject) map.get(new TestObject("A"));
        assertEquals(bb._other, new TestObject("C"));
        TestObject aa = (TestObject) map.keySet().toArray()[0];
        assertEquals(aa._other, bb);
    }

    @Test
    public void testMap3()
    {
        Map map = new HashMap<>();
        map.put("a", "b");
        String json = TestUtil.toJson(map);
        TestUtil.printLine("json = " + json);
        map = TestUtil.toObjects(json, null);
        assertNotNull(map);
        assertEquals(1, map.size());
    }

    @Test
    public void testMapArrayKey()
    {
        MapArrayKey m = new MapArrayKey();
        m.setContent(new LinkedHashMap<>());
        Object[] arrayA = new Object[2];
        arrayA[0] = new Point(1, 2);
        arrayA[1] = new Point(10, 20);
        m.getContent().put(arrayA, "foo");
        Object[] arrayB = new Object[2];
        arrayB[0] = new Point(3, 4);
        arrayB[1] = new Point(30, 40);
        m.getContent().put(arrayB, "bar");
        String json = TestUtil.toJson(m);
        MapArrayKey x = TestUtil.toObjects(json, null);

        Iterator<Map.Entry<Object[], String>> i = x.getContent().entrySet().iterator();
        Map.Entry<Object[], String> entry = i.next();

        assertEquals("foo", entry.getValue());
        arrayA = entry.getKey();
        assertEquals(new Point(1, 2), arrayA[0]);
        assertEquals(new Point(10, 20), arrayA[1]);

        entry = i.next();
        assertEquals("bar", entry.getValue());
        arrayB = entry.getKey();
        assertEquals(new Point(3, 4), arrayB[0]);
        assertEquals(new Point(30, 40), arrayB[1]);
    }

    @Test
    public void testMapSetKey()
    {
        MapSetKey m = new MapSetKey();
        m.setContent(new LinkedHashMap<>());
        Set<Point> setA = new LinkedHashSet<>();
        setA.add(new Point(1, 2));
        setA.add(new Point(10, 20));
        m.getContent().put(setA, "foo");
        Set<Point> setB = new LinkedHashSet<>();
        setB.add(new Point(3, 4));
        setB.add(new Point(30, 40));
        m.getContent().put(setB, "bar");
        String json = TestUtil.toJson(m);
        MapSetKey x = TestUtil.toObjects(json, null);

        assertEquals("foo", x.getContent().get(setA));
        assertEquals("bar", x.getContent().get(setB));

        m = new MapSetKey();
        m.setContent(new LinkedHashMap<>());
        m.getContent().put(null, null);
        json = TestUtil.toJson(m);
        x = TestUtil.toObjects(json, null);
        assertNull(x.getContent().get(null));

        m = new MapSetKey();
        m.setContent(new LinkedHashMap<>());
        m.getContent().put(new LinkedHashSet<>(), "Fargo");
        json = TestUtil.toJson(m);
        x = TestUtil.toObjects(json, null);
        assertEquals("Fargo", x.getContent().get(new LinkedHashSet<Point>()));
    }

    @Test
    public void testMapToMapCompatibility()
    {
        String json0 = "{\"rows\":[{\"columns\":[{\"name\":\"FOO\",\"value\":\"9000\"},{\"name\":\"VON\",\"value\":\"0001-01-01\"},{\"name\":\"BAR\",\"value\":\"0001-01-01\"}]},{\"columns\":[{\"name\":\"FOO\",\"value\":\"9713\"},{\"name\":\"VON\",\"value\":\"0001-01-01\"},{\"name\":\"BAR\",\"value\":\"0001-01-01\"}]}],\"selectedRows\":\"110\"}";
        Map root = TestUtil.toObjects(json0, new ReadOptions().returnType(ReturnType.JSON_VALUES), null);
        String json1 = TestUtil.toJson(root);
        Map root2 = TestUtil.toObjects(json1, new ReadOptions().returnType(ReturnType.JSON_VALUES), null);
        assertTrue(DeepEquals.deepEquals(root, root2));

        // Will be different because @keys and @items get inserted during processing
        TestUtil.printLine("json0=" + json0);
        TestUtil.printLine("json1=" + json1);
    }

    @Test
    public void testMapWithAtType()
    {
        AssignToList atl = new AssignToList();
        String json = "{\"@id\":1,\"@type\":\"java.util.LinkedHashMap\",\"@keys\":[\"1000004947\",\"0000020985\",\"0000029443\",\"0000020994\"],\"@items\":[\"Me\",\"Fox, James\",\"Renewals, CORE\",\"Gade, Raja\"]}";
        Map assignTo = TestUtil.toObjects(json, null);
        assert assignTo instanceof LinkedHashMap;
        atl.setAssignTo(assignTo);
        json = TestUtil.toJson(atl);
        TestUtil.printLine(json);
    }

    @Test
    public void testMapWithParameterizedTypes()
    {
        String json = "{\"@type\":\"" + ParameterizedMap.class.getName() + "\", \"content\":{\"foo\":{\"one\":{\"x\":1,\"y\":2},\"two\":{\"x\":10,\"y\":20}},\"bar\":{\"ten\":{\"x\":3,\"y\":4},\"twenty\":{\"x\":30,\"y\":40}}}}";
        ParameterizedMap pCol = TestUtil.toObjects(json, null);
        Map<String, Point> points = pCol.getContent().get("foo");
        assertNotNull(points);
        assertEquals(2, points.size());
        assertEquals(new Point(1, 2), points.get("one"));
        assertEquals(new Point(10, 20), points.get("two"));

        points = pCol.getContent().get("bar");
        assertNotNull(points);
        assertEquals(2, points.size());
        assertEquals(new Point(3, 4), points.get("ten"));
        assertEquals(new Point(30, 40), points.get("twenty"));
    }

    @Test
    public void testOddMaps()
    {
        String json = "{\"@type\":\"java.util.HashMap\",\"@keys\":null,\"@items\":null}";
        Map map = TestUtil.toObjects(json, null);
        assertTrue(map instanceof HashMap);
        assertTrue(map.isEmpty());

        json = "{\"@type\":\"java.util.HashMap\"}";
        map = TestUtil.toObjects(json, null);
        assertTrue(map instanceof HashMap);
        assertTrue(map.isEmpty());
        
        final String json1 = "{\"@type\":\"java.util.HashMap\",\"@keys\":null,\"@items\":[]}";
        Exception e = assertThrows(Exception.class, () -> { TestUtil.toObjects(json1, null);});
        assert e.getMessage().toLowerCase().contains("@keys or @items");
        assert e.getMessage().toLowerCase().contains("empty");

        final String json2 = "{\"@type\":\"java.util.HashMap\",\"@keys\":[1,2],\"@items\":[true]}";
        e = assertThrows(Exception.class, () -> { TestUtil.toObjects(json2, null); });
        assert e.getMessage().toLowerCase().contains("different size");
    }

    @Test
    public void testReconstituteMapEmpty()
    {
        Map map = new LinkedHashMap<>();
        String json0 = TestUtil.toJson(map);
        TestUtil.printLine("json0=" + json0);

        map = TestUtil.toObjects(json0, new ReadOptions().returnType(ReturnType.JSON_VALUES), null);
        String json1 = TestUtil.toJson(map);
        TestUtil.printLine("json1=" + json1);

        map = TestUtil.toObjects(json1, null);
        assertTrue(map instanceof LinkedHashMap);
        assertTrue(map.isEmpty());
        assertEquals(json0, json1);
    }

    @Test
    public void testReconstituteRefMap()
    {
        Map m1 = new HashMap<>();
        Object[] root = new Object[]{m1, m1};
        String json0 = TestUtil.toJson(root);
        TestUtil.printLine("json0=" + json0);

        Object[] array = TestUtil.toObjects(json0, new ReadOptions().returnType(ReturnType.JSON_VALUES), null);
        String json1 = TestUtil.toJson(array);
        TestUtil.printLine("json1=" + json1);

        root = TestUtil.toObjects(json1, null);
        assertEquals(2, root.length);
        assertTrue(root[0] instanceof Map);
        assertTrue(((Map) root[0]).isEmpty());
        assertTrue(root[1] instanceof Map);
        assertTrue(((Map) root[1]).isEmpty());
        assertEquals(json0, json1);
    }

    @Test
    public void testReconstituteMapSimple()
    {
        SimpleMapTest smt = new SimpleMapTest();
        smt.getMap().put("a", "alpha");
        String json0 = TestUtil.toJson(smt);
        TestUtil.printLine("json0=" + json0);

        Map result = TestUtil.toObjects(json0, new ReadOptions().returnType(ReturnType.JSON_VALUES), null);
        String json1 = TestUtil.toJson(result);
        TestUtil.printLine("json1=" + json1);

        SimpleMapTest mapTest = TestUtil.toObjects(json1, null);
        assertTrue(DeepEquals.deepEquals(mapTest.getMap(), smt.getMap()));
        assertEquals(json0, json1);
    }

    @Test
    public void testMapFromUnknown()
    {
        Map map = TestUtil.toObjects("{\"a\":\"alpha\", \"b\":\"beta\"}", new ReadOptions().unknownTypeClass(ConcurrentHashMap.class), null);
        assert map instanceof ConcurrentHashMap;
        assert map.size() == 2;
        assert map.get("a").equals("alpha");
        assert map.get("b").equals("beta");

        map = TestUtil.toObjects("{\"a\":\"alpha\", \"b\":\"beta\"}", new ReadOptions().unknownTypeClass(ConcurrentSkipListMap.class), null);
        assert map instanceof ConcurrentSkipListMap;
        assert map.size() == 2;
        assert map.get("a").equals("alpha");
        assert map.get("b").equals("beta");

        map = TestUtil.toObjects("{\"a\":\"alpha\", \"b\":\"beta\"}", null);
        assert map instanceof JsonObject;// Default 'Map' type
        assert ((JsonObject) map).size() == 2;
        assert map.get("a").equals("alpha");
        assert map.get("b").equals("beta");
    }

    @Test
    public void testMapWithQuoteInKey()
    {
        Map<Serializable, Long> quoteInKeyMap = new LinkedHashMap<>(3);
        quoteInKeyMap.put(0L, 0L);
        quoteInKeyMap.put("\"one\"", 1L);
        quoteInKeyMap.put("\"two\"", 2L);
        String json = TestUtil.toJson(quoteInKeyMap);
        Map ret = TestUtil.toObjects(json, new ReadOptions(), null);
        assert ret.size() == 3;

        assert ret.get(0L).equals(0L);
        assert ret.get("\"one\"").equals(1L);
        assert ret.get("\"two\"").equals(2L);

        Map<String, Long> stringKeys = new LinkedHashMap<>(3);
        stringKeys.put("\"zero\"", 0L);
        stringKeys.put("\"one\"", 1L);
        stringKeys.put("\"two\"", 2L);
        json = TestUtil.toJson(stringKeys);
        ret = TestUtil.toObjects(json, new ReadOptions(), null);
        assert ret.size() == 3;

        assert ret.get("\"zero\"").equals(0L);
        assert ret.get("\"one\"").equals(1L);
        assert ret.get("\"two\"").equals(2L);
    }

    @Test
    public void testMapWithPrimitiveValues()
    {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.clear();
        cal.set(2017, 5, 10, 8, 1, 32);
        final Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("Byte", (byte) 79);
        map.put("Integer", 179);
        map.put("Short", (short) 179);
        map.put("Float", 179.0f);
        map.put("date", cal.getTime());
        map.put("Long", 179L);
        map.put("Double", 179.0);
        map.put("Character", 'z');
        map.put("Boolean", Boolean.FALSE);
        map.put("BigInteger", new BigInteger("55"));
        map.put("BigDecimal", new BigDecimal("3.33333"));

        final String str = TestUtil.toJson(map, new WriteOptions().isoDateTimeFormat());

        TestUtil.printLine(str + "\n");

        final Map<String, Object> map2 = (Map) JsonReader.toMaps(str);

        // for debugging
        for (Map.Entry<String, Object> entry : map2.entrySet())
        {
            TestUtil.printLine(entry.getKey() + " : " + entry.getValue() + " {" + entry.getValue().getClass().getSimpleName() + "}");
        }

        assert map2.get("Boolean") instanceof Boolean;
        assert map2.get("Boolean").equals(false);

        assert map2.get("Byte") instanceof Byte;
        assert map2.get("Byte").equals((byte)79);

        assert map2.get("Short") instanceof Short;
        assert map2.get("Short").equals((short)179);

        assert map2.get("Integer") instanceof Integer;
        assert map2.get("Integer").equals((int)179);

        assert map2.get("Long") instanceof Long;
        assert map2.get("Long").equals(179L);

        assert map2.get("Float") instanceof Float;
        assert map2.get("Float").equals(179f);

        assert map2.get("Double") instanceof Double;
        assert map2.get("Double").equals(179d);

        assert map2.get("Character") instanceof Character;
        assert map2.get("Character").equals('z');

        assert map2.get("date") instanceof Date;
        assert map2.get("date").equals(cal.getTime());

        assert map2.get("BigInteger") instanceof BigInteger;
        assert map2.get("BigInteger").equals(new BigInteger("55"));

        assert map2.get("BigDecimal") instanceof BigDecimal;
        assert map2.get("BigDecimal").equals(new BigDecimal("3.33333"));
    }

    @Test
    public void testSingletonMap()
    {
        // SingleTon Maps are simple one key, one value Maps (inner class to Collections) and must be reconstituted
        // in a special way.  Make sure that works.
        Map root1 = Collections.singletonMap("testCfgKey", "testCfgValue");
        String json = TestUtil.toJson(root1);
        Map root2 = TestUtil.toObjects(json, null);
        assert root2.get("testCfgKey").equals("testCfgValue");
        assert root1.get("testCfgKey").equals(root2.get("testCfgKey"));
    }

    public static class MapArrayKey
    {
        public Map<Object[], String> getContent()
        {
            return content;
        }

        public void setContent(Map<Object[], String> content)
        {
            this.content = content;
        }

        private Map<Object[], String> content;
    }

    public static class MapSetKey
    {
        public Map<Set<Point>, String> getContent()
        {
            return content;
        }

        public void setContent(Map<Set<Point>, String> content)
        {
            this.content = content;
        }

        private Map<Set<Point>, String> content;
    }

    public static class ParameterizedMap
    {
        public Map<String, Map<String, Point>> getContent()
        {
            return content;
        }

        public void setContent(Map<String, Map<String, Point>> content)
        {
            this.content = content;
        }

        private Map<String, Map<String, Point>> content = new LinkedHashMap<>();
    }

    public static class AssignToList
    {
        public Map getAssignTo()
        {
            return assignTo;
        }

        public void setAssignTo(Map assignTo)
        {
            this.assignTo = assignTo;
        }

        private Map assignTo;
    }

    public static class SimpleMapTest
    {
        public Map<Object, Object> getMap()
        {
            return map;
        }

        public void setMap(Map map)
        {
            this.map = map;
        }

        private Map map = new HashMap<>();
    }

    private static class ManyMaps implements Serializable
    {
        private void init()
        {
            _strings_a = new HashMap<>();
            _strings_b = new HashMap<>();
            _strings_c = null;
            _testobjs_a = new TreeMap<>();
            _map_col = new HashMap<>();
            _map_col_2 = new TreeMap<>();
            _map_col_3 = new HashMap<>();
            _map_obj = new HashMap<>();
            _map_con = new ConcurrentHashMap<>();

            _strings_a.put("woods", "tiger");
            _strings_a.put("mickleson", "phil");
            _strings_a.put("garcia", "sergio");

            _testobjs_a.put(new TestObject("one"), new TestObject("alpha"));
            _testobjs_a.put(new TestObject("two"), new TestObject("bravo"));

            List<String> l = new LinkedList<>();
            l.add("andromeda");
            _map_col.put(new TestObject[]{new TestObject("earth"), new TestObject("jupiter")}, l);
            _map_col_2.put("cat", new Object[]{"tiger", "lion", "cheetah", "jaguar"});
            _map_col_3.put(new Object[]{"composite", "key"}, "value");

            _map_obj.put(99, 0.123d);
            _map_obj.put(null, null);

            _map_con.put(new TestObject("alpha"), new TestObject("one"));
            _map_con.put(new TestObject("bravo"), new TestObject("two"));

            _typedMap = new TreeMap<>();
            _typedMap.put("a", "alpha");
            _typedMap.put("b", "bravo");
        }

        private Map<String, Object> _strings_a;
        private Map<String, Object> _strings_b;
        private Map<String, Object> _strings_c;
        private Map _testobjs_a;
        private Map _map_col;
        private Map _map_col_2;
        private Map _map_col_3;
        private Map _map_obj;
        private Map _map_con;
        private Map<String, Object> _typedMap;
    }
}
