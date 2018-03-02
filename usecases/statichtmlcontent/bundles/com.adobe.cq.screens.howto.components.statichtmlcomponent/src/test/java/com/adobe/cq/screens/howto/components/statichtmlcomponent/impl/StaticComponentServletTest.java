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


import static org.mockito.Mockito.mock;


import com.adobe.cq.screens.howto.components.statichtmlcomponent.util.MainHtmlPageHandlerUtil;
import com.adobe.cq.screens.howto.components.statichtmlcomponent.util.StaticContentZipUtils;

import javax.servlet.http.HttpServletResponse;

public class StaticComponentServletTest {

    private static String STATIC_CONTENT_RESOURCE_PATH = "/content/screens/we-retail/channels/idle-night/_jcr_content/par/staticcontent";
    private static String ARCHIVE_REL_PATH = "file.sftmp";

    @Rule
    public final AemContext context = new AemContext(RESOURCERESOLVER_MOCK);

    @Mock
    private StaticContentZipUtils staticContentZipUtilsMock;
    @Mock
    private MainHtmlPageHandlerUtil mainHtmlPageHandlerUtilMock;

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

//        resourceUtilWrapperSpy = mock(StaticContentZipUtils.ResourceUtilWrapper.class);
        staticContentZipUtilsMock = mock(StaticContentZipUtils.class);
        mainHtmlPageHandlerUtilMock = mock(MainHtmlPageHandlerUtil.class);

        Whitebox.setInternalState(servlet, "zipUtils", staticContentZipUtilsMock);
        Whitebox.setInternalState(servlet, "mainHtmlPageHandlerUtil", mainHtmlPageHandlerUtilMock);

    }

    @Test
    public void doPost() {

        context.load().json("/staticcontent-contains-sftmp-file.json", STATIC_CONTENT_RESOURCE_PATH);
        context.load().json("/staticcontent-is-empty.json", STATIC_CONTENT_RESOURCE_PATH + "/" + ARCHIVE_REL_PATH);

        Resource staticCompRes = resourceResolver.getResource(STATIC_CONTENT_RESOURCE_PATH);

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