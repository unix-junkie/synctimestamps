/*-
 * $Id$
 */
package com.google.code.synctimestamps;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.constants.TiffTagConstants;

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

			final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
			final TiffField field = jpegMetadata.findEXIFValue(TiffTagConstants.TIFF_TAG_DATE_TIME);
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
			 * Silently return null if file format is unsupported
			 * (e.g.: *.avi, *.3gp)
			 */
			return null;
		}
	}
}
