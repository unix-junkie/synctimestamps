/*-
 * $Id$
 */
package com.google.code.synctimestamps;

import static java.lang.String.format;
import static org.apache.log4j.Logger.getLogger;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * Returns image timestamp based on its file name, provided that one
 * matches a certain pattern.
 *
 * @author Andrey ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
@ManagedResource
public final class FileNameBased extends AbstractDateTimeProvider implements WritableDateTimeProvider {
	private static final Logger LOGGER = getLogger(FileNameBased.class);

	private Pattern fileNamePattern;

	/**
	 * Date format pattern used when parsing the first regex group
	 * extracted from file name.
	 */
	private String dateFormatPattern;

	private char separator;

	@Nullable
	@CheckForNull
	@ManagedAttribute
	public String getFileNamePattern() {
		return this.fileNamePattern.pattern();
	}

	/**
	 * @param fileNamePattern
	 */
	@Required
	public void setFileNamePattern(final String fileNamePattern) {
		this.fileNamePattern = Pattern.compile(fileNamePattern);
	}

	@Nullable
	@CheckForNull
	@ManagedAttribute
	public String getDateFormatPattern() {
		return this.dateFormatPattern;
	}

	/**
	 * @param dateFormatPattern
	 */
	@Required
	public void setDateFormatPattern(final String dateFormatPattern) {
		this.dateFormatPattern = dateFormatPattern;
	}

	@ManagedAttribute
	public char getSeparator() {
		return this.separator;
	}

	/**
	 * @param separator
	 */
	@Required
	public void setSeparator(final char separator) {
		this.separator = separator;
	}

	/**
	 * @see DateTimeProvider#getDateTime(File)
	 */
	@Override
	@Nullable
	@CheckForNull
	public Date getDateTime(final File file) {
		final String fileName = file.getName();
		final Matcher matcher = this.fileNamePattern.matcher(fileName);
		if (!matcher.matches()) {
			return null;
		}
		if (matcher.groupCount() != 2) {
			/*
			 * Assume the pattern contains exactly 2 groups.
			 */
			return null;
		}
		final String dateTime0 = matcher.group(1);
		try {
			final DateFormat format = new SimpleDateFormat(this.dateFormatPattern);
			final Date dateTime1 = format.parse(dateTime0);
			return dateTime1.getTime() < 0 ? null : dateTime1;
		} catch (final ParseException pe) {
			pe.printStackTrace();
			return null;
		}
	}

	/**
	 * @see WritableDateTimeProvider#setDateTime(File, Date)
	 */
	@Override
	public File setDateTime(final File file, @Nullable final Date dateTime) {
		final String fileName = file.getName();
		final Matcher matcher = this.fileNamePattern.matcher(fileName);

		/*
		 * File name w/o any datetime information.
		 */
		final String partialFileName;
		if (matcher.matches()) {
			final int groupCount = matcher.groupCount();
			if (groupCount != 2) {
				/*
				 * Assume the pattern contains exactly 2 groups.
				 */
				LOGGER.warn(format("Group count for this matcher: %d; expected: 2.", Integer.valueOf(groupCount)));
				return file;
			}
			partialFileName = matcher.group(2);
		} else {
			partialFileName = fileName;
		}

		final DateFormat format = new SimpleDateFormat(this.dateFormatPattern);
		final String targetFileName = format.format(dateTime) + this.separator + partialFileName;
		if (fileName.equals(targetFileName)) {
			return file;
		}

		final File targetFile = new File(file.getParentFile(), targetFileName);
		if (targetFile.exists()) {
			LOGGER.warn(format("I refuse to move %s to %s because the target file already exists.", fileName, targetFileName));
			return file;
		}

		LOGGER.info(format("Moving %s to %s", fileName, targetFileName));
		if (!file.renameTo(targetFile)) {
			LOGGER.warn(format("Failed to move %s to %s", fileName, targetFileName));
			return file;
		}

		return targetFile;
	}
}
