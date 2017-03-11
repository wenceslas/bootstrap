package org.ligoj.bootstrap.core.security;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Class to test the securityContexUtilClass
 */
public class SecurityContextUtilTest {

	private final SecurityHelper securityHelper = new SecurityHelper();

	/**
	 * Name for the user authenticated
	 */
	private static final String USER_NAME = "admin";

	/**
	 * Method to test the mock authentication test
	 */
	@Test
	public void testSetUserNameNoName() {
		// call to the method
		securityHelper.setUserName(USER_NAME);
		securityHelper.setUserName(null);

		// make the assertion of untouched user name
		final String nameAuthenticated = SecurityContextHolder.getContext().getAuthentication().getName();
		Assert.assertEquals(USER_NAME, nameAuthenticated);
	}

	/**
	 * Method to test the mock authentication test
	 */
	@Test
	public void testSetUserName() {
		// call to the method
		securityHelper.setUserName(USER_NAME);

		// make the assertion
		final String nameAuthenticated = SecurityContextHolder.getContext().getAuthentication().getName();
		Assert.assertEquals(USER_NAME, nameAuthenticated);
	}

}
