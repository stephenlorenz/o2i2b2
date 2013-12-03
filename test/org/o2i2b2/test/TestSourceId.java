package org.o2i2b2.test;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.math.NumberUtils;
import org.junit.Test;
import org.o2i2b2.loader.ImportUtils;

public class TestSourceId {

	@Test
	public void testSourceId() {
		
		assertTrue(ImportUtils.getSourceId("this is a test").equals("THIS_IS_A_TEST")); 
		assertTrue(ImportUtils.getSourceId("PIH-MWI").equals("PIH_MWI")); 
		
		
	}
	
	@Test
	public void testIsNumeric() {
		assertTrue(NumberUtils.isNumber("1"));
		assertTrue(!NumberUtils.isNumber("a"));
		assertTrue(NumberUtils.isNumber("0.0023"));
	}
}
