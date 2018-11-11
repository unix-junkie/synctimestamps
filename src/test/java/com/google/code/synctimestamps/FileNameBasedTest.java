/*-
 * $Id$
 */
package com.google.code.synctimestamps;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.createFile;
import static java.nio.file.Files.createTempDirectory;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.getLastModifiedTime;
import static java.nio.file.Files.walkFileTree;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrey ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public class FileNameBasedTest {
	private static final ThreadLocal<Path> WORKING_DIR = new ThreadLocal<>();

	@SuppressWarnings("static-method")
	@Before
	public void setUp() throws IOException {
		final Path workingDir = createTempDirectory("synctimestamps-");
		WORKING_DIR.set(workingDir);
	}

	@SuppressWarnings("static-method")
	@After
	public void tearDown() throws IOException {
		final Path workingDir = WORKING_DIR.get();
		walkFileTree(workingDir, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(final Path file,
						final BasicFileAttributes attrs)
				throws IOException {
					delete(file);
					return CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(final Path file,
						final IOException ioe)
				throws IOException {
					ioe.printStackTrace();
					return CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(final Path dir,
						final IOException ioe)
				throws IOException {
					if (ioe != null) {
						ioe.printStackTrace();
					}

					delete(dir);
					return CONTINUE;
				}
		});
	}

	@SuppressWarnings("static-method")
	@Test
	public void testSingleWithDefaultPattern() throws IOException, ParseException {
		final Path workingDir = WORKING_DIR.get();

		final FileNameBased dateTimeProvider = new FileNameBased();
		dateTimeProvider.setFileNamePattern("(\\d{4}\\-\\d{2}\\-\\d{2}_\\d{2}\\-\\d{2}\\-\\d{2})_(.*)");
		dateTimeProvider.setDateFormatPattern("yyyy-MM-dd_HH-mm-ss");
		dateTimeProvider.setSeparator('_');

		final Date referenceDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("1971-04-12 09:07:00");

		final Path file0 = createFile(workingDir.resolve("1971-04-12_09-07-00_name.jpg"));
		final Date dateTime0 = dateTimeProvider.getDateTime(file0);
		assertThat(dateTime0).isNotNull().isEqualTo(referenceDate);
		assertThat(dateTime0.getTime()).isNotEqualTo(getLastModifiedTime(file0).toMillis());

		final Path file1 = createFile(workingDir.resolve("1971-04-12_09-07-00_name.mp4"));
		final Date dateTime1 = dateTimeProvider.getDateTime(file1);
		assertThat(dateTime1).isNotNull().isEqualTo(referenceDate);
		assertThat(dateTime1.getTime()).isNotEqualTo(getLastModifiedTime(file1).toMillis());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testNestedWithDefaultPattern() throws IOException, ParseException {
		final Path workingDir = WORKING_DIR.get();

		final FileNameBased dateTimeProvider = new FileNameBased();
		dateTimeProvider.setFileNamePattern("(\\d{4}\\-\\d{2}\\-\\d{2}_\\d{2}\\-\\d{2}\\-\\d{2})_(.*)");
		dateTimeProvider.setDateFormatPattern("yyyy-MM-dd_HH-mm-ss");
		dateTimeProvider.setSeparator('_');
		dateTimeProvider.setNext(new MtimeBased());

		final Date referenceDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("1971-04-12 09:07:00");

		final Path file0 = createFile(workingDir.resolve("1971-04-12_09-07-00_name.jpg"));

		final Path file1 = createFile(workingDir.resolve("1971-04-12_09-07-00_name.mp4"));

		dateTimeProvider.updateDateTime(file0, referenceDate);
		dateTimeProvider.updateDateTime(file1, referenceDate);

		assertThat(getLastModifiedTime(file0).toMillis()).isEqualTo(referenceDate.getTime());
		assertThat(getLastModifiedTime(file1).toMillis()).isEqualTo(referenceDate.getTime());
	}

	/**
	 * <p>Tests file name format used by <em>Xiaomi Redmi Note 4X</em>.</p>
	 *
	 * @throws IOException
	 * @throws ParseException
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testSingleWithPattern2() throws IOException, ParseException {
		final Path workingDir = WORKING_DIR.get();

		final FileNameBased dateTimeProvider = new FileNameBased();
		dateTimeProvider.setFileNamePattern(".+_(\\d{8}_\\d{6})(\\.[^\\.]+)");
		dateTimeProvider.setDateFormatPattern("yyyyMMdd_HHmmss");
		dateTimeProvider.setSeparator('_');

		final Date referenceDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("1971-04-12 09:07:00");

		final Path file0 = createFile(workingDir.resolve("img_19710412_090700.jpg"));
		final Date dateTime0 = dateTimeProvider.getDateTime(file0);
		assertThat(dateTime0).isNotNull().isEqualTo(referenceDate);
		assertThat(dateTime0.getTime()).isNotEqualTo(getLastModifiedTime(file0).toMillis());

		final Path file1 = createFile(workingDir.resolve("vid_19710412_090700.mp4"));
		final Date dateTime1 = dateTimeProvider.getDateTime(file1);
		assertThat(dateTime1).isNotNull().isEqualTo(referenceDate);
		assertThat(dateTime1.getTime()).isNotEqualTo(getLastModifiedTime(file1).toMillis());
	}

	/**
	 * <p>Tests file name format used by <em>WhatsApp</em>.</p>
	 *
	 * @throws IOException
	 * @throws ParseException
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testSingleWithPattern3() throws IOException, ParseException {
		final Path workingDir = WORKING_DIR.get();

		final FileNameBased dateTimeProvider = new FileNameBased();
		dateTimeProvider.setFileNamePattern(".+\\-(\\d{8})(\\-wa\\d{4}\\.[^\\.]+)");
		dateTimeProvider.setDateFormatPattern("yyyyMMdd");
		dateTimeProvider.setSeparator('_');

		final Date referenceDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("1971-04-12 00:00:00");

		final Path file0 = createFile(workingDir.resolve("img-19710412-wa0041.jpg"));
		final Date dateTime0 = dateTimeProvider.getDateTime(file0);
		assertThat(dateTime0).isNotNull().isEqualTo(referenceDate);
		assertThat(dateTime0.getTime()).isNotEqualTo(getLastModifiedTime(file0).toMillis());

		final Path file1 = createFile(workingDir.resolve("vid-19710412-wa0004.mp4"));
		final Date dateTime1 = dateTimeProvider.getDateTime(file1);
		assertThat(dateTime1).isNotNull().isEqualTo(referenceDate);
		assertThat(dateTime1.getTime()).isNotEqualTo(getLastModifiedTime(file1).toMillis());
	}
}
