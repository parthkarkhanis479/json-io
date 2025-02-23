package com.cedarsoftware.util.io;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
class ByteArrayTest
{
    @Test
    void testPerformance()
    {
        byte[] bytes = new byte[128 * 1024];
        Random r = new Random();
        r.nextBytes(bytes);
        String json = TestUtil.toJson(bytes);
        byte[] bytes2 = TestUtil.toObjects(json, null);

        for (int i = 0; i < bytes.length; i++)
        {
            assertEquals(bytes[i], bytes2[i]);
        }
    }
}
