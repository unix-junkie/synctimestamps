/*-
 * $Id$
 */
package com.google.code.synctimestamps;

import static java.lang.String.format;
import static org.apache.log4j.Logger.getLogger;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author Andrey ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class MtimeBased extends AbstractDateTimeProvider implements WritableDateTimeProvider {
	private static final Logger LOGGER = getLogger(MtimeBased.class);

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
			LOGGER.info(format("%s: setting file MTime to %s", fileName, dateTime));
			final long lastModified = dateTime.getTime();
			if (lastModified < 0 || !file.setLastModified(lastModified)) {
				LOGGER.error(format("%s: failed to set file MTime to %s", fileName, dateTime));
			}
		}
		return file;
	}
}
