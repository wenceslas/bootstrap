/*
 * Licensed under MIT (https://github.com/ligoj/ligoj/blob/master/LICENSE)
 */
package org.ligoj.bootstrap.core.resource.mapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.springframework.dao.EmptyResultDataAccessException;

/**
 * Handles business exception (explicitly managed) {@link EmptyResultDataAccessException} (wrapping NoResultException)
 * to a JSON string, and a 404 status code error.
 */
@Provider
public class NoResultExceptionMapper extends AbstractEntityNotFoundExceptionMapper implements ExceptionMapper<EmptyResultDataAccessException> {

	@Override
	public Response toResponse(final EmptyResultDataAccessException exception) {
		return toResponseNotFound(exception.getCause());
	}

}
