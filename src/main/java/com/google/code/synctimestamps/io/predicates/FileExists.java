/*-
 * $Id$
 */
package com.google.code.synctimestamps.io.predicates;

import java.io.File;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

/**
 * This predicate has a side-effect.
 *
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class FileExists implements Predicate<File> {
	/**
	 * @see Predicate#apply(Object)
	 */
	@Override
	public boolean apply(@Nullable final File input) {
		if (input == null) {
			return false;
		}

		final boolean exists = input.exists();
		if (!exists) {
			System.out.println("File doesn't exist: " + input.getPath());
		}

		return exists;
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
