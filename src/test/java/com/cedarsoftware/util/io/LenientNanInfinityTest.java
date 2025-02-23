package com.cedarsoftware.util.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author John DeRegnaucourt (jdereg@gmail.com)
 *         <br>
 *         Copyright (c) Cedar Software LLC
 *         <br><br>
 *         Licensed under the Apache License, Version 2.0 (the "License")
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *         <br><br>
 *         <a href="http://www.apache.org/licenses/LICENSE-2.0">License</a>
 *         <br><br>
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 */
class LenientNanInfinityTest
{
    public class A
    {
        private final Double doubleField;
        private final Float floatField;
        
        public A(Double doubleField, Float floatField) {
            this.doubleField = doubleField;
            this.floatField = floatField;
        }

        /**
         * @return the doubleField
         */
        public Double getDoubleField() {
            return doubleField;
        }

        /**
         * @return the floatField
         */
        public Float getFloatField() {
            return floatField;
        }
    }

    @Test
    void testFloatDoubleNormal()
    {
        float float1 = 1f;
        double double1 = 2.0;
        testFloatDouble(float1, double1);
    }

    @Test
    void testFloatDoubleNaNInf()
    {
        // Test NaN, +/-Infinity
        testFloatDouble(Float.NaN, Double.NaN);
        testFloatDouble(Float.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        testFloatDouble(Float.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        
        // Mixed.
        testFloatDouble(Float.NaN, Double.POSITIVE_INFINITY);
        testFloatDouble(Float.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        testFloatDouble(Float.NEGATIVE_INFINITY, Double.NaN);
    }

    private void testFloatDouble(float float1, double double1)
    {
        WriteOptions writeOptions = new WriteOptions().allowNanAndInfinity(true);
        A a = new A(double1, float1);

        String json = TestUtil.toJson(a, writeOptions);
        TestUtil.printLine("a = " + a);
        TestUtil.printLine("json = " + json);

        ReadOptions readOptions = new ReadOptions().allowNanAndInfinity(true).build();
        A newA = (A) TestUtil.toObjects(json, readOptions, null);
        TestUtil.printLine("newA = " + newA);
        
        Double newDoubleField = newA.getDoubleField();
        Float newFloatField = newA.getFloatField();
        
        Double doubleField = a.getDoubleField();
        Float floatField = a.getFloatField();
        assertEquals(newDoubleField, doubleField);
        assertEquals(newFloatField, floatField);
    }
}
