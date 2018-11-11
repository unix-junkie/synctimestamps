/*-
 * $Id$
 */
package com.google.code.synctimestamps;

import java.io.File;
import java.util.Date;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

/**
 * @author Andrey ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public interface DateTimeProvider {
	/**
	 * <p>Returns the timestamp of a file on the file system, or {@code
	 * null} if this provider can't determine the timestamp, or the
	 * timestamp is before Jan 1st, 1970.</p>
	 *
	 * @param file
	 * @return the timestamp of a file, or {@code null}.
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
