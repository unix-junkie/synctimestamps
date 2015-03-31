/*-
 * $Id$
 */
package com.google.code.synctimestamps;

import java.io.File;
import java.util.Date;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public interface DateTimeProvider {
	/**
	 * @param file
	 */
	@Nullable
	@CheckForNull
	Date getDateTime(final File file);

	/**
	 * @param file
	 * @param parentDateTime
	 */
	void updateDateTime(final File file, @Nullable final Date parentDateTime);
}
