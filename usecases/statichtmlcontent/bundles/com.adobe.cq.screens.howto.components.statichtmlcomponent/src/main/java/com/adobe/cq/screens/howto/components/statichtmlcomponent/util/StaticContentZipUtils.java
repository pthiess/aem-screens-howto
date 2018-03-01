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
package com.adobe.cq.screens.howto.components.statichtmlcomponent.util;

import static com.day.cq.commons.jcr.JcrConstants.JCR_DATA;
import static com.day.cq.commons.jcr.JcrConstants.JCR_LASTMODIFIED;
import static com.day.cq.commons.jcr.JcrConstants.JCR_MIMETYPE;
import static org.apache.jackrabbit.JcrConstants.NT_FILE;
import static org.apache.jackrabbit.JcrConstants.NT_FOLDER;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.mime.MimeTypeService;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.Nonnull;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;


/**
 * StaticContentZipUtils
 * Singletone that extracts a zip archive located inside a existing node and saves the extrated files and folders
 * under a specified nt:folder
 * Used by "StaticComponentServlet"
 *
 */
public class StaticContentZipUtils {

    private static StaticContentZipUtils sharedInstance;

    private static final Logger log = LoggerFactory.getLogger(StaticContentZipUtils.class);
    private static final String MIME_TYPE_ZIP = "application/zip";

    private ResourceUtilWrapper resourceUtil;
    private MimeTypeService mimeTypeService;

    private StaticContentZipUtils (MimeTypeService mimeTypeService) {
        resourceUtil = ResourceUtilWrapper.getSharedInstance();
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

        Resource contentRes = archiveRes.getChild("jcr:content");//Node.JCR_CONTENT doesn't work
        if (null == contentRes) {
            return null;
        }
        ValueMap valueMap = contentRes.getValueMap();
        InputStream dataStream = valueMap.get("jcr:data", InputStream.class);
        return new ZipInputStream(dataStream);
    }

    /**
     * Extracts the archive located inside the "node" and saves the extracted files under "destination"
     * Added to this is the special treatment of the "index.html" file, a base tag is added at the beginning of the "html" tag.
     *
     * @param archiveRes The resource that contains the archive  (the parent (nt:file) of JCR_CONTENT node)
     * @param destResource The nt:folder where to save the extracted files
     * @param resourceResolver The resource resolver
     * @return true if archive was unzipped and saved with success, false otherwise
     * @throws RepositoryException thrown if input nodes do not exists
     * @throws IOException An exception
     */
    public boolean extract(Resource archiveRes, Resource destResource, ResourceResolver resourceResolver) throws RepositoryException, IOException {

        ZipInputStream zin = getInputStream(archiveRes);

        if (null == zin) {
            return false;
        }

        String rootPath = destResource.getPath() + "/";
        ZipEntry entry;
        while (null != (entry = zin.getNextEntry())) {
            if (entry.isDirectory()) {
                handleDir(entry, rootPath, resourceResolver);
            } else {
                handleFile(entry, zin, rootPath, resourceResolver);
            }
            zin.closeEntry();
        }

        zin.close();
        return true;
    }

    private void handleDir(ZipEntry entry, String rootPath, ResourceResolver resourceResolver) throws PersistenceException {
        String filePath = (rootPath + entry.getName());

        resourceUtil.getOrCreateResource(resourceResolver, filePath, JcrResourceConstants.NT_SLING_FOLDER,
                                         JcrResourceConstants.NT_SLING_FOLDER, true);

    }

    private void handleFile(ZipEntry entry, InputStream zin, String rootPath, ResourceResolver resourceResolver)
        throws RepositoryException, IOException {
        String fileName = entry.getName();
        String filePath = (rootPath + fileName);

        //todo: check for a MAX size
        long size = entry.getSize();
        if (size > Integer.MAX_VALUE) {
            String message = String.format("File %s, exceeded the max size", fileName);
            log.error(message);
            return;
        }
        BoundedInputStream input = new BoundedInputStream(zin, size);//not sure if it's ok with getSize()
        String mineType = mimeTypeService.getMimeType(Text.getName(fileName));
        createFileNode(filePath, input, mineType, resourceResolver);
    }

    private void createFileNode(String filePath, BoundedInputStream content, String mimeType, ResourceResolver resourceResolver)
        throws PersistenceException, RepositoryException {
        int lastPos = filePath.lastIndexOf(47);
        String name = filePath.substring(lastPos + 1);
        Resource parentResource;
        if (lastPos == 0) {
            parentResource = resourceResolver.getResource("/");
        } else {
            String pe = filePath.substring(0, lastPos);
            parentResource = resourceUtil.getOrCreateResource(resourceResolver, pe, JcrResourceConstants.NT_SLING_FOLDER,
                                                              JcrResourceConstants.NT_SLING_FOLDER, true);
        }

        //add new file resource
        Map<String, Object> fileProperties = new HashMap<String, Object>(1);
        fileProperties.put(JcrConstants.JCR_PRIMARYTYPE, NT_FILE);
        Resource fileRes = resourceResolver.create(parentResource, name, fileProperties);

        //add content to file
        Map<String, Object> fileContentProperties = new HashMap<String, Object>(4);
        fileContentProperties.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_RESOURCE);
        fileContentProperties.put(JCR_MIMETYPE, mimeType);
        fileContentProperties.put(JCR_DATA, content);
        fileContentProperties.put(JCR_LASTMODIFIED, Calendar.getInstance());
        resourceResolver.create(fileRes, JcrConstants.JCR_CONTENT, fileContentProperties);
    }

    public static class ResourceUtilWrapper {

        private static ResourceUtilWrapper sharedInstance;

        public static ResourceUtilWrapper getSharedInstance() {
            if (null == sharedInstance) {
                sharedInstance = new ResourceUtilWrapper();
            }
            return sharedInstance;
        }

        public Resource getOrCreateResource(@Nonnull ResourceResolver resolver, @Nonnull String path, String resourceType,
                                            String intermediateResourceType, boolean autoCommit) throws PersistenceException {
            return ResourceUtil.getOrCreateResource(resolver, path, resourceType, intermediateResourceType, autoCommit);
        }

    }
}
