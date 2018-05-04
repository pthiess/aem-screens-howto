
/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2015 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */
/* globals org, com, javax, resource, request, use */
use(['helper.js'], function(helper) {
    'use strict';

    /**
     * Check if the user has the permission to perform the the desired action.
     *
     * @param {AccessControlManager} acm        The access control manager to use
     * @param {Resource}             resource   The resource the action is to be performed on
     * @param {String}               privilege  The action to perform
     *
     * @return {Boolean} Returns `true` if the action is permitted, `false` otherwise
     */
    var RT_TAG_CHANNEL = 'screens-howto/components/screens/tagchannel';
    var hasPermission = function(acm, resource, privilege) {
        if (acm !== null) {
            var p = acm.privilegeFromName(privilege);
            return acm.hasPrivileges(resource.getPath(), [p]);
        }
        return false;
    };

    /**
     * Get the list of permitted actions on the specified resource.
     *
     * @param {Resource} resource The resource the actions are to be checked on
     *
     * @return {String[]} Returns an array with the permitted actions
     */
    var getActionRels = function(resource) {
        var unwrappedResource, unwrappedResourceProps,
            isLegacyChannelAssignmentWithoutRT, isChannelAssignmentAssignedToSchedule;
        var actionRels = {};
        var resourceResolver = resource.resourceResolver;
        var acm = resourceResolver.adaptTo(javax.jcr.Session).getAccessControlManager();

        actionRels[helper.constants.screens.ACT_COPY] = true;

        var page = resource.adaptTo(com.day.cq.wcm.api.Page);
        if (!page) {
            page = resource.parent.adaptTo(com.day.cq.wcm.api.Page);
        }

        // Stop here if we cannot even read the resource
        if (!hasPermission(acm, resource, javax.jcr.security.Privilege.JCR_READ)) {
            return actionRels;
        }

        if (page) {
            var device;

            // Displays
            if (helper.isResourceType(page.getContentResource(), [helper.constants.screens.RT_DISPLAY])) {
                actionRels[helper.constants.screens.ACT_VIEW_FIRMWARE] = true;
                actionRels[helper.constants.screens.ACT_VIEW_DASHBOARD_DISPLAY] = true;
                if (hasPermission(acm, page.getContentResource().getChild(helper.constants.screens.NN_CHANNELS), javax.jcr.security.Privilege.JCR_ADD_CHILD_NODES)) {
                    actionRels[helper.constants.screens.ACT_LINK_CHANNEL] = true;
                }
            }

            // Device Configs
            if (helper.isResourceType(page.getContentResource(), [helper.constants.screens.RT_DEVICE_CONFIG])) {
                actionRels[helper.constants.screens.ACT_VIEW_FIRMWARE] = true;
                actionRels[helper.constants.screens.ACT_VIEW_DASHBOARD_DEVICE] = true;

                var deviceManager = resource.getResourceResolver().adaptTo(com.adobe.cq.screens.device.DeviceManager);
                var deviceConfig = resource.adaptTo(com.adobe.cq.screens.device.DeviceConfig);

                var deviceId = deviceConfig.getAssignedDeviceId();
                device = deviceId ? deviceManager.getDevice(deviceId) : null;
                var isAssigned = !!(deviceId && device);
                if (isAssigned) {
                    actionRels['screens-dcc-actions-Device-unassign-activator'] = true;
                    actionRels['screens-dcc-actions-Device-pushconfig-activator'] = true;
                    actionRels['screens-dcc-actions-Device-configupdate-activator'] = true;
                }
                else {
                    actionRels['screens-dcc-actions-Device-assign-activator'] = true;
                    actionRels[helper.constants.screens.ACT_DELETE] = true;
                }
            }

            // Device Folder
            if (helper.isResourceType(page.getContentResource(), [helper.constants.screens.RT_DEVICE_FOLDER])) {
                actionRels[helper.constants.screens.ACT_VIEW_DEVICE_MANAGER] = true;
            }

            // Channels
            if (helper.isResourceType(page.getContentResource(), [helper.constants.screens.RT_CHANNEL])) {
                if (hasPermission(acm, resource, javax.jcr.security.Privilege.JCR_ADD_CHILD_NODES) &&
                    !helper.isResourceType(page.getContentResource(), [RT_TAG_CHANNEL])) {
                    actionRels[helper.constants.screens.ACT_EDIT] = true;
                }
                actionRels[helper.constants.screens.ACT_VIEW_CONTENT] = true;
                actionRels[helper.constants.screens.ACT_VIEW_DASHBOARD_CHANNEL] = true;
            }

            // Locations
            if (helper.isResourceType(page.getContentResource(), [helper.constants.screens.RT_LOCATION])) {
                if (hasPermission(acm, resource, javax.jcr.security.Privilege.JCR_ADD_CHILD_NODES)) {
                    actionRels[helper.constants.screens.ACT_CREATE] = true;
                }
            }

            // Schedules
            if (helper.isResourceType(page.getContentResource(), [helper.constants.screens.RT_SCHEDULE])) {
                actionRels[helper.constants.screens.ACT_VIEW_DASHBOARD_SCHEDULE] = true;
            }

            // Folders
            if (helper.isResourceType(page.getContentResource(), [helper.constants.screens.RT_APPLICATION_FOLDER,
                    helper.constants.screens.RT_LOCATION_FOLDER, helper.constants.screens.RT_CHANNEL_FOLDER,
                    helper.constants.screens.RT_SCHEDULE_FOLDER, helper.constants.screens.RT_PROJECT])) {
                if (hasPermission(acm, resource, javax.jcr.security.Privilege.JCR_ADD_CHILD_NODES)) {
                    actionRels[helper.constants.screens.ACT_CREATE] = true;
                }
            }
 
            // Applications
            if (helper.isResourceType(page.getContentResource(), [helper.constants.screens.RT_APPLICATION])
                && page.depth > 4) {
                if (page.getProperties().get('isEditable')
                    && hasPermission(acm, resource, javax.jcr.security.Privilege.JCR_ADD_CHILD_NODES)) {
                    actionRels[helper.constants.screens.ACT_EDIT] = true;
                }
                actionRels[helper.constants.screens.ACT_VIEW_CONTENT] = true;
            }

            // More specifc rules
            if (!helper.isResourceType(page.getContentResource(), [helper.constants.screens.RT_APPLICATION])) {
                if (hasPermission(acm, resource, javax.jcr.security.Privilege.JCR_MODIFY_PROPERTIES)) {
                    actionRels[helper.constants.screens.ACT_VIEW_PROPERTIES] = true;
                }
            }
            if (!helper.isResourceType(page.getContentResource(), [helper.constants.screens.RT_APPLICATION,
                    helper.constants.screens.RT_DEVICE_CONFIG])) {
                if (hasPermission(acm, resource, javax.jcr.security.Privilege.JCR_REMOVE_NODE)) {
                    actionRels[helper.constants.screens.ACT_MOVE] = true;
                    actionRels[helper.constants.screens.ACT_DELETE] = true;
                }
            }
        }
        else {

            device = resource.adaptTo(com.adobe.cq.screens.device.Device);
            unwrappedResource = resourceResolver.getResource(resource.path);
            unwrappedResourceProps = unwrappedResource.adaptTo(org.apache.sling.api.resource.ValueMap);
            isChannelAssignmentAssignedToSchedule = request.requestPathInfo.selectorString == 'channelAssignmentAssignedToSchedule'; // eslint-disable-line
            isLegacyChannelAssignmentWithoutRT = unwrappedResourceProps.get('role') != null && unwrappedResourceProps.get('path') != null;  // eslint-disable-line

            if (device) {
                // Device specific actions
                if (!device.getDeviceConfig()) {
                    if (hasPermission(acm, resource, javax.jcr.security.Privilege.JCR_MODIFY_PROPERTIES)) {
                        actionRels[helper.constants.screens.ACT_ASSIGN_DEVICE] = true;
                    }
                }
                actionRels[helper.constants.screens.ACT_VIEW_DASHBOARD_DEVICE] = true;
                if (hasPermission(acm, resource, javax.jcr.security.Privilege.JCR_REMOVE_NODE)) {
                    actionRels['screens-dcc-actions-Device-delete-activator'] = true;
                }
            }
            else if (helper.isResourceType(unwrappedResource, [helper.constants.screens.RT_SCHEDULE_ASSIGNMENT_ABSOLUTE])) {
                actionRels['screens-dcc-actions-scheduleDashboard-activator'] = true;
                if (hasPermission(acm, resource, javax.jcr.security.Privilege.JCR_REMOVE_NODE)) {
                    actionRels[helper.constants.screens.ACT_DELETE] = true;
                }
            }
            else if (helper.isResourceType(unwrappedResource, [
                    helper.constants.screens.RT_CHANNEL_ASSIGNMENT_ABSOLUTE,
                    helper.constants.screens.RT_CHANNEL_ASSIGNMENT_DYNAMIC
                ]) || isLegacyChannelAssignmentWithoutRT) {
                    actionRels['screens-dcc-actions-channelDashboard-activator'] = true;

                    // Try to resolve the assignment to an actual channel
                    var assignmentSvc = resourceResolver.adaptTo(com.adobe.cq.screens.assignment.AssignmentService);
                    var contextResource = resourceResolver.resolve(request.requestPathInfo.suffix);
                    var channelResource = assignmentSvc ? assignmentSvc.resolveEntity(resource, contextResource) : null;
                    var channelContentResource = channelResource ? channelResource.getChild(helper.constants.cq.NN_CONTENT) : null;

                    // A ChannelAssignment that is assigned to a Schedule shall only be editable
                    if (channelResource && !helper.isResourceType(channelContentResource, [RT_TAG_CHANNEL]) && hasPermission(acm, channelResource, javax.jcr.security.Privilege.JCR_ADD_CHILD_NODES)) {
                        actionRels[helper.constants.screens.ACT_EDIT] = true;
                    }
                    if (!isChannelAssignmentAssignedToSchedule) {
                        if (hasPermission(acm, resource.getParent(), javax.jcr.security.Privilege.JCR_REMOVE_CHILD_NODES)) {
                            actionRels[helper.constants.screens.ACT_DELETE] = true;
                        }
                        if (hasPermission(acm, resource, javax.jcr.security.Privilege.JCR_MODIFY_PROPERTIES)) {
                            actionRels['screens-dcc-actions-ChannelLink-edit-activator'] = true;
                        }
                    }

            }
            else {
                // Folders
                if (hasPermission(acm, resource, javax.jcr.security.Privilege.JCR_MODIFY_PROPERTIES)) {
                    actionRels[helper.constants.screens.ACT_VIEW_FOLDERPROPERTIES] = true;
                }
                if (hasPermission(acm, resource, javax.jcr.security.Privilege.JCR_REMOVE_NODE)) {
                    actionRels[helper.constants.screens.ACT_DELETE] = true;
                }
            }
        }

        return actionRels;
    };

    return function(res) {
        return {
            getActionRels: getActionRels,
            array: function() {
                return getActionRels(res || resource) || [];
            },
            list: function() {
                return Object.keys(getActionRels(res || resource)).join(' ') || '';
            }
        };
    };

});
