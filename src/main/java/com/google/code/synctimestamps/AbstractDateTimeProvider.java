/*-
 * $Id$
 */
package com.google.code.synctimestamps;

import java.io.File;
import java.util.Date;

import javax.annotation.Nullable;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public abstract class AbstractDateTimeProvider implements DateTimeProvider {
	private WritableDateTimeProvider next;

	/**
	 * @param next
	 */
	public final void setNext(final WritableDateTimeProvider next) {
		if (next == this) {
			throw new IllegalArgumentException();
		}
		this.next = next;
	}

	/**
	 * @see DateTimeProvider#updateDateTime(File, Date)
	 */
	@Override
	public final void updateDateTime(final File file, @Nullable final Date parentDateTime) {
		final boolean parentDateTimeNull = parentDateTime == null;

		@SuppressWarnings("null")
		final File targetFile = parentDateTimeNull || !(this instanceof WritableDateTimeProvider)
				? file
				: ((WritableDateTimeProvider) this).setDateTime(file, parentDateTime);

		if (this.next != null) {
			this.next.updateDateTime(targetFile, parentDateTimeNull ? this.getDateTime(file) : parentDateTime);
		}
	}
}
