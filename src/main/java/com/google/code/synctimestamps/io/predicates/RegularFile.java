/*-
 * $Id$
 */
package com.google.code.synctimestamps.io.predicates;

import java.io.File;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

/**
 * @author Andrey ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class RegularFile implements Predicate<File> {
	/**
	 * @see Predicate#apply(Object)
	 */
	@Override
	public boolean apply(@Nullable final File input) {
		return input != null && input.isFile();
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Added purely for {@code null} analysis.</p>
	 *
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Added purely for {@code null} analysis.</p>
	 *
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(@Nullable final Object obj) {
		return super.equals(obj);
	}
}
