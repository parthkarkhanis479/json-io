package com.cedarsoftware.util.io.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.cedarsoftware.util.io.JsonObject;
import com.cedarsoftware.util.io.JsonReader;

public abstract class HandWrittenDateFactoryTests<T> {

    protected abstract JsonReader.ClassFactory createFactory();

    protected abstract Class<T> getClassForFactory();


    @SuppressWarnings("unchecked")
    @Test
    void newInstance_handWrittenDate_withNoZone() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.setValue("2011-12-03T10:15:30");

        JsonReader.ClassFactory factory = createFactory();

        T dt = (T) factory.newInstance(getClassForFactory(), jsonObject, null);

        assert_handWrittenDate_withNoZone(dt);
    }

    protected abstract void assert_handWrittenDate_withNoZone(T dt);


    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @ValueSource(strings = {
            "2011-2-3",
            "2011-02-03",
            "02/03/2011",
            "2/3/2011",
            "2011/2/3",
            "2011/02/03",
            "02.3.2011",
            "2.03.2011",
            "2011.02.03",
            "sat 3 Feb 2011",
            "sun 3 Feb 2011",
            "3 February 2011",
            "03 February 2011",
            "FEB 03, 2011"
    })
    void newInstance_handWrittenDate_withNoTime_usesFactoryZone_andZeroTime(String dateWithNoTime) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.setValue(dateWithNoTime);

        JsonReader.ClassFactory factory = createFactory();

        T dt = (T) factory.newInstance(getClassForFactory(), jsonObject, null);

        assert_handWrittenDate_withNoTime(dt);
    }

    protected abstract void assert_handWrittenDate_withNoTime(T dt);

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @ValueSource(strings = {
            "2011-2-3 08:09:03",
            "2011-02-03T08:09:03",
            "FEB 03, 2011 08:09.03",
            "02/03/2011 08:09:03",
            "2/3/2011T08:09:03",
            "2011/2/3 08:09:03",
            "2011/02/03T08:09:03",
            "02.3.2011 08:09:03",
            "2.03.2011T08:09:03",
            "2011.02.03 08:09:03"
    })
    void newInstance_handWrittenDate_withTime(String dateWithNoTime) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.setValue(dateWithNoTime);

        JsonReader.ClassFactory factory = createFactory();

        T dt = (T) factory.newInstance(getClassForFactory(), jsonObject, null);

        assert_handWrittenDate_withTime(dt);
    }

    protected abstract void assert_handWrittenDate_withTime(T dt);

    @SuppressWarnings("unchecked")
    @Test
    void newInstance_handWrittenDate_includingMilliseconds() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.setValue("2011-12-03T10:15:30.050");

        JsonReader.ClassFactory factory = createFactory();

        T dt = (T) factory.newInstance(getClassForFactory(), jsonObject, null);

        assert_handWrittenDate_withMilliseconds(dt);
    }

    protected abstract void assert_handWrittenDate_withMilliseconds(T dt);

}
