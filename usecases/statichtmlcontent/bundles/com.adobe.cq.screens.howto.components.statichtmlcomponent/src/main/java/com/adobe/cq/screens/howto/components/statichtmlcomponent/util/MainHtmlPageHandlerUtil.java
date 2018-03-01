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

        BoundedInputStream is = new BoundedInputStream(indexIS, indexIS.available());

        String content = readStringContent(is);
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

    private String readStringContent(BoundedInputStream is) throws IOException {
        int size = new BigDecimal(is.getSize()).intValueExact();

        StringBuffer content = new StringBuffer();
        int possition = 0;
        //no sure if required
        boolean keepReading = true;
        while (keepReading) {//hackish because of BoundedInputStream initialization

            int byteArraySize = 0;
            if (size > 0) {
                byteArraySize = size - possition;
            } else {
                byteArraySize = 512;
            }

            byte[] byteContent = new byte[byteArraySize];
            int read = is.read(byteContent);
            possition += read;
            content.append(new String(byteContent, StandardCharsets.UTF_8.name()));

            if (read < 0) {
                keepReading = false;
                break;
            } else if (size > 0){

                if (possition >= size) {
                    keepReading = false;
                    break;
                }
            }
        }

        return content.toString();
    }

}
