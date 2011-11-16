/*-
 * $Id$
 */
package com.google.code.synctimestamps;

import java.io.File;
import java.util.Date;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public interface DateTimeProvider {
	/**
	 * @param file
	 */
	Date getDateTime(final File file);

	/**
	 * @param file
	 * @param parentDateTime
	 */
	void updateDateTime(final File file, final Date parentDateTime);
}
