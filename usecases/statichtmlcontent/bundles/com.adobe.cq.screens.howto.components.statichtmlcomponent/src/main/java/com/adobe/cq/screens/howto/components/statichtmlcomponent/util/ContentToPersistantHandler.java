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

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.jcr.RepositoryException;

public class ContentToPersistantHandler implements StaticContentZipUtilsDelegate {

    private final static Logger log = LoggerFactory.getLogger(ContentToPersistantHandler.class);

    private StaticContentZipUtilsDelegate nextDelegate;
    private ResourceUtilWrapper resourceUtil;
    private String rootPath;
    private ResourceResolver resourceResolver;

    public ContentToPersistantHandler() {
        resourceUtil = ResourceUtilWrapper.getSharedInstance();
    }

    public void setResourceResolver(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
    }

    public void setDestination(Resource destination) {
        this.rootPath = destination.getPath() + "/";
    }

    @Override
    public void setNext(StaticContentZipUtilsDelegate delegate) {
        nextDelegate = delegate;
    }

    @Override
    public void handleDirectory(String dirPath) throws PersistenceException {
        String filePath = (rootPath + dirPath);
        //save as resource
        resourceUtil.getOrCreateResource(resourceResolver, filePath, JcrResourceConstants.NT_SLING_FOLDER,
                                             JcrResourceConstants.NT_SLING_FOLDER, true);
        //send to next if any
        if (null != nextDelegate) {
            nextDelegate.handleDirectory(dirPath);
        }
    }

    @Override
    public void handleFile(String fileRelPath, String mimeType, InputStream content) throws RepositoryException, PersistenceException {
        String filePath = (rootPath + fileRelPath);
        createFileResource(filePath, content, mimeType, resourceResolver);

        //send to next if any
        if (null != nextDelegate) {
            nextDelegate.handleFile(fileRelPath, mimeType, content);
        }
    }

    private void createFileResource(String filePath, InputStream content, String mimeType, ResourceResolver resourceResolver)
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

    protected static class ResourceUtilWrapper {

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
