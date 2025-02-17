/*
 * Licensed under MIT (https://github.com/ligoj/ligoj/blob/master/LICENSE)
 */
package org.ligoj.bootstrap;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import jakarta.ws.rs.core.UriInfo;
import org.hibernate.collection.spi.PersistentBag;
import org.junit.jupiter.api.BeforeAll;
import org.ligoj.bootstrap.core.crypto.CryptoHelper;
import org.ligoj.bootstrap.core.csv.AbstractCsvManager;
import org.ligoj.bootstrap.core.dao.csv.CsvForJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

/**
 * Basic JPA test support.
 */
public abstract class AbstractJpaTest extends AbstractSecurityTest {

	@Autowired
	protected CsvForJpa csvForJpa;

	@Autowired
	protected CryptoHelper cryptoHelper;

	@Autowired
	protected CacheManager cacheManager;

	@BeforeAll
	static void init() {
		System.setProperty("app.crypto.file", "src/test/resources/security.key");
	}

	/**
	 * Entity manager.
	 */
	@PersistenceContext(type = PersistenceContextType.TRANSACTION, unitName = "pu")
	protected EntityManager em;

	/**
	 * Clear all caches.
	 */
	protected void clearAllCache() {
		cacheManager.getCacheNames().stream().map(cacheManager::getCache).forEach(Cache::clear);
	}

	/**
	 * Persist all elements from the given CSV resource.
	 *
	 * @param clazz    The JPA entity class.
	 * @param resource The CSV resource path.
	 * @param <T>      The entity type.
	 * @return persisted entities.
	 * @throws IOException from {@link ClassPathResource#getInputStream()}
	 */
	protected <T> List<T> persistEntities(final Class<T> clazz, final String resource) throws IOException {
		return persistEntities(clazz, resource, AbstractCsvManager.DEFAULT_ENCODING);
	}

	/**
	 * Persist all elements from the given CSV resource.
	 *
	 * @param clazz    the JPA entity class.
	 * @param resource the CSV resource path.
	 * @param encoding {@link java.nio.charset.Charset charset} for read.
	 * @param <T>      The entity type.
	 * @return persisted entities.
	 * @throws IOException from {@link ClassPathResource#getInputStream()}
	 */
	private <T> List<T> persistEntities(final Class<T> clazz, final String resource, final Charset encoding)
			throws IOException {
		// Persist entities from CSV
		final var list = csvForJpa.toJpa(clazz,
				new InputStreamReader(new ClassPathResource(resource).getInputStream(), encoding), true, true);
		em.flush();
		em.clear();
		return list;
	}

	/**
	 * Persist the managed entities using CSV file corresponding to the entity name, inside the given root path.
	 *
	 * @param csvRoot     the root path of CSV resources. Lower case file name will be used.
	 * @param entityModel the ordered set to clean, and also to refill from CSV files.
	 * @return the pagination object.
	 * @throws IOException Read issue occurred.
	 */
	protected UriInfo persistEntities(final String csvRoot, final Class<?>... entityModel) throws IOException {
		return persistEntities(csvRoot, entityModel, AbstractCsvManager.DEFAULT_ENCODING);
	}

	/**
	 * Persist the managed entities using CSV file corresponding to the entity name, inside the given root path.
	 *
	 * @param csvRoot     the root path of CSV resources. Lower case file name will be used.
	 * @param entityModel the ordered set to clean, and also to refill from CSV files.
	 * @param encoding    the encoding used to read the CSV resources. The name of a supported {@link java.nio.charset.Charset
	 *                    charset}
	 * @return the pagination object.
	 * @throws IOException Read issue occurred.
	 * @deprecated Use #persistEntities
	 */
	@Deprecated
	protected UriInfo persistEntities(final String csvRoot, final Class<?>[] entityModel, final String encoding)
			throws IOException {
		return persistEntities(csvRoot,entityModel, Charset.forName(encoding));
	}
	/**
	 * Persist the managed entities using CSV file corresponding to the entity name, inside the given root path.
	 *
	 * @param csvRoot     the root path of CSV resources. Lower case file name will be used.
	 * @param entityModel the ordered set to clean, and also to refill from CSV files.
	 * @param encoding {@link java.nio.charset.Charset charset} for read.
	 * @return the pagination object.
	 * @throws IOException Read issue occurred.
	 */
	protected UriInfo persistEntities(final String csvRoot, final Class<?>[] entityModel, final Charset encoding)
			throws IOException {
		csvForJpa.reset(csvRoot, entityModel, encoding.name());
		return newUriInfo();
	}

	/**
	 * Test a collection is a {@link PersistentBag} and is initialized.
	 *
	 * @param bag the JPA bag to test.
	 * @return <code>true</code> if the given collection is a {@link PersistentBag} and is initialized.
	 */
	protected boolean isLazyInitialized(final Collection<?> bag) {
		return bag instanceof PersistentBag<?> b && b.wasInitialized();
	}
}
