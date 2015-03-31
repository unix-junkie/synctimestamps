/*-
 * $Id$
 */
package com.google.code.synctimestamps;

import static java.util.Arrays.asList;
import static org.apache.sanselan.formats.tiff.constants.ExifTagConstants.EXIF_TAG_CREATE_DATE;
import static org.apache.sanselan.formats.tiff.constants.ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL;
import static org.apache.sanselan.formats.tiff.constants.TiffTagConstants.TIFF_TAG_DATE_TIME;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.constants.TagInfo;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class SanselanProvider extends AbstractDateTimeProvider implements ExifBased {
	private static final String DATE_PATTERNS[] = {
		"yyyy:MM:dd HH:mm:ss",
		"yyyy-MM-dd HH:mm:ss",
	};

	/**
	 * @see DateTimeProvider#getDateTime(File)
	 */
	@Override
	@Nullable
	@CheckForNull
	public Date getDateTime(final File file) {
		try {
			final IImageMetadata metadata;
			try {
				metadata = Sanselan.getMetadata(file);
			} catch (final IOException ioe) {
				System.out.println("ERROR: " + file.getPath() + ": " + ioe.getMessage());
				return null;
			}
			if (!(metadata instanceof JpegImageMetadata)) {
				return null;
			}

			/*-
			 * Certain digital cameras don't add any DateTime tag,
			 * but still add DateTimeOriginal and/or DateTimeDigitized.
			 */
			final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
			for (@Nonnull final TagInfo tagInfo : asList(TIFF_TAG_DATE_TIME, EXIF_TAG_DATE_TIME_ORIGINAL, EXIF_TAG_CREATE_DATE)) {
				final Date dateTime = getDateTime(file, jpegMetadata, tagInfo);
				if (dateTime == null) {
					continue;
				}
				return dateTime;
			}

			return null;
		} catch (final ImageReadException ire) {
			/*
			 * Silently return null if file format is unsupported
			 * (e.g.: *.avi, *.3gp)
			 */
			return null;
		}
	}

	/**
	 * @param file
	 * @param jpegMetadata
	 * @param tagInfo
	 */
	@Nullable
	@CheckForNull
	private static Date getDateTime(final File file, final JpegImageMetadata jpegMetadata, final TagInfo tagInfo) {
		try {
			final TiffField field = jpegMetadata.findEXIFValue(tagInfo);
			if (field == null) {
				return null;
			}
			final String value = (String) field.getValue();
			/*
			 * Malformed EXIF entry.
			 */
			if (value.equals("0000:00:00 00:00:00\0")) {
				return null;
			}
			try {
				final DateFormat format = new SimpleDateFormat(DATE_PATTERNS[0]);
				return format.parse(value);
			} catch (final ParseException pe0) {
				try {
					final DateFormat format = new SimpleDateFormat(DATE_PATTERNS[1]);
					return format.parse(value);
				} catch (final ParseException pe1) {
					System.out.println("ERROR: " + file.getPath() + ": " + pe1.getMessage());
					return null;
				}
			}
		} catch (final ImageReadException ire) {
			/*
			 * Never.
			 */
			ire.printStackTrace();
			return null;
		}
	}
}
