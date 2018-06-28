DAM driven Screens experience
========================

Use Case
--------
As a customer you can be overwhelmed by the power of DCC, especially if you're already an expert in DAM features. 
This module contains a sample on how to edit and maintain the content of a Channel via DAM instead of DCC. 
The purpose is to strictly disallow editing via DCC and instead leverage the full potential of DAM, including:
* tagging

More info: 
* research story: [[research] Create a DAM driven Tagging-Channel in HowTo](https://jira.corp.adobe.com/browse/CQ-4237003)
* implementation story: [[screens-howto] Create DAM driven tagging channel](https://jira.corp.adobe.com/browse/CQ-4237003).

How to Use the Sample Content
-----------------------------
* Add more tags to /etc/tags/screens-howto/damdriven-content (can do the same from [AEM Tags](http://localhost:4502/libs/cq/tagging/gui/content/tags.html/etc/tags))
* Add more tagged assets to /content/dam/screens-howto/damdriven or change tags on the existing assets (can do the same from [AEM Assets](http://localhost:4502/assets.html/content/dam))
* Add more tagged channels to /content/screens/channels/dam-driven-content (can do the same from [AEM screens-howto -> channels -> dam-driven-content](http://localhost:4502/screens.html/content/screens/screens-howto/channels/dam-driven-content))

Technical Details
-----------------
### Features built upon
* Workflow launcher
* Workflow model
* Tags
* Assets
* Channel component and template

### Implementation
* Custom workflow launcher to trigger on asset metadata modified: /etc/workflow/launcher/config/screens-howto/damdriven/sync-tagged-assets
* Custom workflow model to start the sync process: /etc/workflow/models/screens-howto/damdriven-sync-tagged-assets
* Tag Channel component based on Sequence Channel: /apps/screens-howto/components/screens/tagchannel
* Tag Channel template based on Sequence Channel: /apps/screens-howto/templates/tagchannel
* Example tags: /etc/tags/screens-howto/dam-driven-content
* Example assets: /content/dam/screens-howto/damdriven
* Example channels and location: /content/screens/screens-howto
* Custom workflow process to sync assets to channels: SyncTaggedDamAssetsWorkflowProcess

Installation
------------
This module requires HowTo project and is part of the install process. Follow [instructions here](../../README.md).

If you still want to install the module individually, you can run:

```
mvn clean install content-package:install
```

Hacks
------------
* Overlay of `libs/screens/dcc/components/dashboard/channel.js` and `libs/screens/dcc/components/dashboard/channelinfo/channelinfo.html` in order to show "Channel Type: Tagged Channel" in Channel Dashboard.
* Overlay of `libs/screens/dcc/components/actionRelationships.js` in order to disable the Edit button from DCC for tagged channels.

Limitations
------------
* Once a tagged channel is created with certain tags, they cannot be modified anymore.
Syncing assets from DAM when tags on channels are updated after channel creation was not part of the implementation and is not supported.
Due to this limitation, the tag field on channels is disabled in edit mode for AEM 6.4 (this would work for AEM 6.3.2 after [Backport GRANITE-17626 - Disabled property in new tagfield component doesn't work](https://jira.corp.adobe.com/browse/NPR-23581]) is completed).

* Problem: Workflow is triggered for all assets in DAM for every tag change.
    * Possible workaround: Change the "globbing path" for the workflow launcher to point to a specific asset-folder.

* Problem: Launchers and workflows are async. That means we cannot show a loading indicator in DAM. 
The user will always assume that the task of syncing and maybe even creating the offline config automatically will be done.
    * Possible workaround for offline config: Add a check in the Publish Button before publishing to check if workflow is finished (Author/Publish setup only).
