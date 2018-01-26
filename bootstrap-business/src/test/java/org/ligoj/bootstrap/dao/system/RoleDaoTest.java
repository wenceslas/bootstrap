package org.ligoj.bootstrap.dao.system;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ligoj.bootstrap.core.dao.AbstractBootTest;
import org.ligoj.bootstrap.model.system.SystemRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Test class of {@link SystemRoleRepository}
 */
@ExtendWith(SpringExtension.class)
public class RoleDaoTest extends AbstractBootTest {

	/**
	 * Factory DAO.
	 */
	@Autowired
	private SystemRoleRepository repository;

	@BeforeEach
	public void prepareData() throws IOException {
		persistEntities(SystemRole.class, "csv/system-test/role.csv");
	}

	@Test
	public void testFindAllSN1() {
		Assertions.assertFalse(repository.findAll().isEmpty());
	}
}