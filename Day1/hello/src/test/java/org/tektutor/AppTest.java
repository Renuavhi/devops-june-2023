package org.tektutor;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Ignore;

public class AppTest {
	private String actualResponse;
	private String expectedResponse;

	private App app = null;

	@Before
	public void init() {
		app = new App();
	}

	@After
	public void cleanUp() {
		app = null;
	}

	@Test
	@Ignore
	public void testSayHello() {
		actualResponse = app.sayHello();
		expectedResponse = "Hello Maven !!!";

		assertEquals ( expectedResponse, actualResponse );
	}
}


