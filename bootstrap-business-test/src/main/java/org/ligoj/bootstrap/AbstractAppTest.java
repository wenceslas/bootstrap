/*
 * Licensed under MIT (https://github.com/ligoj/ligoj/blob/master/LICENSE)
 */
package org.ligoj.bootstrap;

import org.ligoj.bootstrap.model.system.SystemAuthorization;
import org.ligoj.bootstrap.model.system.SystemRole;
import org.ligoj.bootstrap.model.system.SystemRoleAssignment;
import org.ligoj.bootstrap.model.system.SystemUser;
import org.ligoj.bootstrap.model.system.SystemAuthorization.AuthorizationType;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Basic Appication test support.
 */
public abstract class AbstractAppTest extends AbstractJpaTest {

	@Autowired
	protected CacheManager cacheManager;

	/**
	 * Clear all caches.
	 */
	protected void clearAllCache() {
		cacheManager.getCacheNames().stream().map(cacheManager::getCache).forEach(Cache::clear);
	}

	/**
	 * Persist system user, role and assignment for user DEFAULT_USER.
	 */
	protected void persistSystemEntities() {
		final SystemRole role = new SystemRole();
		role.setName("some");
		em.persist(role);
		final SystemUser user = new SystemUser();
		user.setLogin(DEFAULT_USER);
		em.persist(user);
		final SystemAuthorization authorization = new SystemAuthorization();
		authorization.setType(AuthorizationType.API);
		authorization.setPattern(".*");
		authorization.setRole(role);
		em.persist(authorization);
		final SystemRoleAssignment assignment = new SystemRoleAssignment();
		assignment.setRole(role);
		assignment.setUser(user);
		em.persist(assignment);
	}

	/**
	 * Destroy the given bean instance (usually a prototype instance obtained from
	 * this factory) according to its bean definition.
	 * <p>
	 * Any exception that arises during destruction should be caught and logged
	 * instead of propagated to the caller of this method.
	 *
	 * @param beanName
	 *            the name of the bean definition
	 */
	protected void destroySingleton(final String beanName) {
		try {
			((DefaultSingletonBeanRegistry) ((ConfigurableApplicationContext) applicationContext).getBeanFactory())
					.destroySingleton(beanName);
		} catch (final NoSuchBeanDefinitionException e) {
			// Ignore
		}
	}

	/**
	 * Register a singleton within the current application context. Don't forget to
	 * destroy this singleton with a try-finally at the end of your tests.
	 *
	 * @param <T> Singleton type.
	 * @param beanName
	 *            the name of the bean definition.
	 * @param singleton
	 *            the bean instance to register
	 * @return The given instance.
	 * @see #destroySingleton(String)
	 */
	protected <T> T registerSingleton(final String beanName, final T singleton) {
		((SingletonBeanRegistry) applicationContext.getAutowireCapableBeanFactory()).registerSingleton(beanName, singleton);
		return singleton;
	}
}
