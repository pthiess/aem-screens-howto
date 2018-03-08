/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2017 Adobe Systems Incorporated
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.jcr.Binary;
import javax.jcr.RepositoryException;

public class JcrBinaryFromFile implements Binary {
    private static final Logger log = LoggerFactory.getLogger(JcrBinaryFromFile.class);

    private FileInputStream fis;
    public JcrBinaryFromFile(File file) throws FileNotFoundException {
        fis = new FileInputStream(file);
    }
    @Override
    public InputStream getStream() throws RepositoryException {
        return fis;
    }

    @Override
    public int read(byte[] b, long position) throws IOException, RepositoryException {
        int wanted = (fis.available() - (int)position);
        return fis.read(b, (int)position, Math.min(b.length, wanted));
    }

    @Override
    public long getSize() throws RepositoryException {
        try {
            return (long)fis.available();
        } catch (IOException e) {
            return 0;
        }
    }

    @Override
    public void dispose() {
        try {
            fis.close();
        } catch (IOException e) {
            log.error("Could not close the fileInputStream", e);
        }
    }
}
