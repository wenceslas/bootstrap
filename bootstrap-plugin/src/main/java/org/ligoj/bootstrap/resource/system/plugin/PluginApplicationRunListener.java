/*
 * Licensed under MIT (https://github.com/ligoj/ligoj/blob/master/LICENSE)
 */
package org.ligoj.bootstrap.resource.system.plugin;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.ligoj.bootstrap.core.plugin.PluginsClassLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import lombok.extern.slf4j.Slf4j;

/**
 * Application listener able to alter the class loader to the plug-in class-loader.
 */
@Slf4j
public class PluginApplicationRunListener implements SpringApplicationRunListener, Ordered {

	/**
	 * Required Spring-Boot constructor to be compliant to {@link SpringApplicationRunListener}
	 *
	 * @param application The current application.
	 * @param args        The application arguments.
	 * @throws IOException              When reading plug-ins directory
	 * @throws NoSuchAlgorithmException MD5 digest is unavailable for version ciphering.
	 */
	public PluginApplicationRunListener(final SpringApplication application, final String... args)
			throws IOException, NoSuchAlgorithmException {
		if (PluginsClassLoader.getInstance() == null) {
			// Replace the main class loader
			log.info("Install the plugin classloader for application {}({})", application, args);
			replaceClassLoader();
		}
	}

	/**
	 * Replace the current classloader by a {@link PluginsClassLoader} instance.
	 *
	 * @throws IOException              When the setup failed.
	 * @throws NoSuchAlgorithmException MD5 digest is unavailable for version ciphering.
	 */
	protected void replaceClassLoader() throws IOException, NoSuchAlgorithmException {
		Thread.currentThread().setContextClassLoader(new PluginsClassLoader());
	}

	@Override
	public void starting() {
		// Nothing to do
	}

	@Override
	public int getOrder() {
		// Be sure to be executed before EventPublishingRunListener
		return -10;
	}

	@Override
	public void environmentPrepared(final ConfigurableEnvironment environment) {
		// Nothing to do
	}

	@Override
	public void contextPrepared(final ConfigurableApplicationContext context) {
		// Nothing to do
	}

	@Override
	public void contextLoaded(final ConfigurableApplicationContext context) {
		// Nothing to do
	}

	@Override
	public void failed(ConfigurableApplicationContext context, Throwable exception) {
		// Nothing to do
		log.error("Context failed to start", exception);
	}

	@Override
	public void running(ConfigurableApplicationContext context) {
		// Nothing to do
	}

	@Override
	public void started(ConfigurableApplicationContext context) {
		// Nothing to do
	}

}
