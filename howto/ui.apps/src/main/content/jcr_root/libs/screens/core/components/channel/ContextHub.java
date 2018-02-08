/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2015 Adobe Systems Incorporated
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
 **************************************************************************/
package libs.screens.core.components.channel;

import com.adobe.cq.sightly.WCMUsePojo;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;


// TODO move to product code base!
public class ContextHub extends WCMUsePojo {

    private boolean defined = false;

    static private String PN_CONTEXTHUB_PATH = "cq:contextHubPath";

    @Override
    public void activate() throws Exception {
        ValueMap properties = getPageProperties();
        String contextHubPath = properties.get(PN_CONTEXTHUB_PATH, String.class);
        Resource contextHub = contextHubPath != null ? getResourceResolver().resolve(contextHubPath) : null;
        this.defined = contextHub != null;
    }

    public boolean isDefined() {
        return this.defined;
    }
}
