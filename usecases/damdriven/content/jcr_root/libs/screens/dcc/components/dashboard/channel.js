/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2015 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:    All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.    The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */
/* globals org, request, resource, use */
use(['../../helper.js'], function(helper) {
    'use strict';

    var resourceResolver = resource.resourceResolver;
    var RT_TAG_CHANNEL = 'screens-howto/components/screens/tagchannel';

    // Retrieve the channel resource and its properties from the request suffix
    var channelPath = request.requestPathInfo.suffix;
    if (channelPath == null) { // eslint-disable-line
        return null;
    }

    var channelResource = resourceResolver.resolve(channelPath);
    var channelContentResource = channelResource.getChild(helper.constants.cq.NN_CONTENT);
    if (channelContentResource == null) { // eslint-disable-line
        return null;
    }

    var type;
    if (helper.isResourceType(channelContentResource, helper.constants.screens.RT_APPLICATION_CHANNEL)) {
        type = 'App Channel';
    }
    else if (helper.isResourceType(channelContentResource, RT_TAG_CHANNEL)) {
        type = 'Tagged Channel';
    }
    else if (helper.isResourceType(channelContentResource, helper.constants.screens.RT_SEQUENCE_CHANNEL)) {
        type = 'Sequence Channel';
    }
    else {
        type = '';
    }

    var channelContentProperties = channelContentResource.adaptTo(org.apache.sling.api.resource.ValueMap);
    if (channelContentProperties == null) { // eslint-disable-line
        return null;
    }

    return {
        windowPreference: helper.windowPreference,
        properties: channelContentProperties,
        type: type
    };

});
