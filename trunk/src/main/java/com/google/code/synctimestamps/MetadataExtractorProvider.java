/*-
 * $Id$
 */
package com.google.code.synctimestamps;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.ExifReader;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class MetadataExtractorProvider extends AbstractDateTimeProvider implements ExifBased {
	/**
	 * @see DateTimeProvider#getDateTime(File)
	 */
	@Override
	public Date getDateTime(final File file) {
		try {
			final ImageInputStream in = ImageIO.createImageInputStream(file);
			final Iterator<ImageReader> it = ImageIO.getImageReaders(in);
			if (!it.hasNext()) {
				return null;
			}

			final ImageReader imageReader = it.next();
			imageReader.setInput(in);

			final IIOMetadata imageMetadata = imageReader.getImageMetadata(0);

			final ExifReader exifReader = new ExifReader(getExifInfo(imageMetadata));
			final Metadata metadata = exifReader.extract();
			final Directory directory = metadata.getDirectory(ExifDirectory.class);
			return directory.getDate(ExifDirectory.TAG_DATETIME);
		} catch (final IOException ioe) {
			ioe.printStackTrace();
			return null;
		} catch (final MetadataException me) {
			me.printStackTrace();
			return null;
		}
	}

	/**
	 * @param imageMetadata
	 */
	private static byte[] getExifInfo(final IIOMetadata imageMetadata) {
		final IIOMetadataNode node = (IIOMetadataNode) imageMetadata.getAsTree("javax_imageio_jpeg_image_1.0");
		return (byte[]) findExifNode(node).getUserObject();
	}

	/**
	 * @param node
	 */
	private static IIOMetadataNode findExifNode(final IIOMetadataNode node) {
		if (isExifNode(node)) {
			return node;
		}

		for (IIOMetadataNode child = (IIOMetadataNode) node.getFirstChild(); child != null; child = (IIOMetadataNode) child.getNextSibling()) {
			final IIOMetadataNode exifInfo = findExifNode(child);
			if (exifInfo == null) {
				continue;
			}
			return exifInfo;
		}
		return null;
	}

	/**
	 * @param node
	 */
	private static boolean isExifNode(final Node node) {
		return node.getNodeType() == Node.ELEMENT_NODE
				&& node.getLocalName().equals("unknown")
				&& hasMarkerTag(node);
	}

	/**
	 * @param node
	 */
	private static boolean hasMarkerTag(final Node node) {
		final NamedNodeMap attributes = node.getAttributes();
		if (attributes == null) {
			return false;
		}

		final Node markerTag = attributes.getNamedItem("MarkerTag");
		if (markerTag == null) {
			return false;
		}

		try {
			final int markerTagValue = Integer.parseInt(markerTag.getNodeValue());
			return markerTagValue == 0xe1;
		} catch (final NumberFormatException nfe) {
			return false;
		}
	}
}
