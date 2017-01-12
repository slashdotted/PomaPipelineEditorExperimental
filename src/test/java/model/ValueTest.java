package model;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.logging.Logger;

/**
 * Created by felipe on 14/05/16.
 */
public class ValueTest {

    private static Value<Long> longValue;
    private static Value<String> stringValue;
    private static Value<Boolean> booleanValue;
    private static Value<Double> doubleValue;
    private static Logger logger = Logger.getLogger("Value Test");


    @Before
    public void setUp() {
        //logger.info("Values initialized for tests");
        longValue = new Value<>("longValue", "", new Long(0), 0L, true);
        stringValue = new Value<>("stringValue", "", new String("0"), "0", true);
        booleanValue = new Value<>("booleanValue", "", new Boolean("0"), false, true);
        doubleValue = new Value<>("doubleValue", "", new Double("0"), 0.0, true);
    }

    private static void initializeNoDefaultValue() {
        longValue = new Value<>("longValue","",  new Long(0), null, true);
        stringValue = new Value<>("stringValue", "", new String("0"), null, true);
        booleanValue = new Value<>("booleanValue", "", new Boolean("0"), null, true);
        doubleValue = new Value<>("doubleValue", "", new Double("0"), null, true);
    }


    @Test
    public void testGetValue() throws Exception {
        assertEquals(0, longValue.getValue().longValue());
        assertEquals("0", stringValue.getValue());
        assertEquals(false, booleanValue.getValue());
        assertEquals(0.0, doubleValue.getValue(), 0.000001);
    }

    @Test
    public void testGetName() throws Exception {

        assertEquals("longValue", longValue.getName());
        assertEquals("stringValue", stringValue.getName());
        assertEquals("booleanValue", booleanValue.getName());
        assertEquals("doubleValue", doubleValue.getName());
    }

    @Test
    public void testGetDefaultValue() throws Exception {
        assertEquals(0L, longValue.getDefaultValue().longValue());
        assertEquals("0", stringValue.getDefaultValue());
        assertEquals(false, booleanValue.getDefaultValue());
        assertEquals(0.0, doubleValue.getDefaultValue().doubleValue(), 0.000001);
    }

    @Test
    public void testUpdateValue() throws Exception {
        initializeNoDefaultValue();
        /*// Long
        assertEquals(longValue.updateValue("1234"), "1234");
        assertEquals(longValue.updateValue("notALong"), "0");

        // String
        assertEquals(stringValue.updateValue("myString"), "myString" );

        //Boolean
        assertEquals(booleanValue.updateValue("true"), true);
        assertEquals(booleanValue.updateValue("false"), false);
        assertEquals(booleanValue.updateValue("0"), false);
        assertEquals(booleanValue.updateValue("1"), true);
        assertEquals(booleanValue.updateValue("notABoolean"), false);

        // Double
        assertEquals(doubleValue.updateValue("1234.0"), 1234.0);
        assertEquals(doubleValue.updateValue("notADouble"), 0.0);*/

    }

    @Test
    public void testToString() throws Exception {
       /* assertEquals(
                "Value{name='longValue' type= 'Long' value= '0' defaultValue = '0' mandatory = 'true' valid = 'true'}",
                longValue.toString());
        assertEquals(
                "Value{name='booleanValue' type= 'Boolean' value= 'false' defaultValue = 'false' mandatory = 'true' valid = 'true'}",
                booleanValue.toString*/
    }

    @Test
    public void testSetValue() throws Exception {
        longValue.setValue(1234L);
        booleanValue.setValue(true);
        stringValue.setValue("myString");
        doubleValue.setValue(1234.0);

        assertEquals(1234L, longValue.getValue().longValue());
        assertEquals("myString", stringValue.getValue());
        assertEquals(true, booleanValue.getValue());
        assertEquals(1234.0, doubleValue.getValue(), 0.000001);
    }

    @Test
    public void testGetType() throws Exception {
        assertEquals("Long", longValue.getType());
        assertEquals("String", stringValue.getType());
        assertEquals("Boolean", booleanValue.getType());
        assertEquals("Double", doubleValue.getType());

    }

    @Test
    public void testGetClassName() throws Exception {
        assertEquals("java.lang.Long", longValue.getClassName());
        assertEquals("java.lang.String", stringValue.getClassName());
        assertEquals("java.lang.Boolean", booleanValue.getClassName());
        assertEquals("java.lang.Double", doubleValue.getClassName());
    }

    @Test
    public void testIsMandatory() throws Exception {
        assertTrue(longValue.isMandatory());
        assertTrue(stringValue.isMandatory());
        assertTrue(booleanValue.isMandatory());
        assertTrue(doubleValue.isMandatory());
    }

    @Test
    public void testIsValid() throws Exception {
     /*   assertTrue(longValue.isValid());
        assertTrue(stringValue.isValid());
        assertTrue(booleanValue.isValid());
        assertTrue(doubleValue.isValid());
        initializeNoDefaultValue();
        assertFalse(longValue.isValid());
        assertFalse(stringValue.isValid());
        assertFalse(booleanValue.isValid());
        assertFalse(doubleValue.isValid());*/

    }

    @Test
    public void testSetValid() throws Exception {
      /*  longValue.setValid(true);
        assertTrue(longValue.isValid());

        longValue.setValid(false);
        assertFalse(longValue.isValid()); */
    }
}