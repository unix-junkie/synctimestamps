/*-
 * $Id$
 */
package com.google.code.synctimestamps;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.base.Predicate;

/**
 * @author Andrey ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
abstract class Main {
	static final AbstractXmlApplicationContext CONTEXT = getContext();

	private static final boolean DAEMON = false;

	private Main() {
		assert false;
	}

	private static AbstractXmlApplicationContext getContext() {
		return new ClassPathXmlApplicationContext("conf/appContext.xml");
	}

	private static List<File> listFilesRecursively(final File root, final Predicate<File> predicate) {
		final List<File> files = new ArrayList<>();
		if (root.isDirectory()) {
			final File children[] = root.listFiles();
			/*
			 * For some reason, File.listFiles() may return null
			 * instead of a zero-length array.
			 */
			if (children != null) {
				for (@Nonnull final File child : root.listFiles()) {
					files.addAll(listFilesRecursively(child, predicate));
				}
			}
		} else if (predicate.apply(root)) {
			files.add(root);
		}

		return files;
	}

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(final String args[]) throws InterruptedException {
		if (args.length != 1) {
			System.err.println("Usage: " + Main.class.getName() + " <FILE>");
			return;
		}

		final String fileName = args[0];
		final File root = new File(fileName);

		/*
		 * 1. If EXIF is present, rename according to the EXIF data
		 * 2. If name matches the input pattern, rename
		 * 3. Rename according to the mtime
		 */
		@Nonnull
		@SuppressWarnings({ "null", "unchecked" })
		final Predicate<File> predicate = CONTEXT.getBean("predicate", Predicate.class);
		final DateTimeProvider exifBased = CONTEXT.getBean("exifBased", DateTimeProvider.class);
		for (@Nonnull final File file : listFilesRecursively(root, predicate)) {
			exifBased.updateDateTime(file, null);
		}

		if (DAEMON) {
			/*
			 * Preserve at least one non-daemon thread.
			 */
			while (true) {
				Thread.sleep(Long.MAX_VALUE);
			}
		}
	}
}
