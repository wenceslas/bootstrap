/*
 * Licensed under MIT (https://github.com/ligoj/ligoj/blob/master/LICENSE)
 */
package org.ligoj.bootstrap.core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.ligoj.bootstrap.core.resource.mapper.AccessDeniedExceptionMapper;
import org.ligoj.bootstrap.model.system.SystemAuthorization.AuthorizationType;
import org.ligoj.bootstrap.resource.system.security.AuthorizationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * URL based security filter based on RBAC strategy. Maintains a set of cache to determine as fast as possible the valid
 * authorizations from the incoming HTTP request.
 */
public class AuthorizingFilter extends GenericFilterBean {

	@Autowired
	private AuthorizationResource authorizationResource;

	@Autowired
	private AccessDeniedExceptionMapper accessDeniedHelper;

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {
		final var httpRequest = (HttpServletRequest) request;

		/*
		 * This is the most serious place of security check. If this filter is called, it means the previous security
		 * checks granted access until there. So, it means the current user is either anonymous either (but assumed) a
		 * fully authenticated user. In case of anonymous user case, there is no role but ROLE_ANONYMOUS. So there is no
		 * need to involve more role checking. We assume there is no way to grant access to ROLE_ANONYMOUS with this
		 * filter.
		 */
		final var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		if (!authorities.contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
			// Not anonymous, so we need to check using RBAC strategy.

			// Build the URL
			final var fullRequest = getFullRequest(httpRequest);
			// Check access
			final var method = StringUtils.upperCase(httpRequest.getMethod());
			if (!isAuthorized(authorities, fullRequest, method)) {
				// Forbidden access
				updateForbiddenAccess((HttpServletResponse) response);
				return;
			}
		}

		// Granted access, continue
		chain.doFilter(request, response);
	}

	/**
	 * Update response for a forbidden access.
	 */
	private void updateForbiddenAccess(final HttpServletResponse response) throws IOException {
		final var response2 = accessDeniedHelper.toResponse(new AccessDeniedException(""));
		response.setStatus(response2.getStatus());
		response.setContentType(response2.getMediaType().toString());
		response.getOutputStream().write(((String) response2.getEntity()).getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Return the full request without query and without context path. Servlet path is kept. The returned path does not
	 * start with '/'.
	 */
	private String getFullRequest(final HttpServletRequest httpRequest) {
		return StringUtils.removeStart(
				httpRequest.getRequestURI().substring(this.getServletContext().getContextPath().length()), "/");
	}

	/**
	 * Check the authorization
	 */
	private boolean isAuthorized(final Collection<? extends GrantedAuthority> authorities, final String request,
			final String method) {
		final var authorizationsCache = authorizationResource.getAuthorizations().get(AuthorizationType.API);

		// Check the authorization
		if (authorizationsCache != null) {
			for (final GrantedAuthority authority : authorities) {
				final var authorizations = authorizationsCache.get(authority.getAuthority());
				if (authorizations != null && match(authorizations.get(method), request)) {
					// Granted access
					return true;
				}
			}
		}

		// No authorization found
		return false;
	}

	private boolean match(final Collection<Pattern> patterns, final String toMatch) {
		if (patterns != null) {
			for (final var pattern : patterns) {
				if (pattern.matcher(toMatch).find()) {
					// Granted access
					return true;
				}
			}
		}
		return false;
	}
}
