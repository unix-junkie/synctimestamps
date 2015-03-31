/*-
 * $Id$
 */
package com.google.code.synctimestamps.io.predicates;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import com.google.common.base.Predicate;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class AllowedExtensions extends ExtensionFilter {
	private final Set<String> allowedExtensions = new HashSet<>();

	/**
	 * @param allowedExtensions
	 */
	public AllowedExtensions(final Set<String> allowedExtensions) {
		for (final String extension : allowedExtensions) {
			if (extension.indexOf('.') != -1) {
				continue;
			}

			this.allowedExtensions.add(extension.toLowerCase());
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
		return extension != null && this.allowedExtensions.contains(extension);
	}

	/**
	 * @see Object#toString()
	 */
	@Nullable
	@CheckForNull
	@Override
	public String toString() {
		return this.allowedExtensions.toString();
	}
}
