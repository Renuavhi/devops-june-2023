package org.tektutor;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

public class DataAccessLayerTest {

	private DataAccessLayer dal;
	private String actualResponse;
	private String expectedResponse;

	@Before
	public void init() {
		dal = new DataAccessLayer();
	}

	@Test
	public void testDataAccessLayer() {
		actualResponse = dal.getModuleName();	
		expectedResponse = "DataAccessLayer Module";

		assertEquals ( expectedResponse, actualResponse );
	}

	@After
	public void cleanUp() {
		dal = null;
	}

}
