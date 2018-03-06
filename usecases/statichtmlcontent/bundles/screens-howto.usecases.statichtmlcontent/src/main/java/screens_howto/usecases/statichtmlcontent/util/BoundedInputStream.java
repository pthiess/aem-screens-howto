/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2018 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 ************************************************************************/
package screens_howto.usecases.statichtmlcontent.util;

import java.io.InputStream;
import java.io.IOException;

public class BoundedInputStream extends InputStream {
    private final InputStream delegate;

    private final long size;

    private long position;

    /**
     * @param delegate Wrapped input stream
     * @param size Max number of bytes to read
     */
    public BoundedInputStream(InputStream delegate, long size) {
        this.delegate = delegate;
        this.size = size;
    }

    @Override
    public int read() throws IOException {
        if (isSizeExceeded()) {
            throw new IOException("Input stream exceeded size of " + size
                                  + " bytes");
        }
        int result = delegate.read();
        if (result != -1) {
            position++;
        }
        return result;
    }

    /**
     * @return Size of the stream
     */
    public long getSize() { return this.size; }

    /**
     * @return Number of supplied bytes
     */
    public long getPosition() {
        return position;
    }

    /**
     * @return True if number of supplied bytes reached defined max size
     */
    public boolean isSizeExceeded() {
        if (size < 0) {
            return false;
        }
        return position > size;
    }
}
