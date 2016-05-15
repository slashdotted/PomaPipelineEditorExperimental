package model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


/**
 * Created by felipe on 14/05/16.
 */
public class ModuleTemplateTest {

    private static ModuleTemplate moduleTemplate;

    @Before
    public void setUp() throws Exception {
        moduleTemplate = ModuleTemplate.getInstance();
        moduleTemplate.setType("testType");
        moduleTemplate.setDescription("testDescription");
    }

    @Test
    public void testGetInstance() throws Exception {
        assertNotNull(moduleTemplate);
    }

    @Test
    public void testGetImageURL() throws Exception {
        assertNotNull(moduleTemplate.getImageURL());
    }

    @Test
    public void testSetImageURL() throws Exception {
        assertEquals("moduleImage.png", moduleTemplate.getImageURL());
    }

    @Test
    public void testGetDescription() throws Exception {
        assertEquals("testDescription", moduleTemplate.getDescription());
    }

    @Test
    public void testSetDescription() throws Exception {
        moduleTemplate.setDescription("otherTestDescription");
        assertEquals("otherTestDescription", moduleTemplate.getDescription());
    }

    @Test
    public void testGetType() throws Exception {
        assertEquals("testType", moduleTemplate.getType());

    }

    @Test
    public void testSetType() throws Exception {
        moduleTemplate.setType("otherTestType");
        assertEquals("otherTestType", moduleTemplate.getType());
    }

    @Test
    public void testGetOptParameters() throws Exception {

    }

    @Test
    public void testSetOptParameters() throws Exception {

    }

    @Test
    public void testGetMandatoryParameters() throws Exception {

    }

    @Test
    public void testSetMandatoryParameters() throws Exception {

    }

    @Test
    public void testGetNameInstance() throws Exception {

    }

    @Test
    public void testGetCounter() throws Exception {

    }

    @Test
    public void testToString() throws Exception {

    }

    @Test
    public void testGetParamsCopy() throws Exception {

    }
}