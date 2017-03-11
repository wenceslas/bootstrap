package org.ligoj.bootstrap.core;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A named bean
 * 
 * @param <ID>
 *            the type of the identifier
 */
@Getter
@Setter
@ToString(of = "name")
@AllArgsConstructor
@NoArgsConstructor
public class NamedBean<ID extends Serializable> implements INamableBean<ID> {

	/**
	 * Identifier of this bean. 
	 */
	private ID id;

	@NotBlank
	@NotNull
	private String name;

	/**
	 * Copy a bean to another one.
	 * 
	 * @param <T>
	 *            Bean type.
	 * @param from
	 *            the source bean.
	 * @param to
	 *            the target bean.
	 */
	public static <T extends Serializable> void copy(final INamableBean<T> from, final INamableBean<T> to) {
		to.setId(from.getId());
		to.setName(from.getName());
	}

}
