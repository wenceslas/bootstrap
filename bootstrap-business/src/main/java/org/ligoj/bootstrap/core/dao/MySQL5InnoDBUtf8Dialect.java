/*
 * Licensed under MIT (https://github.com/ligoj/ligoj/blob/master/LICENSE)
 */
package org.ligoj.bootstrap.core.dao;

import org.hibernate.dialect.MySQLDialect;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;

/**
 * MySQL dialect with enforced UTF-8 encoding.
 */
public class MySQL5InnoDBUtf8Dialect extends MySQLDialect {

	public MySQL5InnoDBUtf8Dialect() {
		registerKeyword("USAGE");
	}

	@Override
	public String getTableTypeString() {
		return super.getTableTypeString() + " DEFAULT CHARSET=utf8";
	}

	@Override
	public NameQualifierSupport getNameQualifierSupport() {
		return NameQualifierSupport.NONE;
	}

}
