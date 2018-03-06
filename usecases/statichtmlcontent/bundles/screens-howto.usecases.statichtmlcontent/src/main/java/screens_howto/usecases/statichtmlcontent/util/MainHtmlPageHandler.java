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

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.RepositoryException;

/**
 * MainHtmlPageHandler
 *
 * Specialized implementation on StaticContentZipUtilsDelegate
 *      - handles only "handleFile" callback from handleFile
 *          (adds href to the content of index.html)
 *      - calls the same callback on the next object, if set
 */
public class MainHtmlPageHandler implements StaticContentZipUtilsDelegate {

    private final static Logger log = LoggerFactory.getLogger(MainHtmlPageHandler.class);

    private final static String MAIN_HTML_PAGE = "index.html";

    private StaticContentZipUtilsDelegate nextDelegate;
    private String rootPath;


    public MainHtmlPageHandler() {
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    private String readStringContent(InputStream is) throws IOException {
        String result = IOUtils.toString(is, StandardCharsets.UTF_8);
        return result;
    }

    @Override
    public void setNext(StaticContentZipUtilsDelegate delegate) {
        nextDelegate = delegate;
    }

    @Override
    public void handleDirectory(String dirPath) throws PersistenceException {
        //nothing to do here, just pass to next
        if (null != nextDelegate) {
            nextDelegate.handleDirectory(dirPath);
        }
    }

    @Override
    public void handleFile(String fileRelPath, String mimeType, InputStream content) throws PersistenceException, RepositoryException {
        //do process here
        int lastPos = fileRelPath.lastIndexOf(47);
        String name = fileRelPath.substring(lastPos + 1);

        InputStream nextInputStream = content;
        if (name.equals(MAIN_HTML_PAGE)) {
            try {
                String contentStr = readStringContent(content);
                Pattern pattern = Pattern.compile("<html.*>");
                Matcher matcher = pattern.matcher(contentStr);
                if (matcher.find()) {
                    String base = String.format("<html>%s<base href=\"%s\">%s", System.lineSeparator(), rootPath, System.lineSeparator());
                    String htmlOpenTag = matcher.group();
                    String contentWithBase = contentStr.replaceFirst(htmlOpenTag, base);
                    nextInputStream = IOUtils.toInputStream(contentWithBase, StandardCharsets.UTF_8);
                }

            } catch (IOException e) {
                log.error("Could not handle " + MAIN_HTML_PAGE);
            }
        }
        //pass to next
        if (null != nextDelegate) {
            nextDelegate.handleFile(fileRelPath, mimeType, nextInputStream);
        }
    }
}
