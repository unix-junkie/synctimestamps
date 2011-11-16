/*-
 * $Id$
 */
package com.google.code.synctimestamps.io.predicates;

import java.io.File;

import com.google.common.base.Predicate;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class RegularFile implements Predicate<File> {
	/**
	 * @see Predicate#apply(Object)
	 */
	@Override
	public boolean apply(final File input) {
		return input.isFile();
	}
}
