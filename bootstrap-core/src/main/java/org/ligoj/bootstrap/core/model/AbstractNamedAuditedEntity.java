package org.ligoj.bootstrap.core.model;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import org.ligoj.bootstrap.core.INamableBean;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Named and audited entity.
 * 
 * @param <PK>
 *            Identifier type.
 */
@Getter
@Setter
@MappedSuperclass
@ToString(of = "name")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractNamedAuditedEntity<PK extends Serializable> extends AbstractAudited<PK> implements INamableBean<PK> {

	/**
	 * SID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Object name
	 */
	@NotBlank
	@NotNull
	private String name;

}
