/*-
 * $Id$
 */
package com.google.code.synctimestamps.io.predicates;

import static java.lang.String.format;
import static org.apache.log4j.Logger.getLogger;

import java.io.File;

import javax.annotation.Nullable;

import org.apache.log4j.Logger;

import com.google.common.base.Predicate;

/**
 * This predicate has a side-effect.
 *
 * @author Andrey ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class FileExists implements Predicate<File> {
	private static final Logger LOGGER = getLogger(FileExists.class);

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
			LOGGER.info(format("File doesn't exist: %s", input.getPath()));
		}

		return exists;
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
