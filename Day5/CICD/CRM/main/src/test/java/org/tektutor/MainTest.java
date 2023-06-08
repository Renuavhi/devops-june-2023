package org.tektutor;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

public class MainTest {

	private Main mainObj;
	private String actualResponse;
	private String expectedResponse;

	@Before
	public void init() {
		mainObj = new Main();
	}

	@Test
	public void testMain() {
		actualResponse = mainObj.getModuleName();	
		expectedResponse = "Main Module";

		assertEquals ( expectedResponse, actualResponse );
	}

	@After
	public void cleanUp() {
		mainObj = null;
	}

}
