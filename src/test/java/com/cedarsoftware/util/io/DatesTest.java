package com.cedarsoftware.util.io;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import com.cedarsoftware.util.ReturnType;
import com.cedarsoftware.util.io.factory.DateFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
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
class DatesTest
{
    private static void compareTimePortion(Calendar exp, Calendar act)
    {
        assertEquals(exp.get(Calendar.HOUR_OF_DAY), act.get(Calendar.HOUR_OF_DAY));
        assertEquals(exp.get(Calendar.MINUTE), act.get(Calendar.MINUTE));
        assertEquals(exp.get(Calendar.SECOND), act.get(Calendar.SECOND));
        assertEquals(exp.get(Calendar.MILLISECOND), act.get(Calendar.MILLISECOND));
    }

    private static void compareDatePortion(Calendar exp, Calendar act)
    {
        assertEquals(exp.get(Calendar.YEAR), act.get(Calendar.YEAR));
        assertEquals(exp.get(Calendar.MONTH), act.get(Calendar.MONTH));
        assertEquals(exp.get(Calendar.DAY_OF_MONTH), act.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testAssignDateFromEmptyString()
    {
        String thisClass = TestDateField.class.getName();
        String json = "{\"@type\":\"" + thisClass + "\",\"fromString\":\"\"}";
        TestDateField tdf = TestUtil.toObjects(json, null);
        assertNull(tdf.getFromString());

        Map jObj = TestUtil.toObjects(json, new ReadOptions().returnType(ReturnType.JSON_VALUES), null);
        assertNull(jObj.get("fromString"));

        json = "{\"@type\":\"" + thisClass + "\",\"fromString\":null,\"dates\":[\"\"]}";
        tdf = TestUtil.toObjects(json, null);
        assertNull(tdf.getDates()[0]);

        jObj = TestUtil.toObjects(json, new ReadOptions().returnType(ReturnType.JSON_VALUES), null);
        json = TestUtil.toJson(jObj);
        tdf = TestUtil.toObjects(json, null);
        assertNull(tdf.getDates()[0]);

        json = "{\"@type\":\"" + thisClass + "\",\"fromString\":1391875635941}";
        tdf = TestUtil.toObjects(json, null);
        assertEquals(new Date(1391875635941L), tdf.getFromString());
    }

    @Test
    public void testDateParse()
    {
        String json = "{\"@type\":\"date\",\"value\":\"2014 July 9\"}";
        Date date = TestUtil.toObjects(json, null);
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.setTime(date);
        assertEquals(2014, cal.get(Calendar.YEAR));
        assertEquals(6, cal.get(Calendar.MONTH));
        assertEquals(9, cal.get(Calendar.DAY_OF_MONTH));

        json = "{\"@type\":\"date\",\"value\":\"2014 Juggler 9\"}";

        try
        {
            TestUtil.toObjects(json, null);
            fail();
        }
        catch (Exception ignore)
        { }
    }

    @Test
    public void testDate()
    {
        TestDate test = new TestDate();
        String json = TestUtil.toJson(test);
        TestUtil.printLine("json = " + json);
        TestDate that = TestUtil.toObjects(json, null);

        assertEquals(that._arrayElement, new Date(-1));
        assertEquals(that._polyRefTarget, new Date(71));
        assertEquals(that._polyRef, new Date(71));
        assertEquals(that._polyNotRef, new Date(71));
        assertNotSame(that._polyRef, that._polyRefTarget);// not same because Date's are treated as immutable primitives
        assertNotSame(that._polyNotRef, that._polyRef);

        assertEquals(6, that._typeArray.length);
        assertNotSame(that._typeArray[0], that._arrayElement);
        assertTrue(that._typeArray[1] instanceof Date);
        assertTrue(that._typeArray[1] instanceof Date);
        assertEquals(that._typeArray[1], new Date(69));
        assertEquals(6, that._objArray.length);
        assertNotSame(that._objArray[0], that._arrayElement);
        assertTrue(that._objArray[1] instanceof Date);
        assertEquals(that._objArray[1], new Date(69));
        assertTrue(that._polyRefTarget instanceof Date);
        assertTrue(that._polyNotRef instanceof Date);
        assertEquals(that._typeArray[1], that._typeArray[5]);
        assertNotSame(that._typeArray[1], that._typeArray[5]);
        assertEquals(that._objArray[1], that._objArray[5]);
        assertNotSame(that._objArray[1], that._objArray[5]);
        assertEquals(that._typeArray[1], that._objArray[1]);
        assertNotSame(that._typeArray[1], that._objArray[1]);
        assertEquals(that._typeArray[5], that._objArray[5]);
        assertNotSame(that._typeArray[5], that._objArray[5]);

        assertNotSame(that._objArray[2], that._typeArray[2]);
        assertEquals(that._objArray[2], new Date(75));

        assertNull(that._null);
        assertNull(that._typeArray[3]);
        assertNull(that._typeArray[4]);
        assertNull(that._objArray[3]);
        assertNull(that._objArray[4]);

        assertEquals(that._min, new Date(Long.MIN_VALUE));
        assertEquals(that._max, new Date(Long.MAX_VALUE));
    }

    @Test
    public void testCustomDateFormat()
    {
        DateTest dt = new DateTest();
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(1965, Calendar.DECEMBER, 17, 14, 01, 30);
        dt.setBirthDay(c.getTime());
        c.clear();
        c.set(1991, Calendar.OCTOBER, 5, 1, 1, 30);
        dt.setAnniversary(new java.sql.Date(c.getTime().getTime()));
        c.clear();
        c.set(2013, Calendar.DECEMBER, 25, 1, 2, 34);
        dt.setChristmas(new java.sql.Date(c.getTime().getTime()));

        // Custom writer that only outputs ISO date portion
        String json = TestUtil.toJson(dt, new WriteOptions().isoDateFormat());
        TestUtil.printLine("json = " + json);

        // Read it back in
        DateTest readDt = TestUtil.toObjects(json, null);

        Calendar exp = Calendar.getInstance();
        exp.setTime(dt.getBirthDay());
        Calendar act = Calendar.getInstance();
        act.setTime(readDt.getBirthDay());
        compareDatePortion(exp, act);

        exp.setTime(dt.getAnniversary());
        act.setTime(readDt.getAnniversary());
        compareDatePortion(exp, act);

        exp.setTime(dt.getChristmas());
        act.setTime(readDt.getChristmas());
        compareDatePortion(exp, act);

        // Custom writer that outputs date and time portion in ISO format
        json = TestUtil.toJson(dt, new WriteOptions().isoDateTimeFormat());
        TestUtil.printLine("json = " + json);

        // Read it back in
        readDt = TestUtil.toObjects(json, null);

        exp.setTime(dt.getBirthDay());
        act.setTime(readDt.getBirthDay());
        compareDatePortion(exp, act);
        compareTimePortion(exp, act);

        exp.setTime(dt.getAnniversary());
        act.setTime(readDt.getAnniversary());
        compareDatePortion(exp, act);
        compareTimePortion(exp, act);

        exp.setTime(dt.getChristmas());
        act.setTime(readDt.getChristmas());
        compareDatePortion(exp, act);
        compareTimePortion(exp, act);

        // Write out dates as long (standard behavior)
        json = TestUtil.toJson(dt);
        readDt = TestUtil.toObjects(json, null);

        exp.setTime(dt.getBirthDay());
        act.setTime(readDt.getBirthDay());
        compareDatePortion(exp, act);
        compareTimePortion(exp, act);

        exp.setTime(dt.getAnniversary());
        act.setTime(readDt.getAnniversary());
        compareDatePortion(exp, act);
        compareTimePortion(exp, act);

        exp.setTime(dt.getChristmas());
        act.setTime(readDt.getChristmas());
        compareDatePortion(exp, act);
        compareTimePortion(exp, act);

        // Version with milliseconds
        json = TestUtil.toJson(dt, new WriteOptions().dateTimeFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        readDt = TestUtil.toObjects(json, null);

        exp.setTime(dt.getBirthDay());
        act.setTime(readDt.getBirthDay());
        compareDatePortion(exp, act);
        compareTimePortion(exp, act);

        exp.setTime(dt.getAnniversary());
        act.setTime(readDt.getAnniversary());
        compareDatePortion(exp, act);
        compareTimePortion(exp, act);

        exp.setTime(dt.getChristmas());
        act.setTime(readDt.getChristmas());
        compareDatePortion(exp, act);
        compareTimePortion(exp, act);

        json = TestUtil.toJson(dt, new WriteOptions().dateTimeFormat("MM/dd/yyyy HH:mm:ss"));
        readDt = TestUtil.toObjects(json, null);

        exp.setTime(dt.getBirthDay());
        act.setTime(readDt.getBirthDay());
        compareDatePortion(exp, act);
        compareTimePortion(exp, act);

        exp.setTime(dt.getAnniversary());
        act.setTime(readDt.getAnniversary());
        compareDatePortion(exp, act);
        compareTimePortion(exp, act);

        exp.setTime(dt.getChristmas());
        act.setTime(readDt.getChristmas());
        compareDatePortion(exp, act);
        compareTimePortion(exp, act);

        // Nov 15, 2013 format
        json = TestUtil.toJson(dt, new WriteOptions().dateTimeFormat("MMM dd, yyyy HH:mm.ss"));
        TestUtil.printLine("json = " + json);
        readDt = TestUtil.toObjects(json, null);

        exp.setTime(dt.getBirthDay());
        act.setTime(readDt.getBirthDay());
        compareDatePortion(exp, act);
        compareTimePortion(exp, act);

        exp.setTime(dt.getAnniversary());
        act.setTime(readDt.getAnniversary());
        compareDatePortion(exp, act);
        compareTimePortion(exp, act);

        exp.setTime(dt.getChristmas());
        act.setTime(readDt.getChristmas());
        compareDatePortion(exp, act);
        compareTimePortion(exp, act);

        // 15 Nov 2013 format
        json = TestUtil.toJson(dt, new WriteOptions().dateTimeFormat("dd MMM yyyy HH:mm.ss"));
        TestUtil.printLine("json = " + json);
        readDt = TestUtil.toObjects(json, null);

        exp.setTime(dt.getBirthDay());
        act.setTime(readDt.getBirthDay());
        compareDatePortion(exp, act);
        compareTimePortion(exp, act);

        exp.setTime(dt.getAnniversary());
        act.setTime(readDt.getAnniversary());
        compareDatePortion(exp, act);
        compareTimePortion(exp, act);

        exp.setTime(dt.getChristmas());
        act.setTime(readDt.getChristmas());
        compareDatePortion(exp, act);
        compareTimePortion(exp, act);
    }

    /**
     * Instantiate off of each others JSON String, proving Date for long substitution works.  This will work on any
     * field that is of type Date or Long.  It will not work when the Dates are inside a Collection, for example.
     * <p/>
     * This substitution trick allows Date fields to be converted to long in order to save memory 16 bytes of memory
     * per date.  (Date's are more than 8 bytes, longs are 8).
     */
    @Test
    public void testDateLongSubstitution()
    {
        long now = System.currentTimeMillis();
        DateTrick d = new DateTrick();
        d._userDate = new Date(now);
        LongTrick l = new LongTrick();
        l._userDate = now;
        String jsonOut1 = TestUtil.toJson(d);
        TestUtil.printLine(jsonOut1);
        String jsonOut2 = TestUtil.toJson(l);
        TestUtil.printLine(jsonOut2);
        jsonOut1 = jsonOut1.replace("$Date", "$Long");
        jsonOut2 = jsonOut2.replace("$Long", "$Date");
        l = TestUtil.toObjects(jsonOut1, null);
        d = TestUtil.toObjects(jsonOut2, null);
        assert d._userDate.getTime() == l._userDate;
    }

    @Test
    public void testDateMissingValue()
    {
        try
        {
            TestUtil.toObjects("[{\"@type\":\"date\"}]", null);
           fail();
        }
        catch (Exception ignored)
        { }
    }

    @Test
    public void testDates()
    {
        // As root
        long now = System.currentTimeMillis();
        Date utilDate = new Date(now);
        java.sql.Date sqlDate = new java.sql.Date(now);
        Timestamp sqlTimestamp = new Timestamp(now);

        String json = TestUtil.toJson(utilDate);
        TestUtil.printLine(json);
        Date checkDate = TestUtil.toObjects(json, null);
        assertNotNull(checkDate);
        assertEquals(checkDate, utilDate);

        json = TestUtil.toJson(sqlDate);
        TestUtil.printLine(json);
        java.sql.Date checkSqlDate = TestUtil.toObjects(json, null);
        assertNotNull(checkSqlDate);
        assertEquals(checkSqlDate, sqlDate);

        json = TestUtil.toJson(sqlTimestamp);
        TestUtil.printLine(json);
        Timestamp checkSqlTimestamp = TestUtil.toObjects(json, null);
        assertNotNull(checkSqlTimestamp);
        assertEquals(checkSqlTimestamp, sqlTimestamp);

        // In Object[]
        Object[] dates = new Object[]{utilDate, sqlDate, sqlTimestamp};
        json = TestUtil.toJson(dates);
        TestUtil.printLine(json);
        Object[] checkDates = TestUtil.toObjects(json, null);
        assertEquals(3, checkDates.length);
        assertTrue(checkDates[0] instanceof Date);
        assertTrue(checkDates[1] instanceof java.sql.Date);
        assertTrue(checkDates[2] instanceof Timestamp);
        assertEquals(checkDates[0], utilDate);
        assertEquals(checkDates[1], sqlDate);
        assertEquals(checkDates[2], sqlTimestamp);

        // In Typed[]
        Date[] utilDates = new Date[]{utilDate};
        json = TestUtil.toJson(utilDates);
        TestUtil.printLine(json);
        Date[] checkUtilDates = TestUtil.toObjects(json, null);
        assertEquals(1, checkUtilDates.length);
        assertNotNull(checkUtilDates[0]);
        assertEquals(checkUtilDates[0], utilDate);

        java.sql.Date[] sqlDates = new java.sql.Date[]{sqlDate};
        json = TestUtil.toJson(sqlDates);
        TestUtil.printLine(json);
        java.sql.Date[] checkSqlDates = TestUtil.toObjects(json, null);
        assertEquals(1, checkSqlDates.length);
        assertNotNull(checkSqlDates[0]);
        assertEquals(checkSqlDates[0], sqlDate);

        Timestamp[] sqlTimestamps = new Timestamp[]{sqlTimestamp};
        json = TestUtil.toJson(sqlTimestamps);
        TestUtil.printLine(json);
        Timestamp[] checkTimestamps = TestUtil.toObjects(json, null);
        assertEquals(1, checkTimestamps.length);
        assertNotNull(checkTimestamps[0]);
        assertEquals(checkTimestamps[0], sqlTimestamp);

        // as Object field
        ObjectDateField dateField = new ObjectDateField(utilDate);
        json = TestUtil.toJson(dateField);
        TestUtil.printLine(json);
        ObjectDateField readDateField = TestUtil.toObjects(json, null);
        assertTrue(readDateField.date instanceof Date);
        assertEquals(readDateField.date, utilDate);

        dateField = new ObjectDateField(sqlDate);
        json = TestUtil.toJson(dateField);
        TestUtil.printLine(json);
        readDateField = TestUtil.toObjects(json, null);
        assertTrue(readDateField.date instanceof java.sql.Date);
        assertEquals(readDateField.date, sqlDate);

        dateField = new ObjectDateField(sqlTimestamp);
        json = TestUtil.toJson(dateField);
        TestUtil.printLine(json);
        readDateField = TestUtil.toObjects(json, null);
        assertTrue(readDateField.date instanceof Timestamp);
        assertEquals(readDateField.date, sqlTimestamp);

        // as Typed field
        DateField typedDateField = new DateField(utilDate);
        json = TestUtil.toJson(typedDateField);
        TestUtil.printLine(json);
        DateField readTypeDateField = TestUtil.toObjects(json, null);
        assertNotNull(readTypeDateField.date);
        assertEquals(readTypeDateField.date, utilDate);

        SqlDateField sqlDateField = new SqlDateField(sqlDate);
        json = TestUtil.toJson(sqlDateField);
        TestUtil.printLine(json);
        SqlDateField readSqlDateField = TestUtil.toObjects(json, null);
        assertNotNull(readSqlDateField.date);
        assertEquals(readSqlDateField.date, sqlDate);

        TimestampField timestampField = new TimestampField(sqlTimestamp);
        json = TestUtil.toJson(timestampField);
        TestUtil.printLine(json);
        TimestampField readTimestampField = TestUtil.toObjects(json, null);
        assertNotNull(readTimestampField.date);
        assertEquals(readTimestampField.date, sqlTimestamp);
    }

    @Test
    public void testSqlDate()
    {
        long now = System.currentTimeMillis();
        Date[] dates = new Date[]{new Date(now), new java.sql.Date(now), new Timestamp(now)};
        String json = TestUtil.toJson(dates);
        TestUtil.printLine("json=" + json);
        Date[] dates2 = TestUtil.toObjects(json, null);
        assertEquals(3, dates2.length);
        assertEquals(dates2[0], new Date(now));
        assertEquals(dates2[1], new java.sql.Date(now));
        Timestamp stamp = (Timestamp) dates2[2];
        assertEquals(stamp.getTime(), dates[0].getTime());
        assertEquals(stamp.getTime(), now);
    }

    @Test
    public void testNullDateHandling()
    {
        DateField dateField = new DateField(new Date());
        dateField.date = null;
        String json = TestUtil.toJson(dateField);
        DateField df = TestUtil.toObjects(json, null);
        assert df.date == null;
    }

    @Test
    public void testDateReaderNullHandling()
    {
        DateFactory factory = new DateFactory();
        try
        {
            JsonObject object = new JsonObject();
            object.setValue(null);
            factory.newInstance(Date.class, object, null);
           fail();
        }
        catch (JsonIoException e)
        {
            assert e.getMessage().toLowerCase().contains("must be specified");
        }

    }

    @Test
    public void testJavaDefaultTimeFormatParsing()
    {
        Date now = new Date();
        String nowStr = now.toString();
        DateFactory factory = new DateFactory();
        JsonObject object = new JsonObject();
        object.setValue(nowStr);
        Date now2 = (Date) factory.newInstance(Date.class, object, null);
        assert nowStr.equals(now2.toString());
    }

    @Test
    public void testDateWithTimeZoneOffsetParsing()
    {
        String date = "9 July 1930 11:02-05:00";
        DateFactory factory = new DateFactory();
        JsonObject object = new JsonObject();
        object.setValue(date);

        Date then = (Date) factory.newInstance(Date.class, object, null);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(1930, 6, 9, 16, 2, 0);
        assert then.equals(cal.getTime());
    }

    @Test
    public void testDayRemainderRemoval()
    {
        String date = "sat 6 Jun 2015";
        DateFactory factory = new DateFactory();
        JsonObject object = new JsonObject();
        object.setValue(date);
        Date date1 = (Date) factory.newInstance(Date.class, object, null);

        Calendar c = Calendar.getInstance();
        c.setTime(date1);
        assert c.get(Calendar.YEAR) == 2015;
        assert c.get(Calendar.MONTH) == 5;
        assert c.get(Calendar.DAY_OF_MONTH) == 6;
    }

    @Test
    public void testBadDayOfWeek()
    {
        String date = "crunchy 6 Jun 2015";
        DateFactory factory = new DateFactory();
        try
        {
            JsonObject object = new JsonObject();
            object.setValue(date);
            factory.newInstance(Date.class, object, null);
           fail();
        }
        catch (JsonIoException e)
        {
            assert e.getMessage().toLowerCase().contains("crunchy");
        }

    }

    @Test
    public void testBadMonth()
    {
        String date = "2015/13/1";
        DateFactory factory = new DateFactory();
        try
        {
            JsonObject object = new JsonObject();
            object.setValue(date);
            factory.newInstance(Date.class, object, null);
           fail();
        }
        catch (JsonIoException e)
        {
            assert e.getMessage().toLowerCase().contains("between 1 and 12");
        }

    }

    @Test
    public void testBadDay()
    {
        String date = "2015/9/34";
        DateFactory factory = new DateFactory();
        try
        {
            JsonObject object = new JsonObject();
            object.setValue(date);
            factory.newInstance(Date.class, object, null);
           fail();
        }
        catch (JsonIoException e)
        {
            assert e.getMessage().toLowerCase().contains("between 1 and 31");
        }

    }

    @Test
    public void testBadHour()
    {
        String date = "2015/9/30 25:30";
        DateFactory factory = new DateFactory();
        try
        {
            JsonObject object = new JsonObject();
            object.setValue(date);
            factory.newInstance(Date.class, object, null);
           fail();
        }
        catch (JsonIoException e)
        {
            assert e.getMessage().toLowerCase().contains("between 0 and 23");
        }

    }

    @Test
    public void testBadMinute()
    {
        String date = "2015/9/30 20:65";
        DateFactory factory = new DateFactory();
        try
        {
            JsonObject object = new JsonObject();
            object.setValue(date);
            factory.newInstance(Date.class, object, null);
           fail();
        }
        catch (JsonIoException e)
        {
            assert e.getMessage().toLowerCase().contains("between 0 and 59");
        }

    }

    @Test
    public void testBadSecond()
    {
        String date = "2015/9/30 20:55:70";
        DateFactory factory = new DateFactory();
        try
        {
            JsonObject object = new JsonObject();
            object.setValue(date);
            factory.newInstance(Date.class, object, null);
           fail();
        }
        catch (JsonIoException e)
        {
            assert e.getMessage().toLowerCase().contains("between 0 and 59");
        }

    }

    @Test
    public void testZForGMT()
    {
        String date = "2015/9/30 20:55Z";
        DateFactory factory = new DateFactory();
        JsonObject object = new JsonObject();
        object.setValue(date);
        Date date1 = (Date) factory.newInstance(Date.class, object, null);
        assertNotNull(date1);
    }

    public static class DateTest
    {
        public Date getBirthDay()
        {
            return birthDay;
        }

        public void setBirthDay(Date birthDay)
        {
            this.birthDay = birthDay;
        }

        public Date getAnniversary()
        {
            return anniversary;
        }

        public void setAnniversary(Date anniversary)
        {
            this.anniversary = anniversary;
        }

        public java.sql.Date getChristmas()
        {
            return christmas;
        }

        public void setChristmas(java.sql.Date christmas)
        {
            this.christmas = christmas;
        }

        private Date birthDay;
        private Date anniversary;
        private java.sql.Date christmas;
    }

    public static class TestDateField
    {
        public Date getFromString()
        {
            return fromString;
        }

        public void setFromString(Date fromString)
        {
            this.fromString = fromString;
        }

        public Date[] getDates()
        {
            return dates;
        }

        public void setDates(Date[] dates)
        {
            this.dates = dates;
        }

        private Date fromString;
        private Date[] dates;
    }

    public static class DateTrick
    {
        private Date _userDate;
    }

    public static class LongTrick
    {
        private long _userDate;
    }

    public static class ObjectDateField
    {
        private ObjectDateField(Object date)
        {
            this.date = date;
        }

        private final Object date;
    }

    private static class DateField
    {
        private DateField(Date date)
        {
            this.date = date;
        }

        private Date date;
    }

    public static class SqlDateField
    {
        private SqlDateField(java.sql.Date date)
        {
            this.date = date;
        }

        private final java.sql.Date date;
    }

    public static class TimestampField
    {
        private TimestampField(Timestamp date)
        {
            this.date = date;
        }

        private final Timestamp date;
    }

    public static class TestDate implements Serializable
    {
        private TestDate()
        {
            _arrayElement = new Date(-1);
            _polyRefTarget = new Date(71);
            _polyRef = _polyRefTarget;
            _polyNotRef = new Date(71);
            Date local = new Date(75);
            _null = null;
            _typeArray = new Date[]{_arrayElement, new Date(69), local, _null, null, new Date(69)};
            _objArray = new Object[]{_arrayElement, new Date(69), local, _null, null, new Date(69)};
            _min = new Date(Long.MIN_VALUE);
            _max = new Date(Long.MAX_VALUE);
        }

        private final Date _arrayElement;
        private final Date[] _typeArray;
        private final Object[] _objArray;
        private final Object _polyRefTarget;
        private final Object _polyRef;
        private final Object _polyNotRef;
        private final Date _min;
        private final Date _max;
        private final Date _null;
    }
}
