package com.vincent.core.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;

/**
 * 
 * @author Vincent To redirect System output stream to log
 *
 */
public class LoggingOutputStream extends OutputStream {

	private static final int DEFAULT_BUFFER_LENGTH = 2048;
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private boolean hasBeenClosed = false;
	private byte[] buffer;
	private int count;
	private int curBufLength;
	private Log log = null;
	private int level;

	public LoggingOutputStream(Log log, int level) {
		if (log == null) {
			throw new IllegalArgumentException("log must not be null.");
		}

		if (level < 1 || level > 6) {
			throw new IllegalArgumentException("log level must be 1 - 6 .");
		}

		this.log = log;
		this.level = level;
		curBufLength = DEFAULT_BUFFER_LENGTH;
		buffer = new byte[curBufLength];
		count = 0;
	}

	@Override
	public void write(int b) throws IOException {
		try {
			if (this.hasBeenClosed) {
				throw new IOException("The stream has been closed.");
			}
			// don't log null
			if (b == 0) {
				return;
			}
			// is the buffer full?
			if (count == curBufLength) {
				// grow the buffer
				final int newBuffLength = curBufLength + DEFAULT_BUFFER_LENGTH;
				final byte[] newBuffer = new byte[newBuffLength];
				System.arraycopy(buffer, 0, newBuffer, 0, curBufLength);
				buffer = newBuffer;
				curBufLength = newBuffLength;
			}

			buffer[count] = (byte) b;
			count++;
		} catch (Throwable t) {
			FileOutputStream out = null;
			try {
				out = FileUtils.openOutputStream(new File("LoggingOutputStream_error.txt"));
				t.printStackTrace(new PrintStream(out));
			} catch (Throwable t2) {

			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (Throwable t3) {

					}

				}
			}
		}
	}

	@Override
	public void flush() {
		if (count == 0) {
			return;
		}
		try {
			if (count == LINE_SEPARATOR.length()) {
				if (((char) buffer[0]) == LINE_SEPARATOR.charAt(0)
						&& ((count == 1) || ((count == 2) && ((char) buffer[1]) == LINE_SEPARATOR.charAt(1)))) {
					reset();
					return;
				}
			}

			final byte[] bytes = new byte[count];
			System.arraycopy(buffer, 0, bytes, 0, count);
			String str = new String(bytes);

			switch (this.level) {
			case 1:
				log.trace(str);
				break;
			case 2:
				log.debug(str);
				break;
			case 3:
				log.info(str);
				break;
			case 4:
				log.warn(str);
				break;
			case 5:
				log.error(str);
				break;
			case 6:
				log.fatal(str);
				break;
			default:
				break;
			}
		} catch (Throwable t) {
			FileOutputStream out = null;
			try {
				out = FileUtils.openOutputStream(new File("LoggingOutputStream_error.txt"));
				t.printStackTrace(new PrintStream(out));
			} catch (Throwable t2) {

			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (Throwable t3) {

					}

				}
			}
		}
		count = 0;
	}

	@Override
	public void close() {
		flush();
		hasBeenClosed = true;
	}

	private void reset() {
		count = 0;

	}

}
