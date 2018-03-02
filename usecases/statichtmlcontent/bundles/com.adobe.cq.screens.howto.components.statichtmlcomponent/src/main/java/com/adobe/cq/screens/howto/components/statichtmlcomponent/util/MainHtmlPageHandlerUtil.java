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

import static org.apache.jackrabbit.JcrConstants.JCR_DATA;
import static org.apache.jackrabbit.JcrConstants.JCR_UUID;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import javafx.beans.binding.ObjectBinding;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.RepositoryException;

public class MainHtmlPageHandlerUtil {

    private static MainHtmlPageHandlerUtil sharedInstance;

    public static MainHtmlPageHandlerUtil getOrCreateSharedInstance() {
        if (null == sharedInstance) {
            sharedInstance = new MainHtmlPageHandlerUtil();
        }
        return sharedInstance;
    }

    public void addBaseToIndexFile(Resource indexHtmlContentRes, String rootPath, ResourceResolver resourceResolver) throws RepositoryException, IOException {

        InputStream indexIS = indexHtmlContentRes.getValueMap().get(JCR_DATA, InputStream.class);

        String content = readStringContent(indexIS);
        indexIS.close();

        Pattern pattern = Pattern.compile("<html.*>");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String base = String.format("<html>%s<base href=\"%s\">%s", System.lineSeparator(), rootPath, System.lineSeparator());
            String htmlOpenTag = matcher.group();
            String contentWithBase = content.replaceFirst(htmlOpenTag, base);

            replaceDataInResource(indexHtmlContentRes, contentWithBase, resourceResolver);
        }

    }

    private void replaceDataInResource(Resource resource, String data, ResourceResolver resourceResolver) throws PersistenceException {
        //hackish
        String name = resource.getName();
        Resource parentRes = resource.getParent();

        HashMap<String, Object> newVm = new HashMap<>(resource.getValueMap());

        if (newVm.containsKey(JCR_DATA)) {
            newVm.remove(JCR_UUID);
            newVm.remove(JCR_DATA);
        }
        newVm.put(JCR_DATA, data);

        resourceResolver.delete(resource);
        resourceResolver.create(parentRes, name, newVm);
    }

    private String readStringContent(InputStream is) throws IOException {
        String result = IOUtils.toString(is, StandardCharsets.UTF_8);
        return result;
    }

}
