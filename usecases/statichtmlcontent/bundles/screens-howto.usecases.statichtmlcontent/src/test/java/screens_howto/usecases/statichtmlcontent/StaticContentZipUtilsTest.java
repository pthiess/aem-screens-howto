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
package screens_howto.usecases.statichtmlcontent;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.mime.MimeTypeService;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.jcr.RepositoryException;

import static org.apache.sling.testing.mock.sling.ResourceResolverType.RESOURCERESOLVER_MOCK;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.wcm.testing.mock.aem.junit.AemContext;
import screens_howto.usecases.statichtmlcontent.util.StaticContentZipUtils;
import screens_howto.usecases.statichtmlcontent.util.StaticContentZipUtilsDelegate;

public class StaticContentZipUtilsTest {

    private static final String SOME_CHANNEL_JCR_CONTENT = "/content/screens/someproject/somechannel/jcr:content";
    private static final String PAR_STATICCONTENT = "/par/staticcontent/file";

    private File resourcesDirectory;
    private File demoFile;

    @Mock
    private MimeTypeService mimeTypeService;

    @Spy
    private StaticContentZipUtilsDelegate delegateSpy;

    private ResourceResolver resourceResolver;

    @Rule
    public final AemContext context = new AemContext(RESOURCERESOLVER_MOCK);

    @InjectMocks
    StaticContentZipUtils staticContentZipUtils;

    @Before
    public void setUp() throws FileNotFoundException, RepositoryException, PersistenceException {

        resourceResolver = context.resourceResolver();

        mimeTypeService = mock(MimeTypeService.class);
        when(mimeTypeService.getMimeType(anyString())).thenReturn("");
        staticContentZipUtils = StaticContentZipUtils.getOrCreateSharedInstance(mimeTypeService);

        resourcesDirectory = new File("src/test/resources");
        demoFile = new File(resourcesDirectory.getPath() + "/demo.zip");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testExtractDemo() throws FileNotFoundException {
        context.load().json("/staticcontent-misses-content-node.json", SOME_CHANNEL_JCR_CONTENT);
        Resource statiContentRes = resourceResolver.getResource(SOME_CHANNEL_JCR_CONTENT + PAR_STATICCONTENT);
        FileInputStream fis = new FileInputStream(demoFile);
        Resource archiveRes = context.load().binaryFile(fis, statiContentRes, "file.swtf", "application/zip");

        delegateSpy = mock(StaticContentZipUtilsDelegate.class);

        try {
            staticContentZipUtils.extract(archiveRes, delegateSpy);
            verify(delegateSpy, times(2)).handleFile(anyString(), anyString(), any(InputStream.class));

        } catch (RepositoryException | IOException e ) {
            assertNull("StaticContentZipUtils raised exception", e);
        }
    }

}
