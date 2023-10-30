package com.cedarsoftware.util.io;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author John DeRegnaucourt (jdereg@gmail.com)
 *         <br>
 *         Copyright (c) Cedar Software LLC
 *         <br><br>
 *         Licensed under the Apache License, Version 2.0 (the "License")
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *         <br><br>
 *         http://www.apache.org/licenses/LICENSE-2.0
 *         <br><br>
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 */
class TransientTest
{
    static class Transient1
    {
        String fname;
        String lname;
        transient String fullname;
        transient Map map = new HashMap<>() {
            public Set entrySet() {
                throw new UnsupportedOperationException();
            }
        };

        void setFname(String f) { fname = f; buildFull(); }

        void setLname(String l) { lname = l; buildFull(); }

        void buildFull() { fullname = fname + " " + lname; }
    }

    @Test
    void testTraceTransientWhenListedInFieldSpecifiers()
    {
        Transient1 person = new Transient1();
        person.fname = "John";
        person.lname = "DeRegnaucourt";
        person.buildFull();

        String json = TestUtil.toJson(person);
        assert json.contains("fname");
        assert json.contains("lname");
        assert !json.contains("fullname");

        final WriteOptionsBuilder options = new WriteOptionsBuilder().withFieldSpecifier(Transient1.class, List.of("fname", "lname", "fullname"));
        json = TestUtil.toJson(person, options.build());
        assert json.contains("fname");
        assert json.contains("lname");
        assert json.contains("fullname");

        // Although the Map throws UnsupportedOperation, JsonWriter should catch this and continue
        Map<Class<?>, List<String>> specifiers = new HashMap<>();
        specifiers.put(Transient1.class, List.of("fname", "lname", "map"));
        final WriteOptionsBuilder options2 = new WriteOptionsBuilder().withFieldSpecifiersMap(specifiers);
        assertDoesNotThrow(()-> {TestUtil.toJson(person, options2.build()); });
    }

    @Test
    void testSkipsTransient()
    {
        Transient1 person = new Transient1();
        person.fname = "John";
        person.lname = "DeRegnaucourt";

        String json = TestUtil.toJson(person);
        TestUtil.printLine("json = " + json);
        assertFalse(json.contains("fullname"));

        person = (Transient1) TestUtil.toJava(json);
        assertNull(person.fullname);
    }

    @Test
    void testForceTransientSerialize() {
        Transient1 person = new Transient1();
        person.fname = "John";
        person.lname = "DeRegnaucourt";
        person.buildFull();

        WriteOptionsBuilder options = new WriteOptionsBuilder().withFieldSpecifier(Transient1.class, List.of("fname", "lname", "fullname"));
        String json = TestUtil.toJson(person, options.build());
        assert json.contains("fullname");

        person = TestUtil.toJava(json);
        assertEquals("John DeRegnaucourt", person.fullname);
    }

    static class Transient2
    {
        transient TestObject backup;
        TestObject main;
    }

    @Test
    void testTransient2()
    {
        Transient2 trans = new Transient2();
        trans.backup = new TestObject("Roswell");
        trans.main = trans.backup;

        String json = TestUtil.toJson(trans);
        TestUtil.printLine("json = " + json);
        assertFalse(json.contains("backup"));

        trans = TestUtil.toJava(json);
        assertEquals(trans.main._name, "Roswell");
    }

}