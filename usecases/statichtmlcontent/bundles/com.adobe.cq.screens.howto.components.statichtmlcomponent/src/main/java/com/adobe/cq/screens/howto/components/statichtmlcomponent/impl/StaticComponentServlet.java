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

package com.adobe.cq.screens.howto.components.statichtmlcomponent.impl;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static org.apache.jackrabbit.JcrConstants.JCR_CONTENT;
import static org.apache.jackrabbit.JcrConstants.NT_FOLDER;

import com.adobe.cq.screens.howto.components.statichtmlcomponent.util.ContentToPersistantHandler;
import com.adobe.cq.screens.howto.components.statichtmlcomponent.util.MainHtmlPageHandler;
import com.adobe.cq.screens.howto.components.statichtmlcomponent.util.StaticContentZipUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.mime.MimeTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;

@SlingServlet(
    resourceTypes = {
        "screens-howto/components/screens/content/staticcontent"
    },
    selectors = "process",
    extensions = "json",
    methods = "POST"
)

/**
 * This servlet handles POST .unzip.json and GET .index.html requests for staticcomponent.
 * E.g. http://localhost:4502/content/screens/we-retail/channels/idle/_jcr_content/par/staticcontent.process.json
 *
 * obs: This class makes the assumption that the archive contains a index.html file (required as a entry point for the rendered content)
 *      A "base" tag is added inside the html (should be moved in another postprocess step) in order to make the component load the other
 *      local includes
 */
public class StaticComponentServlet extends SlingAllMethodsServlet {

    private final static Logger log = LoggerFactory.getLogger(StaticComponentServlet.class);
    private final static String MAIN_HTML_PAGE = "index.html";

    @Reference
    private transient MimeTypeService mimeTypeService = null;

    private transient StaticContentZipUtils zipUtils;
    private transient MainHtmlPageHandler mainHtmlPageHandler;
    private transient ContentToPersistantHandler contentToPersistantHandler;

    @Override
    public void init() throws ServletException {
        zipUtils = StaticContentZipUtils.getOrCreateSharedInstance(mimeTypeService);
        mainHtmlPageHandler = new MainHtmlPageHandler();
        contentToPersistantHandler = new ContentToPersistantHandler();

        //first add href base tag, then save as resource
        mainHtmlPageHandler.setNext(contentToPersistantHandler);
    }
  
    @Override
    protected void doPost(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response) throws IOException {

        ResourceResolver resourceResolver = request.getResourceResolver();


        // Return json
        response.setContentType("application/json");
        Resource staticComponent = request.getResource();
        String archiveRelPath = request.getParameter("file_path");

        if (archiveRelPath == null) {
            response.setStatus(SC_BAD_REQUEST);
            response.getWriter().print(buildMessageObject("Parameter 'file_path' is missing."));
            return;
        }

        try {
            Resource archiveRes = staticComponent.getChild(archiveRelPath);
            if (null == archiveRes) {
                response.setStatus(SC_NOT_FOUND);
                response.getWriter().print(buildMessageObject("Zip-file is missing."));
                return;
            }

            Resource contentRes = getContentResource(staticComponent, resourceResolver);
            contentToPersistantHandler.setResourceResolver(resourceResolver);
            contentToPersistantHandler.setDestination(contentRes);
            mainHtmlPageHandler.setRootPath(contentRes.getPath() + "/");

            if (!zipUtils.isZip(archiveRes)) {
                response.setStatus(SC_BAD_REQUEST);
                response.getWriter().print(buildMessageObject("Given file is not a zip file."));
                return;
            }

            if (!zipUtils.zipContainsFile(archiveRes, MAIN_HTML_PAGE)) {
                response.setStatus(SC_BAD_REQUEST);
                response.getWriter().print(buildMessageObject("Zip must contain " + MAIN_HTML_PAGE));
                return;
            }



            //first add href base tag, then save as resource
            boolean archiveUnzipped = zipUtils.extract(archiveRes, mainHtmlPageHandler);
            if (!archiveUnzipped) {
                response.setStatus(SC_BAD_REQUEST);
                response.getWriter().print(buildMessageObject("Could not unzip the input file."));
                return;
            }

            resourceResolver.commit();
            response.getWriter().print(buildMessageObject("OK"));

        } catch (RepositoryException | IOException e) {
            String message = String.format(" Error at zip file: %s ", e.getMessage());
            log.error(message);
            response.setStatus(SC_BAD_REQUEST);
            response.getWriter().print(buildMessageObject("Unexpected error while extracting the zip-file."));
            return;
        }
    }

    private Resource getContentResource(Resource staticComponent, ResourceResolver resourceResolver) throws PersistenceException {
        Resource contentRes = staticComponent.getChild("content");
        if (null != contentRes) {
            resourceResolver.delete(contentRes);
        }
        Map<String, Object> properties = new HashMap<String, Object>(1);
        properties.put(JcrConstants.JCR_PRIMARYTYPE, NT_FOLDER);
        contentRes = resourceResolver.create(staticComponent, "content", properties);

        return contentRes;
    }

    private String buildMessageObject(String msg) {
        JSONObject messageObject = new JSONObject();
        try {
            messageObject.put("status", msg);
            return messageObject.toString();
        } catch (JSONException e) {
            return msg;
        }

    }
}
