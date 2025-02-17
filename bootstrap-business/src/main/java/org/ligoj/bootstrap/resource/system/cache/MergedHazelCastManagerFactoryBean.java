/*
 * Licensed under MIT (https://github.com/ligoj/ligoj/blob/master/LICENSE)
 */
package org.ligoj.bootstrap.resource.system.cache;

import com.hazelcast.cache.HazelcastCacheManager;
import com.hazelcast.cache.HazelcastCachingProvider;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import lombok.Setter;
import org.ligoj.bootstrap.core.GlobalPropertyUtils;
import org.ligoj.bootstrap.resource.system.configuration.ConfigurationResource;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ModifiedExpiryPolicy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.HOURS;

/**
 * Factory of cache with modular {@link CacheConfig} creation delegate to
 * {@link CacheManagerAware} implementors.
 */
public class MergedHazelCastManagerFactoryBean implements FactoryBean<CacheManager>, InitializingBean, DisposableBean {

	/**
	 * Cache manager instance.
	 */
	protected CacheManager cacheManager;

	/**
	 * Cache configuration location.
	 */
	@Setter
	private String location;

	@Autowired
	protected ApplicationContext context;

	@Autowired
	protected ConfigurationResource configuration;

	@Setter
	@Value("${hazelcast.statistics.enable:false}")
	private boolean statisticsEnabled = false;

	@Override
	public void afterPropertiesSet() throws URISyntaxException {
		System.setProperty("hazelcast.jcache.provider.type", "member");
		final var properties = HazelcastCachingProvider.propertiesByLocation(location);
		final var provider = (com.hazelcast.cache.HazelcastCachingProvider) Caching.getCachingProvider();
		final var manager = (HazelcastCacheManager) provider.getCacheManager(new URI("bootstrap-cache-manager"), null,
				properties);
		context.getBeansOfType(CacheManagerAware.class).forEach((n, a) -> a.onCreate(manager, this::newCacheConfig));
		this.cacheManager = manager;
	}

	/**
	 * Compete the configuration after its creation and configuration be {@link CacheManagerAware} implementor.
	 *
	 * @param mapConfig The target {@link CacheConfig} to configure.
	 */
	protected void postConfigure(final CacheConfig<?, ?> mapConfig) {
		if (statisticsEnabled) {
			mapConfig.setStatisticsEnabled(true);
		}
	}

	/**
	 * Create a new {@link CacheConfig} with configured settings before {@link CacheManagerAware} implementor.
	 *
	 * @param name The cache name to configure.
	 * @return The created  {@link CacheConfig} with the policy.
	 */
	protected CacheConfig<?, ?> newCacheConfig(final String name) {
		final CacheConfig<?, ?> config = new CacheConfig<>(name);
		config.setEvictionConfig(new EvictionConfig().setEvictionPolicy(EvictionPolicy.LRU));
		// Post configuration
		postConfigure(config);

		// Last chance to override the default TTL from system configuration
		final var ttlDuration = configuration.get("cache." + name + ".ttl", -1);
		if (ttlDuration == -1) {
			config.setExpiryPolicyFactory(ModifiedExpiryPolicy.factoryOf(Duration.ETERNAL));
		} else {
			config.setExpiryPolicyFactory(ModifiedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, ttlDuration)));
		}

		return config;
	}

	@Override
	public CacheManager getObject() {
		return this.cacheManager;
	}

	@Override
	public Class<? extends CacheManager> getObjectType() {
		return this.cacheManager == null ? CacheManager.class : this.cacheManager.getClass();
	}

	@Override
	public void destroy() {
		this.cacheManager.close();
	}

}