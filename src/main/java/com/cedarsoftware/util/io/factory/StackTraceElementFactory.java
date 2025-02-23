package com.cedarsoftware.util.io.factory;

import com.cedarsoftware.util.io.JsonObject;
import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.MetaUtils;
import com.cedarsoftware.util.io.ReaderContext;

import java.lang.reflect.Constructor;

/**
 * Factory class to create Throwable instances.  Needed for JDK17+ as the only way to set the
 * 'detailMessage' field on a Throwable is via its constructor.
 * <p>
 *
 * @author Ken Partlow (kpartlow@gmail.com)
 * <br>
 * Copyright (c) Cedar Software LLC
 * <br><br>
 * Licensed under the Apache License, Version 2.0 (the "License");
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
public class StackTraceElementFactory implements JsonReader.ClassFactory {

    public static final String DECLARING_CLASS = "declaringClass";
    public static final String METHOD_NAME = "methodName";
    public static final String FILE_NAME = "fileName";
    public static final String LINE_NUMBER = "lineNumber";
    public static final String CLASS_LOADER_NAME = "classLoaderName";
    public static final String MODULE_NAME = "moduleName";
    public static final String MODULE_VERSION = "moduleVersion";

    private static final Constructor<?> constructor1;

    private static final Constructor<?> constructor2;

    static {
        constructor1 = MetaUtils.safelyIgnoreException(() -> StackTraceElement.class.getConstructor(String.class, String.class, String.class, int.class), null);
        constructor2 = MetaUtils.safelyIgnoreException(() -> StackTraceElement.class.getConstructor(String.class, String.class, String.class, String.class, String.class, String.class, int.class), null);
    }

    public Object newInstance(Class<?> c, JsonObject jObj, ReaderContext context) {
        String declaringClass = (String) jObj.get(DECLARING_CLASS);
        String methodName = (String) jObj.get(METHOD_NAME);
        String fileName = (String) jObj.get(FILE_NAME);
        Number lineNumber = (Number) jObj.get(LINE_NUMBER);

        String classLoaderName = (String) jObj.get(CLASS_LOADER_NAME);
        String moduleName = (String) jObj.get(MODULE_NAME);
        String moduleVersion = (String) jObj.get(MODULE_VERSION);

        if (constructor2 != null) {
            StackTraceElement element = MetaUtils.safelyIgnoreException(() -> (StackTraceElement) constructor2.newInstance(classLoaderName, moduleName, moduleVersion, declaringClass, methodName, fileName, lineNumber.intValue()), null);
            if (element != null) {
                return element;
            }
        }

        if (constructor1 != null) {
            StackTraceElement element = MetaUtils.safelyIgnoreException(() -> (StackTraceElement) constructor1.newInstance(declaringClass, methodName, fileName, lineNumber.intValue()), null);
            if (element != null) {
                return element;
            }
        }

        //  if the elements failed using reflection do the old version directly.
        return new StackTraceElement(declaringClass, methodName, fileName, lineNumber.intValue());
    }

    public boolean isObjectFinal() {
        return true;
    }
}
