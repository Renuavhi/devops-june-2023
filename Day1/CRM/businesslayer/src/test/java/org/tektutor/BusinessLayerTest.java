package org.tektutor;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

public class BusinessLayerTest {

	private BusinessLayer bl;
	private String actualResponse;
	private String expectedResponse;

	@Before
	public void init() {
		bl = new BusinessLayer();
	}

	@Test
	public void testBusinessLayer() {
		actualResponse = bl.getModuleName();	
		expectedResponse = "BusinessLayer Module";

		assertEquals ( expectedResponse, actualResponse );
	}

	@After
	public void cleanUp() {
		bl = null;
	}

}
