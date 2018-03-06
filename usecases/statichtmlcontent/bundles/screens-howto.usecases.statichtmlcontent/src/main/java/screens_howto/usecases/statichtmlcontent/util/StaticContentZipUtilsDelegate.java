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

import org.apache.sling.api.resource.PersistenceException;

import java.io.InputStream;

import javax.jcr.RepositoryException;

/**
 * StaticContentZipUtilsDelegate
 * Interface that is implemented by specialised objects that handle resulting
 * data from the StaticContentZipUtils.extract method
 *
 * A chain of delegates can be obtained if a next delegate is set
 *
 * Set by "StaticComponentServlet" for StaticContentZipUtils extract method
 *
 */
public interface StaticContentZipUtilsDelegate {

    /**
     * A next delegate can be set, in this way a stack of delegates
     *  (middleware behaviour) can be obtained
     * @param delegate
     */
    void setNext(StaticContentZipUtilsDelegate delegate);

    /**
     * Called by StaticContentZipUtils when a folder is found
     * @param dirRelPath the relative path of the the folder
     * @throws PersistenceException
     */
    void handleDirectory(String dirRelPath) throws PersistenceException;

    /**
     * Called by StaticContentZipUtils when a file is found
     * @param fileRelPath the relative path of the file (contains also the name of the file)
     * @param mimeType the mimeType of the file
     * @param content InputStream with the content
     * @throws RepositoryException
     * @throws PersistenceException
     */
    void handleFile(String fileRelPath, String mimeType, InputStream content) throws RepositoryException, PersistenceException;
}
