package com.cedarsoftware.util.io;

import com.cedarsoftware.util.DeepEquals;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

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
public class CollectionTests {
    @Test
    public void testCollection() {
        TestUtil.printLine("\nTestJsonReaderWriter.testCollection()");
        ManyCollections obj = new ManyCollections();
        obj.init();
        String jsonOut = TestUtil.toJson(obj);
        TestUtil.printLine(jsonOut);

        ManyCollections root = (ManyCollections) TestUtil.toJava(jsonOut);

        assertCollection(root);

        JsonWriter writer = new JsonWriter(new ByteArrayOutputStream());
        writer.write(obj);
        // TODO: Uncomment to test identity counter strategies (currently incremental + only referenced)
//        System.out.TestUtil.printLine("writer._identity = " + writer._identity)
    }

    private void assertCollection(ManyCollections root) {
        assertTrue(root._cols.length == 3);
        assertTrue(root._cols[0].getClass().equals(ArrayList.class));
        assertTrue(root._cols[1].getClass().equals(HashSet.class));
        assertTrue(root._cols[2].getClass().equals(TreeSet.class));

        Collection array = root._cols[0];
        assertTrue(array.size() == 4);
        assertTrue(array.getClass().equals(ArrayList.class));
        List alist = (List) array;
        assertTrue(alist.get(0).equals(_testDate));
        assertTrue(alist.get(1).equals("Hello"));
        assertTrue(alist.get(2).equals(new TestObject("fudge")));
        assertTrue(alist.get(3).equals(_CONST_INT));

        Collection set = root._cols[1];
        assertTrue(set.size() == 4);
        assertTrue(set.getClass().equals(HashSet.class));
        assertTrue(set.contains(Map.class));
        assertTrue(set.contains(Boolean.TRUE));
        assertTrue(set.contains(null));
        assertTrue(set.contains(_CONST_INT));

        set = root._cols[2];
        assertTrue(set.size() == 4);
        assertTrue(set.getClass().equals(TreeSet.class));
        assertTrue(set.contains(Integer.valueOf(Integer.MIN_VALUE)));
        assertTrue(set.contains(Integer.valueOf(1)));
        assertTrue(set.contains(Integer.valueOf(Integer.MAX_VALUE)));
        assertTrue(set.contains(_CONST_INT));

        assertTrue(root._strings_a.size() == 4);
        assertTrue(root._strings_a.get(0).equals("Alpha"));
        assertTrue(root._strings_a.get(1).equals("Bravo"));
        assertTrue(root._strings_a.get(2).equals("Charlie"));
        assertTrue(root._strings_a.get(3).equals("Delta"));
        assertTrue(root._strings_b.isEmpty());
        assertNull(root._strings_c);

        assertTrue(root._dates_a.size() == 4);
        assertTrue(root._dates_a.get(0).equals(new Date(0)));
        assertTrue(root._dates_a.get(1).equals(_testDate));
        assertTrue(root._dates_a.get(2).equals(new Date(Long.MAX_VALUE)));
        assertNull(root._dates_a.get(3));
        assertTrue(root._dates_b.isEmpty());
        assertNull(root._dates_c);

        assertTrue(root._classes_a.size() == 12);
        assertTrue(root._classes_a.get(0).equals(Boolean.class));
        assertTrue(root._classes_a.get(1).equals(Character.class));
        assertTrue(root._classes_a.get(2).equals(Byte.class));
        assertTrue(root._classes_a.get(3).equals(Short.class));
        assertTrue(root._classes_a.get(4).equals(Integer.class));
        assertTrue(root._classes_a.get(5).equals(Long.class));
        assertTrue(root._classes_a.get(6).equals(Float.class));
        assertTrue(root._classes_a.get(7).equals(Double.class));
        assertTrue(root._classes_a.get(8).equals(String.class));
        assertTrue(root._classes_a.get(9).equals(Date.class));
        assertNull(root._classes_a.get(10));
        assertTrue(root._classes_a.get(11).equals(Class.class));
        assertTrue(root._classes_b.isEmpty());
        assertNull(root._classes_c);

        assertTrue(root._sb_a.size() == 2);
        assertTrue(root._sb_a.get(0).toString().equals("one"));
        assertTrue(root._sb_a.get(1).toString().equals("two"));
        assertTrue(root._sb_b.isEmpty());
        assertNull(root._sb_c);

        assertTrue(root._poly_a.size() == 17);
        assertTrue(root._poly_a.get(0).equals(Boolean.TRUE));
        assertTrue(root._poly_a.get(1).equals(Character.valueOf('a')));
        assertTrue(root._poly_a.get(2).equals(Byte.valueOf((byte) 16)));
        assertTrue(root._poly_a.get(3).equals(Short.valueOf((byte) 69)));
        assertTrue(root._poly_a.get(4).equals(Integer.valueOf(714)));
        assertTrue(root._poly_a.get(5).equals(Long.valueOf(420)));
        assertTrue(root._poly_a.get(6).equals(Float.valueOf(0.4f)));
        assertTrue(root._poly_a.get(7).equals(Double.valueOf(3.14)));
        assertTrue(root._poly_a.get(8).equals("Jones'in\tfor\u0019a\ncoke"));
        assertNull(root._poly_a.get(9));
        assertTrue(root._poly_a.get(10).toString().equals("eddie"));
        assertTrue(root._poly_a.get(11).equals(_testDate));
        assertTrue(root._poly_a.get(12).equals(Long.class));

        String[] sa = (String[]) root._poly_a.get(13);
        assertTrue(sa[0].equals("beatles"));
        assertTrue(sa[1].equals("stones"));
        TestObject[] to = (TestObject[]) root._poly_a.get(14);
        assertTrue(to[0].getName().equals("flint"));
        assertTrue(to[1].getName().equals("stone"));
        Object[] arrayInCol = (Object[]) root._poly_a.get(15);
        assertTrue(arrayInCol[0].equals("fox"));
        assertTrue(arrayInCol[1].equals("wolf"));
        assertTrue(arrayInCol[2].equals("dog"));
        assertTrue(arrayInCol[3].equals("hound"));

        Set colors = (Set) root._poly_a.get(16);
        assertTrue(colors.size() == 3);
        assertTrue(colors.contains(new TestObject("red")));
        assertTrue(colors.contains(new TestObject("green")));
        assertTrue(colors.contains(new TestObject("blue")));

        assertTrue(root._strs_a.size() == 7);
        assertTrue(root._strs_a.contains("Dog"));
        assertTrue(root._strs_a.contains("Cat"));
        assertTrue(root._strs_a.contains("Cow"));
        assertTrue(root._strs_a.contains("Horse"));
        assertTrue(root._strs_a.contains("Duck"));
        assertTrue(root._strs_a.contains("Bird"));
        assertTrue(root._strs_a.contains("Goose"));
        assertTrue(root._strs_b.isEmpty());
        assertNull(root._strs_c);
        assertTrue(root._strs_d.size() == 7);
        assertTrue(root._strs_d instanceof TreeSet);

        assertTrue(root._typedCol != null);
        assertTrue(root._typedCol.size() == 6);
        assertTrue("string".equals(root._typedCol.get(0)));
        assertTrue(null == root._typedCol.get(1));
        assertTrue((new Date(19)).equals(root._typedCol.get(2)));
        assertTrue((Boolean) root._typedCol.get(3));
        assertTrue(17.76 == (Double) root._typedCol.get(4));
        assertTrue(TimeZone.getTimeZone("PST").equals(root._typedCol.get(5)));

        assertTrue(root._typedSet != null);
        assertTrue(root._typedSet.size() == 6);
        assertTrue(root._typedSet.contains("string"));
        assertTrue(root._typedCol.contains(null));
        assertTrue(root._typedCol.contains(new Date(19)));
        assertTrue(root._typedCol.contains(true));
        assertTrue(root._typedCol.contains(17.76));
        assertTrue(root._typedCol.contains(TimeZone.getTimeZone("PST")));
    }

    @Test
    public void testReconstituteCollection2() {
        ManyCollections testCol = new ManyCollections();
        testCol.init();
        String json0 = TestUtil.toJson(testCol);
        TestUtil.printLine("json0=" + json0);
        ManyCollections testCol2 = (ManyCollections) TestUtil.toJava(json0);

        String json1 = TestUtil.toJson(testCol2);
        TestUtil.printLine("json1=" + json1);

        ManyCollections testCol3 = (ManyCollections) TestUtil.toJava(json1);
        assertCollection(testCol3);// Re-written from Map of Maps and re-loaded correctly
        assertTrue(json0.equals(json1));
    }

    @Test
    public void testAlwaysShowType() {
        ManyCollections tc = new ManyCollections();
        tc.init();
        Map<String, Object> map = new LinkedHashMap<>(1);
        map.put(JsonWriter.TYPE, true);
        String json0 = TestUtil.toJson(tc, map);
        String json1 = TestUtil.toJson(tc);
        TestUtil.printLine("json0 = " + json0);
        TestUtil.printLine("json1 = " + json1);
        assertTrue(json0.length() > json1.length());
    }

    @Test
    public void testCollectionWithEmptyElement() {
        List list = new ArrayList<>();
        list.add("a");
        list.add(null);
        list.add("b");
        String json = TestUtil.toJson(list);
        List list2 = TestUtil.toJava(json);
        assertTrue(DeepEquals.deepEquals(list, list2));

        json = "{\"@type\":\"java.util.ArrayList\",\"@items\":[\"a\",{},\"b\"]}";
        list2 = TestUtil.toJava(json);
        assertTrue(list2.size() == 3);
        assertTrue(list2.get(0).equals("a"));
        assertEquals(list2.get(1).getClass(), JsonObject.class);
        assertTrue(list2.get(2).equals("b"));
    }

    @Test
    public void testCollectionWithReferences() {
        TestObject o = new TestObject("JSON");
        List list = new ArrayList<>();
        list.add(o);
        list.add(o);

        // Backward reference
        String json = TestUtil.toJson(list);
        TestUtil.printLine("json=" + json);
        List list2 = TestUtil.toJava(json);
        assertTrue(DeepEquals.deepEquals(list, list2));

        // Forward reference
        String pkg = TestObject.class.getName();
        json = "{\"@type\":\"java.util.ArrayList\",\"@items\":[{\"@ref\":3},{\"@id\":3,\"@type\":\"" + pkg + "\",\"_name\":\"JSON\",\"_other\":null}]}";
        list2 = TestUtil.toJava(json);
        assertTrue(DeepEquals.deepEquals(list, list2));
    }

    @Test
    public void testCollectionWithNonJsonPrimitives() {
        Collection col = new ArrayList<>();
        col.add(Integer.valueOf(7));
        col.add(Short.valueOf((short) 9));
        col.add(Float.valueOf(3.14f));
        String json = TestUtil.toJson(col);
        Collection col1 = TestUtil.toJava(json);
        assertTrue(col.equals(col1));
    }

    @Test
    public void testCollectionWithParameterizedTypes() {
        String json = "{\"@type\":\"" + ParameterizedCollection.class.getName() + "\", \"content\":{\"foo\":[{\"x\":1,\"y\":2},{\"x\":10,\"y\":20}],\"bar\":[{\"x\":3,\"y\":4}, {\"x\":30,\"y\":40}]}}";
        ParameterizedCollection pCol = TestUtil.toJava(json);
        Set<Point> points = pCol.getContent().get("foo");
        assertNotNull(points);
        assertEquals(2, points.size());
        points.contains(new Point(1, 2));
        points.contains(new Point(10, 20));

        points = pCol.getContent().get("bar");
        assertNotNull(points);
        assertEquals(2, points.size());
        points.contains(new Point(3, 4));
        points.contains(new Point(30, 40));

        json = "{\"@type\":\"" + ParameterizedCollection.class.getName() + "\", \"content\":{\"foo\":[],\"bar\":null}}";
        pCol = (ParameterizedCollection) TestUtil.toJava(json);
        points = pCol.getContent().get("foo");
        assertNotNull(points);
        assertEquals(0, points.size());

        points = pCol.getContent().get("bar");
        assertNull(points);

        json = "{\"@type\":\"" + ParameterizedCollection.class.getName() + "\", \"content\":{}}";
        pCol = (ParameterizedCollection) TestUtil.toJava(json);
        assertNotNull(pCol.getContent());
        assertEquals(0, pCol.getContent().size());

        json = "{\"@type\":\"" + ParameterizedCollection.class.getName() + "\", \"content\":null}";
        pCol = (ParameterizedCollection) TestUtil.toJava(json);
        assertNull(pCol.getContent());
    }

    @Test
    public void testEmptyCollections() {
        EmptyCols emptyCols;
        String className = CollectionTests.class.getName();
        String json = "{\"@type\":\"" + className + "$EmptyCols\",\"col\":{},\"list\":{},\"map\":{},\"set\":{},\"sortedSet\":{},\"sortedMap\":{}}";
        TestUtil.printLine("json = " + json);
        emptyCols = (EmptyCols) TestUtil.toJava(json);

        assertTrue(emptyCols.getCol().size() == 0);
        assertTrue(emptyCols.getCol() instanceof ArrayList);
        assertTrue(emptyCols.getList().size() == 0);
        assertTrue(emptyCols.getList() instanceof ArrayList);
        assertTrue(emptyCols.getMap().size() == 0);
        assertTrue(emptyCols.getMap() instanceof LinkedHashMap);
        assertTrue(emptyCols.getSet().size() == 0);
        assertTrue(emptyCols.getSet() instanceof LinkedHashSet);
        assertTrue(emptyCols.getSortedSet().size() == 0);
        assertTrue(emptyCols.getSortedSet() instanceof TreeSet);
        assertTrue(emptyCols.getSortedMap().size() == 0);
        assertTrue(emptyCols.getSortedMap() instanceof TreeMap);
    }

    @Test
    public void testEnumsInsideOfACollection_whenWritingAsObject_withPrivateMembersIncluded() {

        Map writeOptions = new WriteOptionsBuilder().writeEnumsAsObjects().build();

        List arrayList = new ArrayList<>();
        arrayList.add(TestEnum4.B);
        String json = TestUtil.toJson(arrayList, writeOptions);
        TestUtil.printLine(json);
        String className = CollectionTests.class.getName();
        assertEquals("{\"@type\":\"java.util.ArrayList\",\"@items\":[{\"@type\":\"" + className + "$TestEnum4\",\"name\":\"B\",\"age\":21,\"foo\":\"bar\"}]}", json);
    }

    @Test
    public void testEnumsInsideOfACollection_whenWritingAsObject_withPublicFieldsOnly() {

        Map writeOptions = new WriteOptionsBuilder().doNotWritePrivateEnumFields().build();

        List list = List.of(TestEnum4.B);
        String json = TestUtil.toJson(list, writeOptions);
        assertEquals("{\"@type\":\"java.util.ImmutableCollections$List12\",\"@items\":[{\"@type\":\"com.cedarsoftware.util.io.CollectionTests$TestEnum4\",\"name\":\"B\"}]}", json);
    }

    @Test
    public void testGenericInfoCollection() {
        String className = PointList.class.getName();
        String json = "{\"@type\":\"" + className + "\",\"points\":{\"@type\":\"java.util.ArrayList\",\"@items\":[{\"x\":1,\"y\":2}]}}";
        PointList list = (PointList) TestUtil.toJava(json);
        assertTrue(list.getPoints().size() == 1);
        Point p1 = list.getPoints().get(0);
        assertTrue(p1.getX() == 1 && p1.getY() == 2);
    }

    @Test
    public void testLocaleInCollection() {
        Locale locale = new Locale(Locale.ENGLISH.getLanguage(), Locale.US.getCountry());
        List list = new ArrayList<>();
        list.add(locale);
        String json = TestUtil.toJson(list);
        TestUtil.printLine("json=" + json);
        list = (List) TestUtil.toJava(json);
        assertTrue(list.size() == 1);
        assertTrue(list.get(0).equals(locale));
    }

    @Test
    public void testMapOfMapsCollection() {
        List stuff = new ArrayList<>();
        stuff.add("Hello");
        Object testObj = new TestObject("test object");
        stuff.add(testObj);
        stuff.add(testObj);
        stuff.add(new Date());
        String json = TestUtil.toJson(stuff);
        TestUtil.printLine("json=" + json);

        List map = (List) TestUtil.toJava(json);
        Object[] items = map.toArray();
        assertTrue(items.length == 4);
        assertTrue("Hello".equals(items[0]));
        assertTrue(items[1].equals(items[2]));

        List list = new ArrayList<>();
        list.add(new Object[]{123L, null, true, "Hello"});
        json = TestUtil.toJson(list);
        TestUtil.printLine("json=" + json);
        map = (List) TestUtil.toJava(json);
        items = map.toArray();
        assertTrue(items.length == 1);
        Object[] oa = (Object[]) items[0];
        assertTrue(oa.length == 4);
        assertTrue(oa[0].equals(123L));
        assertTrue(oa[1] == null);
        assertTrue(oa[2].equals(Boolean.TRUE));
        assertTrue("Hello".equals(oa[3]));
    }

    @Test
    public void testReconstituteCollection() {
        TestObject to = new TestObject("football");
        Collection objs = new ArrayList<>();
        Date now = new Date();
        ((ArrayList) objs).add(now);
        ((ArrayList) objs).add(123.45);
        ((ArrayList) objs).add("This is a string");
        ((ArrayList) objs).add(null);
        ((ArrayList) objs).add(to);
        ((ArrayList) objs).add(new Object[]{"dog", new String[]{"a", "b", "c"}});
        Collection two = new ArrayList<>();
        ((ArrayList) two).add(objs);
        ((ArrayList) two).add("bella");
        ((ArrayList) two).add(objs);

        String json0 = TestUtil.toJson(two);
        TestUtil.printLine("json0=" + json0);
        List map = (List) TestUtil.toJava(json0);
        map.hashCode();
        String json1 = TestUtil.toJson(map);
        TestUtil.printLine("json1=" + json1);

        List colOuter = (List) TestUtil.toJava(json1);
        assertTrue(colOuter.get(0).equals(colOuter.get(2)));
        assertTrue("bella".equals(colOuter.get(1)));
        List col1 = (List) colOuter.get(0);
        assertTrue(col1.get(0).equals(now));
        assertTrue(col1.get(1).equals(123.45));
        assertTrue("This is a string".equals(col1.get(2)));
        assertTrue(col1.get(3) == null);
        assertTrue(col1.get(4).equals(to));
        assertTrue(col1.get(5) instanceof Object[]);
        Object[] oa = (Object[]) col1.get(5);
        assertTrue("dog".equals(oa[0]));
        assertTrue(oa[1] instanceof String[]);
        String[] sa = (String[]) oa[1];
        assertTrue("a".equals(sa[0]));
        assertTrue("b".equals(sa[1]));
        assertTrue("c".equals(sa[2]));

        assertTrue(json0.equals(json1));
    }

    @Test
    public void testReconstituteEmptyCollection() {
        Collection empty = new ArrayList<>();
        String json0 = TestUtil.toJson(empty);
        TestUtil.printLine("json0=" + json0);

        List map = (List) TestUtil.toJava(json0);
        assertTrue(map != null);
        assertTrue(map.isEmpty());
        String json1 = TestUtil.toJson(map);
        TestUtil.printLine("json1=" + json1);

        assertTrue(json0.equals(json1));

        Object[] list = new Object[]{empty, empty};
        json0 = TestUtil.toJson(list);
        TestUtil.printLine("json0=" + json0);

        Object[] array = (Object[]) TestUtil.toJava(json0);
        assertTrue(array != null);
        list = array;
        assertTrue(list.length == 2);
        List e1 = (List) list[0];
        List e2 = (List) list[1];
        assertTrue(e1.isEmpty());
        assertTrue(e2.isEmpty());
    }

    @Test
    public void testUntypedCollections() {
        Object[] poly = new Object[]{"Road Runner", 16L, 3.1415d, true, false, null, 7, "Coyote", "Coyote"};
        String json = TestUtil.toJson(poly);
        TestUtil.printLine("json=" + json);
        assertTrue("[\"Road Runner\",16,3.1415,true,false,null,{\"@type\":\"int\",\"value\":7},\"Coyote\",\"Coyote\"]".equals(json));
        Collection col = new ArrayList<>();
        ((ArrayList) col).add("string");
        ((ArrayList) col).add(Long.valueOf(16));
        ((ArrayList) col).add(Double.valueOf(3.14159));
        ((ArrayList) col).add(Boolean.TRUE);
        ((ArrayList) col).add(Boolean.FALSE);
        ((ArrayList) col).add(null);
        ((ArrayList) col).add(Integer.valueOf(7));
        json = TestUtil.toJson(col);
        TestUtil.printLine("json=" + json);
        assertEquals("{\"@type\":\"java.util.ArrayList\",\"@items\":[\"string\",16,3.14159,true,false,null,{\"@type\":\"int\",\"value\":7}]}", json);
    }

    @Test
    public void testEmptyArrayList() {
        EmptyArrayList x = new EmptyArrayList();
        String json = TestUtil.toJson(x);
        TestUtil.printLine(json);
        assertTrue(json.contains("list\":[]"));

        EmptyArrayList obj = (EmptyArrayList) TestUtil.toJava(json);
        json = TestUtil.toJson(obj);
        TestUtil.printLine(json);
        assertTrue(json.contains("list\":[]"));
    }

    public static Date _testDate = new Date();
    public static Integer _CONST_INT = Integer.valueOf(36);

    private static enum TestEnum4 {
        A, B, C;

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        private int internal = 6;
        protected long age = 21;
        private String foo = "bar";
    }

    public static class EmptyCols {
        public Collection getCol() {
            return col;
        }

        public void setCol(Collection col) {
            this.col = col;
        }

        public List getList() {
            return list;
        }

        public void setList(List list) {
            this.list = list;
        }

        public Map getMap() {
            return map;
        }

        public void setMap(Map map) {
            this.map = map;
        }

        public Set getSet() {
            return set;
        }

        public void setSet(Set set) {
            this.set = set;
        }

        public SortedSet getSortedSet() {
            return sortedSet;
        }

        public void setSortedSet(SortedSet sortedSet) {
            this.sortedSet = sortedSet;
        }

        public SortedMap getSortedMap() {
            return sortedMap;
        }

        public void setSortedMap(SortedMap sortedMap) {
            this.sortedMap = sortedMap;
        }

        private Collection col = new LinkedList<>();
        private List list = new ArrayList<>();
        private Map map = new HashMap<>();
        private Set set = new HashSet();
        private SortedSet sortedSet = new TreeSet();
        private SortedMap sortedMap = new TreeMap();
    }

    public static class ParameterizedCollection {
        public Map<String, Set<Point>> getContent() {
            return content;
        }

        public void setContent(Map<String, Set<Point>> content) {
            this.content = content;
        }

        private Map<String, Set<Point>> content = new LinkedHashMap<String, Set<Point>>();
    }

    public static class PointList {
        public List<Point> getPoints() {
            return points;
        }

        public void setPoints(List<Point> points) {
            this.points = points;
        }

        private List<Point> points;
    }

    public static class EmptyArrayList {
        public ArrayList<String> getList() {
            return list;
        }

        public void setList(ArrayList<String> list) {
            this.list = list;
        }

        private ArrayList<String> list = new ArrayList<String>();
    }

    private static class ManyCollections implements Serializable {
        private void init() {
            Collection array = new ArrayList<>();
            ((ArrayList) array).add(_testDate);
            ((ArrayList) array).add("Hello");
            ((ArrayList) array).add(new TestObject("fudge"));
            ((ArrayList) array).add(_CONST_INT);

            Collection set = new HashSet();
            ((HashSet) set).add(Map.class);
            ((HashSet) set).add(Boolean.TRUE);
            ((HashSet) set).add(null);
            ((HashSet) set).add(_CONST_INT);

            Collection tree = new TreeSet();
            ((TreeSet) tree).add(Integer.valueOf(Integer.MIN_VALUE));
            ((TreeSet) tree).add(Integer.valueOf(1));
            ((TreeSet) tree).add(Integer.valueOf(Integer.MAX_VALUE));
            ((TreeSet) tree).add(_CONST_INT);

            _cols = new Collection[]{array, set, tree};

            _strings_a = new LinkedList<>();
            _strings_a.add("Alpha");
            _strings_a.add("Bravo");
            _strings_a.add("Charlie");
            _strings_a.add("Delta");
            _strings_b = new LinkedList<>();
            _strings_c = null;

            _dates_a = new ArrayList<>();
            _dates_a.add(new Date(0));
            _dates_a.add(_testDate);
            _dates_a.add(new Date(Long.MAX_VALUE));
            _dates_a.add(null);
            _dates_b = new ArrayList<>();
            _dates_c = null;

            _classes_a = new ArrayList<>();
            _classes_a.add(Boolean.class);
            _classes_a.add(Character.class);
            _classes_a.add(Byte.class);
            _classes_a.add(Short.class);
            _classes_a.add(Integer.class);
            _classes_a.add(Long.class);
            _classes_a.add(Float.class);
            _classes_a.add(Double.class);
            _classes_a.add(String.class);
            _classes_a.add(Date.class);
            _classes_a.add(null);
            _classes_a.add(Class.class);
            _classes_b = new ArrayList<>();
            _classes_c = null;

            _sb_a = new LinkedList<>();
            _sb_a.add(new StringBuffer("one"));
            _sb_a.add(new StringBuffer("two"));
            _sb_b = new LinkedList<>();
            _sb_c = null;

            _poly_a = new ArrayList<>();
            _poly_a.add(Boolean.TRUE);
            _poly_a.add(Character.valueOf('a'));
            _poly_a.add(Byte.valueOf((byte) 16));
            _poly_a.add(Short.valueOf((short) 69));
            _poly_a.add(Integer.valueOf(714));
            _poly_a.add(Long.valueOf(420));
            _poly_a.add(Float.valueOf(0.4f));
            _poly_a.add(Double.valueOf(3.14));
            _poly_a.add("Jones'in\tfor\u0019a\ncoke");
            _poly_a.add(null);
            _poly_a.add(new StringBuffer("eddie"));
            _poly_a.add(_testDate);
            _poly_a.add(Long.class);
            _poly_a.add(new String[]{"beatles", "stones"});
            _poly_a.add(new TestObject[]{new TestObject("flint"), new TestObject("stone")});
            _poly_a.add(new Object[]{"fox", "wolf", "dog", "hound"});

            Set colors = new TreeSet();
            colors.add(new TestObject("red"));
            colors.add(new TestObject("green"));
            colors.add(new TestObject("blue"));
            _poly_a.add(colors);

            _strs_a = new HashSet();
            _strs_a.add("Dog");
            _strs_a.add("Cat");
            _strs_a.add("Cow");
            _strs_a.add("Horse");
            _strs_a.add("Duck");
            _strs_a.add("Bird");
            _strs_a.add("Goose");
            _strs_b = new HashSet();
            _strs_c = null;
            _strs_d = new TreeSet();
            _strs_d.addAll(_strs_a);

            _typedCol = new ArrayList<>();
            _typedCol.add("string");
            _typedCol.add(null);
            _typedCol.add(new Date(19));
            _typedCol.add(true);
            _typedCol.add(17.76);
            _typedCol.add(TimeZone.getTimeZone("PST"));

            _typedSet = new HashSet();
            _typedSet.add("string");
            _typedSet.add(null);
            _typedSet.add(new Date(19));
            _typedSet.add(true);
            _typedSet.add(17.76);
            _typedSet.add(TimeZone.getTimeZone("PST"));

            _imm_lst_0 = List.of();
            _imm_lst_1 = List.of("One");

        }

        private Collection[] _cols;
        private List _strings_a;
        private List _strings_b;
        private List _strings_c;
        private List _dates_a;
        private List _dates_b;
        private List _dates_c;
        private List _classes_a;
        private List _classes_b;
        private List _classes_c;
        private List _sb_a;
        private List _sb_b;
        private List _sb_c;
        private List _poly_a;
        private ArrayList _typedCol;
        private Set _strs_a;
        private Set _strs_b;
        private Set _strs_c;
        private Set _strs_d;
        private HashSet _typedSet;
        private List<String> _imm_lst_0;
        private List<String> _imm_lst_1;
    }
}