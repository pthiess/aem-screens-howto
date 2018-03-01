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
package apps.screens_howto.components.screens.content.staticcontent;

import com.adobe.cq.sightly.WCMUsePojo;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticContent extends WCMUsePojo {

    /**
     * Default logger.
     */
    private static final Logger log = LoggerFactory.getLogger(StaticContent.class);

    @Override
    public void activate() throws Exception {
    }

    /**
     * Returns a path that is resolved by StaticComponentServlet GET servlet.
     * @return: e.g: /content/screens/<project>/channels/<channel>/_jcr_content/par/<staticcontent>.index.html
     */
    public String pathToMainPage() {
        Resource resource = getResource();
        return getResourceResolver().map(resource.getPath()) + ".index.html";
    }
}
