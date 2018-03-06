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

import static org.apache.jackrabbit.JcrConstants.JCR_CONTENT;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.mime.MimeTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * StaticContentZipUtils
 * Singletone that extracts a zip archive located inside a existing resource and send the
 * folders and files details to StaticContentZipUtilsDelegate object
 *
 * Used by "StaticComponentServlet"
 *
 */
public class StaticContentZipUtils {

    private static StaticContentZipUtils sharedInstance;

    private static final Logger log = LoggerFactory.getLogger(StaticContentZipUtils.class);
    private static final String MIME_TYPE_ZIP = "application/zip";

    private MimeTypeService mimeTypeService;

    private StaticContentZipUtils (MimeTypeService mimeTypeService) {
        this.mimeTypeService = mimeTypeService;
    }

    public static StaticContentZipUtils getOrCreateSharedInstance(MimeTypeService mimeTypeService) {
        if (null == sharedInstance) {
            sharedInstance = new StaticContentZipUtils(mimeTypeService);
        }
        return sharedInstance;
    }

    /**
     * Checks the mimeType of a resource file against "application/zip"
     * @param archiveFile: the resourced to be checked
     * @return the result of the comparison
     */
    public boolean isZip(Resource archiveFile) {
        try {
            Node node = archiveFile.adaptTo(Node.class);
            Node content = node.getNode(Node.JCR_CONTENT);
            String mimeType = content.getProperty(javax.jcr.Property.JCR_MIMETYPE).getString();

            return MIME_TYPE_ZIP.equals(mimeType);
        } catch (RepositoryException e) {
            log.error("Repository exception when checking for zip content", e);
            return false;
        }
    }

    /**
     * Checks if zip contains a specific file.
     * @param archiveRes The resourece that represents the zip
     * @param fileName the file that shall be contained in the zip
     * @return Tells the consumer if the given zip file contains a specific file.
     * @throws IOException an exception
     */
    public boolean zipContainsFile(Resource archiveRes, String fileName) throws IOException {
        try {
            boolean success = false;
            ZipInputStream zin = getInputStream(archiveRes);
            if (null == zin) {
                return false;//todo: send error
            }
            ZipEntry entry;

            while (null != (entry = zin.getNextEntry()) && !success) {
                if (entry.isDirectory()) {
                    continue;
                }
                if (entry.getName().equals(fileName)) {
                    success = true;
                }
                zin.closeEntry();
            }
            return success;

        } catch (RepositoryException e) {
            log.error("Repository exception when checking for zip content", e);
            return false;
        }
    }

    private ZipInputStream getInputStream(Resource archiveRes) throws RepositoryException {

        Resource contentRes = archiveRes.getChild(JCR_CONTENT);//Node.JCR_CONTENT doesn't work
        if (null == contentRes) {
            return null;
        }
        ValueMap valueMap = contentRes.getValueMap();
        InputStream dataStream = valueMap.get(JcrConstants.JCR_DATA, InputStream.class);
        return new ZipInputStream(dataStream);
    }

    /**
     * Extracts the archive located inside the "resource"
     * Extracted values are sent to the specified delegate
     *
     * @param archiveRes The resource that contains the archive  (the parent (nt:file) of JCR_CONTENT node)
     * @param delegate Delegate objects that handle the unarchived folders and files
     *                 (see ContentToPersistantHandler and MainHtmlPageHandler)
     * @return true if archive was unzipped and with success, false otherwise
     * @throws RepositoryException thrown if input nodes do not exists
     * @throws IOException An exception
     */
    public boolean extract(Resource archiveRes, StaticContentZipUtilsDelegate delegate) throws RepositoryException, IOException {

        ZipInputStream zin = getInputStream(archiveRes);

        if (null == zin) {
            return false;
        }

        ZipEntry entry;
        while (null != (entry = zin.getNextEntry())) {
            if (entry.isDirectory()) {
                handleDir(entry, delegate);
            } else {
                handleFile(entry, zin, delegate);
            }
            zin.closeEntry();
        }

        zin.close();
        return true;
    }

    private void handleDir(ZipEntry entry, StaticContentZipUtilsDelegate delegate) throws PersistenceException {
        String filePath = entry.getName();
        delegate.handleDirectory(filePath);
    }

    private void handleFile(ZipEntry entry, InputStream zin, StaticContentZipUtilsDelegate delegate)
        throws RepositoryException, IOException {
        String fileName = entry.getName();

        //todo: check for a MAX size
        long size = entry.getSize();
        if (size > Integer.MAX_VALUE) {
            String message = String.format("File %s, exceeded the max size", fileName);
            log.error(message);
            return;
        }
        BoundedInputStream input = new BoundedInputStream(zin, size);//not sure if it's ok with getSize()
        String mineType = mimeTypeService.getMimeType(Text.getName(fileName));

        delegate.handleFile(fileName, mineType, input);
    }

}
