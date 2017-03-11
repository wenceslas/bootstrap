package org.ligoj.bootstrap.core.resource.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.dao.DataAccessResourceFailureException;

import lombok.extern.slf4j.Slf4j;

/**
 * Handles database (no transaction) issue to a JSON string.
 */
@Provider
@Slf4j
public class DataAccessResourceFailureExceptionMapper extends AbstractDatabaseDownExceptionMapper
		implements ExceptionMapper<DataAccessResourceFailureException> {

	@Override
	public Response toResponse(final DataAccessResourceFailureException exception) {
		log.error("Connection exception", exception);
		return super.toResponse();
	}

}
