package org.ligoj.bootstrap.core.crypto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Test class of {@link SystemEnvironmentAndFilePBEConfig} using a FS file.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/application-context-test.xml")
public class SystemEnvironmentAndFilePBEConfigTest extends AbstractSystemEnvironmentAndFilePBEConfigTest {

	@BeforeAll
	public static void init() {
		System.setProperty("app.crypto.file", "src/test/resources/security.key");
	}

}
