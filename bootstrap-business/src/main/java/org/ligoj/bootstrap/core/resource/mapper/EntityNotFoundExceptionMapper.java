/*
 * Licensed under MIT (https://github.com/ligoj/ligoj/blob/master/LICENSE)
 */
package org.ligoj.bootstrap.core.resource.mapper;

import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Handles business exception (explicitly managed) {@link EntityNotFoundException} to a JSON string, and a 404 status code error.
 */
@Provider
public class EntityNotFoundExceptionMapper extends AbstractEntityNotFoundExceptionMapper implements ExceptionMapper<EntityNotFoundException> {

	@Override
	public Response toResponse(final EntityNotFoundException exception) {
		return toResponseNotFound(exception);
	}

}
