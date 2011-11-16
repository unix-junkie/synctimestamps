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
				ioe.printStackTrace();
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
			try {
				final DateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
				return format.parse(value);
			} catch (final ParseException pe) {
				pe.printStackTrace();
				return null;
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
