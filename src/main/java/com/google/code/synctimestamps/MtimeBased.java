/*-
 * $Id$
 */
package com.google.code.synctimestamps;

import java.io.File;
import java.util.Date;

/**
 * @author Andrey ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class MtimeBased extends AbstractDateTimeProvider implements WritableDateTimeProvider {
	/**
	 * @see DateTimeProvider#getDateTime(File)
	 */
	@Override
	public Date getDateTime(final File file) {
		return new Date(file.lastModified());
	}

	/**
	 * @see WritableDateTimeProvider#setDateTime(File, Date)
	 */
	@Override
	public File setDateTime(final File file, final Date dateTime) {
		if (!this.getDateTime(file).equals(dateTime)) {
			final String fileName = file.getName();
			System.out.println("INFO: " + fileName + ": setting file MTime to " + dateTime);
			final long lastModified = dateTime.getTime();
			if (lastModified < 0 || !file.setLastModified(lastModified)) {
				System.out.println("ERROR: " + fileName + ": failed set file MTime to " + dateTime);
			}
		}
		return file;
	}
}
