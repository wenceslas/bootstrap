package org.ligoj.bootstrap.core.resource.handler;

import java.time.Instant;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import org.ligoj.bootstrap.AbstractDataGeneratorTest;
import org.ligoj.bootstrap.core.DateUtils;

/**
 * Test class of {@link LocalDateParamConverter}
 */
public class LocalDateParamConverterTest extends AbstractDataGeneratorTest {

	@Test
	public void fromStringNull() {
		Assert.assertNull(new LocalDateParamConverter().fromString(null));
	}

	@Test
	public void fromString() {
		final Date time = getDate(2016, 9, 8, 12, 52, 16);
		Assert.assertEquals("2016-09-08", new LocalDateParamConverter().fromString(String.valueOf(time.getTime())).toString());
	}

	@Test
	public void dateToString() {
		final Date time = getDate(2016, 9, 8, 12, 52, 16);
		final Date date = getDate(2016, 9, 8);

		// Check only date is kept without time
		Assert.assertEquals(String.valueOf(date.getTime()), new LocalDateParamConverter()
				.toString(Instant.ofEpochMilli(time.getTime()).atZone(DateUtils.getApplicationTimeZone().toZoneId()).toLocalDate()));
	}
}
