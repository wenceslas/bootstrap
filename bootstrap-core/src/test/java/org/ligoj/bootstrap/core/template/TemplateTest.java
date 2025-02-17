/*
 * Licensed under MIT (https://github.com/ligoj/ligoj/blob/master/LICENSE)
 */
package org.ligoj.bootstrap.core.template;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ligoj.bootstrap.model.system.SystemRole;
import org.ligoj.bootstrap.model.system.SystemRoleAssignment;
import org.ligoj.bootstrap.model.system.SystemUser;

/**
 * Test class of {@link TemplateTest}
 */
class TemplateTest {

	private Writer writer;

	private ByteArrayOutputStream bos;

	@BeforeEach
    void initialize() {
		bos = new ByteArrayOutputStream();
		writer = new PrintWriter(bos);
	}

	/**
	 * Simple write empty template.
	 */
	@Test
    void testWriteEmpty() throws IOException {
		new Template<>("").write(writer, new HashMap<>(), null);
		Assertions.assertEquals(0, bos.toByteArray().length);
	}

	/**
	 * Not mapped tag.
	 */
	@Test
    void testWriteNotMappedTag() {
		Assertions.assertThrows(Exception.class, () -> new Template<>("{{any/}}").write(writer, new HashMap<>(), null), "Not mapped template tag {{any}} found at position 0");
	}

	/**
	 * Not invalid end tag.
	 */
	@Test
    void testWriteInvalidEnd() {
		Assertions.assertThrows(Exception.class, () -> new Template<>("{{any/}").write(writer, new HashMap<>(), null), "Invalid opening tag syntax '{{' without '}}' at position 0");
	}

	/**
	 * Not invalid end tag.
	 */
	@Test
    void testWriteInvalidEndOverlay() {
		final Map<String, Processor<?>> tags = new HashMap<>();
		tags.put("any", new Processor<>(new String[] { "value" }));
		Assertions.assertThrows(Exception.class, () -> new Template<>("{{any}}{{one... {{/any}}..}}").write(writer, tags, null), "Invalid opening tag syntax '{{' without '}}' at position 7");
	}

	/**
	 * Missing closing tag.
	 */
	@Test
    void testWriteNoClosing() {
		final Map<String, Processor<?>> tags = new HashMap<>();
		tags.put("any", new Processor<>());
		Assertions.assertThrows(Exception.class, () -> new Template<>("{{any}}..").write(writer, tags, null), "Closing tag {{/any}} not found");
	}

	/**
	 * Null collection.
	 */
	@Test
    void testWriteNullCollection() throws IOException {
		final Map<String, Processor<?>> tags = new HashMap<>();
		tags.put("any", new Processor<>());
		new Template<>("{{any}}.{{/any}}").write(writer, tags, null);
		Assertions.assertEquals(0, bos.toByteArray().length);
	}

	/**
	 * Missing closing tag.
	 */
	@Test
    void testWriteNoClosingNesting() {
		final Map<String, Processor<?>> tags = new HashMap<>();
		tags.put("any", new Processor<>(new String[] { "value" }));
		tags.put("one", new Processor<>());
		Assertions.assertThrows(Exception.class, () -> new Template<>("{{any}}.{{one}}.{{/any}}.{{/one}}").write(writer, tags, null), "Closing tag {{/one}} not found");
	}

	/**
	 * Empty tag name.
	 */
	@Test
    void testWriteEmptyTag() {
		Assertions.assertThrows(Exception.class, () -> new Template<>("{{}}").write(writer, new HashMap<>(), null), "Empty tag {{}} found at position 0");
	}

	/**
	 * Too long tag name.
	 */
	@Test
    void testWriteTooLongTag() {
		Assertions.assertThrows(Exception.class, () -> new Template<>("{{" + "z".repeat(101) + "}}..").write(writer, new HashMap<>(), null), "Too long (max is 100 tag zzzzzzzzzzzzzzzzzzzzzzzzzzzz...}} found at position 0");
	}

	/**
	 * Bean, without attribute access
	 */
	@Test
    void testWriteBean() throws IOException {
		final Map<String, Processor<?>> tags = new HashMap<>();
		tags.put("tag", new Processor<>("value"));
		new Template<>("{{tag}}..{{/tag}}").write(writer, tags, null);
		Assertions.assertEquals("..", new String(bos.toByteArray(), StandardCharsets.UTF_8));
	}

	/**
	 * Cascaded beans
	 */
	@Test
    void testWriteCascadedBean() throws IOException {
		final Map<String, Processor<?>> tags = new HashMap<>();
		final var user = new SystemUser();
		user.setLogin("junit");
		final Set<SystemRoleAssignment> assignments = new HashSet<>();
		final var assignment = new SystemRoleAssignment();
		final var role = new SystemRole();
		role.setName("myRole");
		assignment.setRole(role);
		assignments.add(assignment);
		user.setRoles(assignments);
		tags.put("user", new Processor<>(user));
		tags.put("username", new BeanProcessor<>(SystemUser.class, "login"));
		tags.put("assignments", new BeanProcessor<>(SystemUser.class, "roles"));
		tags.put("role", new BeanProcessor<>(SystemRoleAssignment.class, "role"));
		tags.put("name", new BeanProcessor<>(SystemRole.class, "name"));
		new Template<>("{{user}}1{{username/}}2{{assignments}}3{{role}}4{{name/}}5{{/role}}6{{/assignments}}7{{/user}}").write(writer, tags,
				null);
		Assertions.assertEquals("1junit234myRole567", new String(bos.toByteArray(), StandardCharsets.UTF_8));
	}

	/**
	 * Nullable bean.
	 */
	@Test
    void testWriteCascadedNullableBean() throws IOException {
		final Map<String, Processor<?>> tags = new HashMap<>();
		tags.put("user", new Processor<>("junit"));
		new Template<>("0{{user}}1{{user/}}2{{/user}}3").write(writer, tags, null);
		Assertions.assertEquals("01junit23", new String(bos.toByteArray(), StandardCharsets.UTF_8));
	}

	/**
	 * Expecting collection or array and got empty {@link ArrayList}
	 */
	@Test
    void testWriteEmptyCollection() throws IOException {
		final Map<String, Processor<?>> tags = new HashMap<>();
		tags.put("tag", new Processor<>(new ArrayList<>()));
		new Template<>("{{tag}}..{{/tag}}").write(writer, tags, null);
		Assertions.assertEquals(0, bos.toByteArray().length);
	}

	/**
	 * Null data
	 */
	@Test
    void testWriteNullData() throws IOException {
		final Map<String, Processor<?>> tags = new HashMap<>();
		tags.put("tag", new Processor<>());
		new Template<>("{{tag/}}").write(writer, tags, null);
		Assertions.assertEquals(0, bos.toByteArray().length);
	}

	/**
	 * Expecting collection or array and got a simple array
	 */
	@Test
    void testWriteArray() throws IOException {
		final Map<String, Processor<?>> tags = new HashMap<>();
		tags.put("tag", new Processor<>(new String[] { "value" }));
		new Template<>("{{tag}}..{{/tag}}").write(writer, tags, null);
		Assertions.assertEquals("..", new String(bos.toByteArray(), StandardCharsets.UTF_8));
	}

	/**
	 * Compete features
	 */
	@Test
    void testWriteFullFeature() throws IOException {
		final Map<String, Processor<?>> tags = new HashMap<>();
		tags.put("tag", new Processor<>(new String[] { "A", "B" }));
		tags.put("tag-value", new Processor<>());
		tags.put("sub-tag", new Processor<>(Arrays.asList("john", "doe")));
		tags.put("sub-tag-value", new Processor<>());
		new Template<>("0{{tag}}1{{tag-value/}}2{{sub-tag}}{{sub-tag-value/}}{{/sub-tag}}{{/tag}}3").write(writer, tags, null);
		Assertions.assertEquals("01A2johndoe1B2johndoe3", new String(bos.toByteArray(), StandardCharsets.UTF_8));
	}
}
