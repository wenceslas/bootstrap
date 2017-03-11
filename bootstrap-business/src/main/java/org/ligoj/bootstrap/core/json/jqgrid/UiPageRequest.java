package org.ligoj.bootstrap.core.json.jqgrid;

import lombok.Getter;
import lombok.Setter;

/**
 * A page request containing filters in addition of the Spring Data variables.
 */
@Getter
@Setter
public class UiPageRequest {

	/**
	 * Base 1 page number.
	 */
	private int page;

	/**
	 * Page size.
	 */
	private int pageSize;

	/**
	 * UI filters.
	 */
	private UiFilter uiFilter;

	/**
	 * UI sort.
	 */
	private UiSort uiSort;

}
