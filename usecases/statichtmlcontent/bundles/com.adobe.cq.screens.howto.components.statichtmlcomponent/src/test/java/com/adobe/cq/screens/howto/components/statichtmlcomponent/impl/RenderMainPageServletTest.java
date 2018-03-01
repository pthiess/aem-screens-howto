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

import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import static org.apache.sling.testing.mock.sling.ResourceResolverType.RESOURCERESOLVER_MOCK;
import static org.junit.Assert.*;

public class RenderMainPageServletTest {

    private static final String SOME_CHANNEL_JCR_CONTENT = "/content/screens/someproject/somechannel/jcr:content";
    private static final String PAR_STATICCONTENT = "/par/staticcontent";

    @Rule
    public final AemContext context = new AemContext(RESOURCERESOLVER_MOCK);

    private RenderMainPageServlet servlet;
    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;
    private ResourceResolver resourceResolver;

    @Before
    public void setUp() throws Exception {
        servlet = new RenderMainPageServlet();
        request = context.request();
        response = context.response();
        resourceResolver = context.resourceResolver();
    }

    @Test
    public void doGetReturnsEmptyHtmlWhenComponentWasAddedButNoZipUploaded() throws Exception {

        context.load().json("/staticcontent-is-empty.json", SOME_CHANNEL_JCR_CONTENT);
        Resource resource = resourceResolver.getResource(SOME_CHANNEL_JCR_CONTENT + PAR_STATICCONTENT);
        request.setResource(resource);

        servlet.doGet(request, response);

        assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
        assertEquals(response.getContentType(), "text/html");
        assertEquals(response.getOutputAsString(), "");
    }

    @Test
    public void doGetReturnsEmptyHtmlWhenComponentWasAddedAndZipUploadedButNotSaved() throws Exception {

        context.load().json("/staticcontent-contains-sftmp-file.json", SOME_CHANNEL_JCR_CONTENT);
        Resource resource = resourceResolver.getResource(SOME_CHANNEL_JCR_CONTENT + PAR_STATICCONTENT);
        request.setResource(resource);

        servlet.doGet(request, response);

        assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
        assertEquals(response.getContentType(), "text/html");
        assertEquals(response.getOutputAsString(), "");
    }

    @Test
    public void doGetReturnsEmptyHtmlWhenComponentWasAddedButContentNodeMissing() throws Exception {

        context.load().json("/staticcontent-misses-content-node.json", SOME_CHANNEL_JCR_CONTENT);
        Resource resource = resourceResolver.getResource(SOME_CHANNEL_JCR_CONTENT + PAR_STATICCONTENT);
        request.setResource(resource);

        servlet.doGet(request, response);

        assertEquals(response.getStatus(), HttpServletResponse.SC_CONFLICT);
        assertEquals(response.getContentType(), "text/html");
        assertEquals(response.getOutputAsString(), "");
    }

    @Test
    public void doGetReturnsEmptyHtmlWhenComponentWasAddedButMainPageMissing() throws Exception {

        context.load().json("/staticcontent-misses-main-page-node.json", SOME_CHANNEL_JCR_CONTENT);
        Resource resource = resourceResolver.getResource(SOME_CHANNEL_JCR_CONTENT + PAR_STATICCONTENT);
        request.setResource(resource);

        servlet.doGet(request, response);

        assertEquals(response.getStatus(), HttpServletResponse.SC_CONFLICT);
        assertEquals(response.getContentType(), "text/html");
        assertEquals(response.getOutputAsString(), "");
    }

    @Test
    public void doGetReturnsEmptyHtmlWhenComponentWasAddedButMainPageCannotBeServed() throws Exception {

        context.load().json("/staticcontent-misses-main-page-binary.json", SOME_CHANNEL_JCR_CONTENT);
        Resource resource = resourceResolver.getResource(SOME_CHANNEL_JCR_CONTENT + PAR_STATICCONTENT);
        request.setResource(resource);

        servlet.doGet(request, response);

        assertEquals(response.getStatus(), HttpServletResponse.SC_CONFLICT);
        assertEquals(response.getContentType(), "text/html");
        assertEquals(response.getOutputAsString(), "");
    }

    @Test
    public void doGetReturnsHtmlWhenComponentWasAddedAndZipUploadedAndSaved() throws Exception {

        context.load().json("/staticcontent-contains-extracted-zip.json", SOME_CHANNEL_JCR_CONTENT);
        context.load().binaryFile("/index.html", SOME_CHANNEL_JCR_CONTENT + PAR_STATICCONTENT + "/content/index.html");
        Resource resource = resourceResolver.getResource(SOME_CHANNEL_JCR_CONTENT + PAR_STATICCONTENT);
        request.setResource(resource);

        servlet.doGet(request, response);

        assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
        assertEquals(response.getContentType(), "text/html");
        assertTrue(response.getOutputAsString().contains("<body>My Test</body>"));
    }
}