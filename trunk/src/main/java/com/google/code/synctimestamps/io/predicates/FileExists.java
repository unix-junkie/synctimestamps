/*-
 * $Id$
 */
package com.google.code.synctimestamps.io.predicates;

import java.io.File;

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
	public boolean apply(final File input) {
		final boolean exists = input.exists();
		if (!exists) {
			System.out.println("File doesn't exist: " + input.getPath());
		}

		return exists;
	}
}
