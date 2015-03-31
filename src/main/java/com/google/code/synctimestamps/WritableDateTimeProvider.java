/*-
 * $Id$
 */
package com.google.code.synctimestamps;

import java.io.File;
import java.util.Date;

import javax.annotation.Nullable;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public interface WritableDateTimeProvider extends DateTimeProvider {
	/**
	 * @param file
	 * @param dateTime
	 * @return the same file (which may have been moved, hence the new name).
	 */
	File setDateTime(final File file, final Date dateTime);
}
