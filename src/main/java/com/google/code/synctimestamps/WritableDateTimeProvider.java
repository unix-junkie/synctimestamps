/*-
 * $Id$
 */
package com.google.code.synctimestamps;

import java.io.File;
import java.util.Date;

/**
 * @author Andrey ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public interface WritableDateTimeProvider extends DateTimeProvider {
	/**
	 * @param file
	 * @param dateTime
	 * @return the same file (which may have been moved, hence the new name).
	 */
	File setDateTime(final File file, final Date dateTime);
}
