/*-
 * $Id$
 */
package com.google.code.synctimestamps.io.predicates;

import java.io.File;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import com.google.common.base.Predicate;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public abstract class ExtensionFilter implements Predicate<File> {
	/**
	 * @param input
	 */
	@Nullable
	@CheckForNull
	protected static String getExtension(final File input) {
		final String name = input.getName();
		final int index = name.lastIndexOf('.');
		return index == -1 ? null : name.substring(index + 1).toLowerCase();
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
	public final boolean equals(@Nullable final Object obj) {
		return super.equals(obj);
	}
}
