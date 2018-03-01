/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2017 Adobe Systems Incorporated
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
package com.adobe.cq.screens.howto.components.statichtmlcomponent;

import com.adobe.cq.screens.howto.components.statichtmlcomponent.util.JcrBinaryFromFile;
import com.adobe.cq.screens.howto.components.statichtmlcomponent.util.StaticContentZipUtils;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.mime.MimeTypeService;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Whitebox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.nodetype.NodeType;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StaticContentZipUtilsTest {

    private File resourcesDirectory;
    private File demoFile;
    private Node destinationNode;

    @Mock
    private ResourceResolver resourceResolverMock;
    @Mock
    private MimeTypeService mimeTypeService;
    @Spy
    private StaticContentZipUtils.ResourceUtilWrapper resourceUtilWrapperSpy;

    @InjectMocks
    StaticContentZipUtils staticContentZipUtils;

    @Before
    public void setUp() throws FileNotFoundException, RepositoryException, PersistenceException {

        resourceResolverMock = mock(ResourceResolver.class);
        Session sessionMock = getMockSession();
        when(resourceResolverMock.adaptTo(Session.class)).thenReturn(sessionMock);

        mimeTypeService = mock(MimeTypeService.class);
        when(mimeTypeService.getMimeType(anyString())).thenReturn("");
        staticContentZipUtils = StaticContentZipUtils.getOrCreateSharedInstance(mimeTypeService);

        resourcesDirectory = new File("src/test/resources");
        demoFile = new File(resourcesDirectory.getPath() + "/demo.zip");
        destinationNode = mock(Node.class);
        when(destinationNode.getPath()).thenReturn("/root");

        resourceUtilWrapperSpy = mock(StaticContentZipUtils.ResourceUtilWrapper.class);
        Whitebox.setInternalState(staticContentZipUtils, "resourceUtil", resourceUtilWrapperSpy);

    }

    private Session getMockSession() throws RepositoryException {
        Value contentValue = mock(Value.class);
        ValueFactory vf = mock(ValueFactory.class);
        when(vf.createValue(anyString())).thenReturn(contentValue);
        when(vf.createValue(any(Binary.class))).thenReturn(contentValue);

        Binary binaryMock = mock(Binary.class);
        when(vf.createBinary(any(InputStream.class))).thenReturn(binaryMock);


        Session sessionMock = mock(Session.class);
        when(sessionMock.getValueFactory()).thenReturn(vf);
        return sessionMock;
    }

    private Node getNodeWithFile(File file) throws RepositoryException, FileNotFoundException {

        Binary content = new JcrBinaryFromFile(file);
        Node contentNode = mock(Node.class);
        Property dataProp = mock(Property.class);
        when(dataProp.getBinary()).thenReturn(content);
        when(contentNode.getProperty(javax.jcr.Property.JCR_DATA)).thenReturn(dataProp);
        Node archiveNode = mock(Node.class);
        when(archiveNode.getNode(Node.JCR_CONTENT)).thenReturn(contentNode);

        return archiveNode;
    }

    @After
    public void tearDown() throws Exception {

    }

//    @Test
    public void testExtractWithDemoZip() throws FileNotFoundException, RepositoryException, PersistenceException {

        Node contentNode = mock(Node.class);
        Node fileNode = mock(Node.class);
        when(fileNode.addNode(Node.JCR_CONTENT, NodeType.NT_RESOURCE)).thenReturn(contentNode);

        Node parentNode = spy(Node.class);
        when(parentNode.addNode(anyString(), anyString())).thenReturn(fileNode);

        Resource parentResource = mock(Resource.class);
        when(parentResource.adaptTo(Node.class)).thenReturn(parentNode);

        when(resourceUtilWrapperSpy.getOrCreateResource(any(ResourceResolver.class), anyString(), anyString(), anyString(), anyBoolean()))
                                    .thenReturn(parentResource);

        Node archiveNode = getNodeWithFile(demoFile);


        try {
//            staticContentZipUtils.extract(archiveNode, destinationNode, resourceResolverMock);

            //verify destination of the extracted files
            verify(resourceUtilWrapperSpy, times(2)).getOrCreateResource(resourceResolverMock, "/root", JcrResourceConstants.NT_SLING_FOLDER,
                                                                         JcrResourceConstants.NT_SLING_FOLDER, true);

            //verify files are added in jcr
            verify(parentNode, times(1)).addNode("Explosion.js", NodeType.NT_FILE);
            verify(parentNode, times(1)).addNode("index.html", NodeType.NT_FILE);


        } catch (IOException e) {
            Assert.fail("Exception at extract");
        }

    }
}
