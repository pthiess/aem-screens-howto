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

import static org.apache.sling.testing.mock.sling.ResourceResolverType.RESOURCERESOLVER_MOCK;

import org.apache.commons.collections.map.HashedMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;

import io.wcm.testing.mock.aem.junit.AemContext;
import static org.junit.Assert.*;


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import com.adobe.cq.screens.howto.components.statichtmlcomponent.util.ContentToPersistantHandler;
import com.adobe.cq.screens.howto.components.statichtmlcomponent.util.MainHtmlPageHandler;
import com.adobe.cq.screens.howto.components.statichtmlcomponent.util.StaticContentZipUtils;
import com.adobe.cq.screens.howto.components.statichtmlcomponent.util.StaticContentZipUtilsDelegate;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletResponse;

public class StaticComponentServletTest {

    private static final String SOME_CHANNEL_JCR_CONTENT = "/content/screens/someproject/somechannel/jcr:content";
    private static final String PAR_STATICCONTENT = "/par/staticcontent";

    private static String ARCHIVE_REL_PATH = "file";

    @Rule
    public final AemContext context = new AemContext(RESOURCERESOLVER_MOCK);

    @Mock
    private StaticContentZipUtils staticContentZipUtilsMock;
    @Mock
    private MainHtmlPageHandler mainHtmlPageHandlerMock;
    @Mock
    private ContentToPersistantHandler contentToPersistantHandlerMock;

    private StaticComponentServlet servlet;

    private ResourceResolver resourceResolver;
    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;

    @Before
    public void setUp() throws Exception {
        resourceResolver = context.resourceResolver();
        servlet = new StaticComponentServlet();
        request = context.request();
        response = context.response();

        initMocks();
    }

    private void initMocks() throws IOException, RepositoryException {
        staticContentZipUtilsMock = mock(StaticContentZipUtils.class);
        mainHtmlPageHandlerMock = mock(MainHtmlPageHandler.class);
        contentToPersistantHandlerMock = mock(ContentToPersistantHandler.class);

        when(staticContentZipUtilsMock.isZip(any(Resource.class))).thenReturn(true);
        when(staticContentZipUtilsMock.zipContainsFile(any(Resource.class), anyString())).thenReturn(true);
        when(staticContentZipUtilsMock.extract(any(Resource.class),
                                               any(StaticContentZipUtilsDelegate.class))).thenReturn(true);

        Whitebox.setInternalState(servlet, "zipUtils", staticContentZipUtilsMock);
        Whitebox.setInternalState(servlet, "mainHtmlPageHandler", mainHtmlPageHandlerMock);
        Whitebox.setInternalState(servlet, "contentToPersistantHandler", contentToPersistantHandlerMock);
    }

    @Test
    public void doPost() throws IOException, RepositoryException {

        //create index.html inside
        context.load().json("/staticcontent-misses-main-page-binary.json", SOME_CHANNEL_JCR_CONTENT);
        Resource staticCompRes = resourceResolver.getResource(SOME_CHANNEL_JCR_CONTENT + PAR_STATICCONTENT);
        request.setResource(staticCompRes);

        HashedMap parameterMap = new HashedMap();
        parameterMap.put("file_path", ARCHIVE_REL_PATH);
        request.setParameterMap(parameterMap);

        try {
            servlet.doPost(request, response);
            assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        } catch (Exception e) {
            assertNull("StaticComponentServlet raised exception", e);
        }
    }

}