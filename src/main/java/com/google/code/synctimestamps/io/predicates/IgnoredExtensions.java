/*-
 * $Id$
 */
package com.google.code.synctimestamps.io.predicates;

import static java.lang.String.format;
import static org.apache.log4j.Logger.getLogger;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;

import com.google.common.base.Predicate;

/**
 * This predicate has a side-effect.
 *
 * @author Andrey ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class IgnoredExtensions extends ExtensionFilter {
	private static final Logger LOGGER = getLogger(IgnoredExtensions.class);

	private final Set<String> ignoredExtensions = new HashSet<>();

	/**
	 * @param ignoredExtensions
	 */
	public IgnoredExtensions(final Set<String> ignoredExtensions) {
		for (final String extension : ignoredExtensions) {
			if (extension.indexOf('.') != -1) {
				continue;
			}

			this.ignoredExtensions.add(extension.toLowerCase());
		}
	}

	/**
	 * @see Predicate#apply(Object)
	 */
	@Override
	public boolean apply(@Nullable final File input) {
		if (input == null) {
			return false;
		}

		final String extension = getExtension(input);
		if (extension == null || !this.ignoredExtensions.contains(extension)) {
			LOGGER.info(format("Unknown file type: %s", input.getPath()));
		}

		/*
		 * Used for logging purposes only,
		 * so returning false anyway.
		 */
		return false;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	@Nullable
	@CheckForNull
	public String toString() {
		return this.ignoredExtensions.toString();
	}
}
