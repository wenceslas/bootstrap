/*
 * Licensed under MIT (https://github.com/ligoj/ligoj/blob/master/LICENSE)
 */
package org.ligoj.bootstrap.core.resource.mapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.ligoj.bootstrap.core.resource.AbstractMapper;

/**
 * Handles data exception resulting a 404 status code error.
 */
public abstract class AbstractEntityNotFoundExceptionMapper extends AbstractMapper {

	/**
	 * Forward to a 404 status error.
	 * 
	 * @param exception
	 *            the root exception.
	 * @return the built response.
	 */
	protected Response toResponseNotFound(final Throwable exception) {
		return toResponse(Status.NOT_FOUND, "entity", exception);
	}

}
