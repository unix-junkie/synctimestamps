/*-
 * $Id$
 */
package com.google.code.synctimestamps;

import java.io.File;
import java.nio.file.Path;
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
	 * @see #getDateTime(Path)
	 */
	@Nullable
	@CheckForNull
	Date getDateTime(final File file);

	/**
	 * <p>Returns the timestamp of a file on the file system, or {@code
	 * null} if this provider can't determine the timestamp, or the
	 * timestamp is before Jan 1st, 1970.</p>
	 *
	 * @param file
	 * @return the timestamp of a file, or {@code null}.
	 * @see #getDateTime(File)
	 */
	@Nullable
	default Date getDateTime(final Path file) {
		return this.getDateTime(file.toFile());
	}

	/**
	 * @param file
	 * @param parentDateTime
	 * @see #updateDateTime(Path, Date)
	 */
	void updateDateTime(final File file, @Nullable final Date parentDateTime);

	/**
	 * @param file
	 * @param parentDateTime
	 * @see #updateDateTime(File, Date)
	 */
	default void updateDateTime(final Path file, @Nullable final Date parentDateTime) {
		this.updateDateTime(file.toFile(), parentDateTime);
	}
}
