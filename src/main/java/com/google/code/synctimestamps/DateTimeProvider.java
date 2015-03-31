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
