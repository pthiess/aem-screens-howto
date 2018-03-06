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

package screens_howto.usecases.statichtmlcontent.impl;

import static javax.servlet.http.HttpServletResponse.SC_CONFLICT;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import com.day.cq.commons.jcr.JcrConstants;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;

@SlingServlet(
        resourceTypes = {
                "screens-howto/components/screens/content/staticcontent"
        },
        selectors = "index",
        extensions = "html",
        methods = "GET"
)

/**
 * This servlet handles GET .index.html requests for staticcomponent.
 * E.g. http://localhost:4502/content/screens/we-retail/channels/idle/_jcr_content/par/staticcontent.index.html
 *
 */
public class RenderMainPageServlet extends SlingAllMethodsServlet {

    private final static Logger log = LoggerFactory.getLogger(RenderMainPageServlet.class);
    private final static String MAIN_HTML_PAGE = "index.html";

    /**
     *  Reads the main page and serves the content.
     */
    @Override
    protected void doGet(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response)
            throws ServletException, IOException {

        Resource resource = request.getResource();

        response.setContentType("text/html");

        if (!hasArchive(resource)) {
            response.setStatus(SC_OK);
            response.getWriter().print("");
            return;
        }

        Resource mainPage = getMainPageJcrContentNode(resource);

        if (null == mainPage) {
            response.sendError(SC_CONFLICT, "Conflict while resolving main page.");
            return;
        }

        final InputStream inputStream;

        try {
            inputStream = mainPage.adaptTo(InputStream.class);
        } catch (Exception e) {
            response.sendError(SC_CONFLICT, "Cannot serve main page.");
            return;
        }

        response.setStatus(SC_OK);
        // Write in blocks instead of copying the entire main page into Java's memory.
        // The following writes it in blocks of 10KB.
        byte[] buffer = new byte[10240];
        // Using try-with-resources syntax so that the stream automatically closes after the program is finished.
        try (OutputStream output = response.getOutputStream()) {
            for (int length = 0; (length = inputStream.read(buffer)) > 0;) {
                output.write(buffer, 0, length);
            }
        }

    }

    /**
     *  Helper method to access the child node (jcr:content) of the main page nt:file node
     * @return Resource object located at component_path/content/index.html/jcr:content
     */
    private Resource getMainPageJcrContentNode(Resource resource) {
        Resource resourceContent = resource.getChild("content");
        if (null == resourceContent) {
            return null;
        }

        Resource index = resourceContent.getChild(MAIN_HTML_PAGE);
        if (null == index) {
            return null;
        }
        Resource jcrContent = index.getChild(JcrConstants.JCR_CONTENT);
        if (null == jcrContent) {
            return null;
        }

        return jcrContent;
    }

    private boolean hasArchive(Resource resource) {
        return null != resource.getChild("file");
    }
}
