package org.ligoj.bootstrap.core.csv;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import org.ligoj.bootstrap.core.dao.csv.ClassPathResourceMultiple;

/**
 * Test class of {@link ClassPathResourceMultiple}
 */
public class ClassPathResourceMultipleTest {

	@Test
	public void testGetInputStream2Paths() throws IOException {
		final ClassPathResourceMultiple resource = new ClassPathResourceMultiple("csv/demo", DummyEntity.class);
		InputStream stream = resource.getInputStream();
		Assert.assertNotNull(stream);
		IOUtils.closeQuietly(stream);
	}

	@Test
	public void testGetInputStream1Path() throws IOException {
		final ClassPathResourceMultiple resource = new ClassPathResourceMultiple("csv/demo", dummyminus.class);
		InputStream stream = resource.getInputStream();
		Assert.assertNotNull(stream);
		IOUtils.closeQuietly(stream);
	}

	@Test(expected = FileNotFoundException.class)
	public void testGetInputStreamNotFound() throws IOException {
		final ClassPathResourceMultiple resource = new ClassPathResourceMultiple("csv/demo", DummyEntity3.class);
		IOUtils.closeQuietly(resource.getInputStream());
	}
}
