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
public abstract class ExtensionFilter implements Predicate<File> {
	/**
	 * @param input
	 */
	protected static String getExtension(final File input) {
		final String name = input.getName();
		final int index = name.lastIndexOf('.');
		return index == -1 ? null : name.substring(index + 1).toLowerCase();
	}
}
