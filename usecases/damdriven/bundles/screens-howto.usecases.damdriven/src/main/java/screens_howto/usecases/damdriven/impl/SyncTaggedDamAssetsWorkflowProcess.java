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
package screens_howto.usecases.damdriven.impl;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.dam.commons.util.DamUtil;

import static com.adobe.granite.workflow.PayloadMap.TYPE_JCR_PATH;

/**
 * This is a Custom Workflow process to create Channel components with assets to multiple channels based on tags attached to assets.
 */
@Component(
        service = {WorkflowProcess.class},
        property = {
                "process.label=Screens DAM Driven Workflow Process",
                Constants.SERVICE_DESCRIPTION + "=AEM Screens Howto DAM Driven Workflow Process implementation."
        }
)
public class SyncTaggedDamAssetsWorkflowProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(SyncTaggedDamAssetsWorkflowProcess.class);

    /**
     * The method called by the AEM Workflow Engine to perform Workflow work.
     *
     * @param workItem        the work item representing the resource moving through the Workflow
     * @param workflowSession the workflow session
     * @param args            arguments for this Workflow Process defined on the Workflow Model (PROCESS_ARGS, argSingle, argMulti)
     * @throws WorkflowException when the Workflow Process step cannot complete. This will cause the WF to retry.
     */
    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args) throws WorkflowException {
        final WorkflowData workflowData = workItem.getWorkflowData();
        final ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);

        if (resolver == null || !workflowData.getPayloadType().equals(TYPE_JCR_PATH)) {
            return;
        }

        final String path = workflowData.getPayload().toString();
        final Resource resource = resolver.getResource(path);

        if (resource == null) {
            return;
        }

        // expect the resource to be asset's metadata
        if (DamUtil.isMetadataRes(resource)) {
            log.debug("Started workflow to sync tagged DAM assets for path {}", path);
            SyncTaggedDamAssetsUtil.syncBasedOnDamMetadata(resolver, resource);
            log.debug("Finished workflow to sync tagged DAM assets for path {}", path);
        }
    }
}
