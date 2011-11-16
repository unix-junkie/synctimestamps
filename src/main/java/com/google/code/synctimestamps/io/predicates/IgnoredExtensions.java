/*-
 * $Id$
 */
package com.google.code.synctimestamps.io.predicates;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Predicate;

/**
 * This predicate has a side-effect.
 *
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class IgnoredExtensions extends ExtensionFilter {
	private final Set<String> ignoredExtensions = new HashSet<String>();

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
	public boolean apply(final File input) {
		final String extension = getExtension(input);
		if (extension == null || !this.ignoredExtensions.contains(extension)) {
			System.out.println("Unknown file type: " + input.getPath());
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
	public String toString() {
		return this.ignoredExtensions.toString();
	}
}
