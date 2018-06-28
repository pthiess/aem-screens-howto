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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;

import static com.day.cq.wcm.api.NameConstants.PN_PAGE_LAST_MOD;
import static com.day.cq.wcm.api.NameConstants.PN_PAGE_LAST_MOD_BY;
import static com.day.cq.wcm.api.NameConstants.PN_TAGS;
import static org.apache.sling.jcr.resource.api.JcrResourceConstants.SLING_RESOURCE_SUPER_TYPE_PROPERTY;
import static org.apache.sling.jcr.resource.api.JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY;

public final class SyncTaggedDamAssetsUtil {

    private static final Logger log = LoggerFactory.getLogger(SyncTaggedDamAssetsUtil.class);

    private static final String XPATH = "xpath";
    private static final String XPATH_OR = " or ";
    private static final String IMAGE = "image";
    private static final String VIDEO = "video";

    private static final String PN_SOURCE = "source";
    private static final String PN_DURATION = "duration";
    private static final String PN_RENDERING = "rendering";

    private static final String SCREENS_TAGCHANNEL_COMPONENT_RT = "screens-howto/components/screens/tagchannel";
    private static final String SCREENS_IMAGE_COMPONENT_RT = "screens/core/components/content/image";
    private static final String SCREENS_VIDEO_COMPONENT_RT = "screens/core/components/content/video";

    private static final SecureRandom random = new SecureRandom();

    private static final Map<String, String> componentByAssetType = new HashMap<>();

    static {
        componentByAssetType.put(IMAGE, SCREENS_IMAGE_COMPONENT_RT);
        componentByAssetType.put(VIDEO, SCREENS_VIDEO_COMPONENT_RT);
    }

    private interface Transformer<T, S> {
        S transform(T input);
    }

    private SyncTaggedDamAssetsUtil() {
    }

    public static void syncBasedOnDamMetadata(ResourceResolver resolver, Resource metadataResource) {
        Asset asset = DamUtil.getAssetFromMetaRes(metadataResource);
        Session session = resolver.adaptTo(Session.class);
        TagManager tagManager = resolver.adaptTo(TagManager.class);

        if (tagManager == null) {
            log.warn("No tag manager to work with");
            return;
        }

        Tag[] tags = tagManager.getTags(metadataResource);
        log.debug("Found {} tags for asset {}", tags.length, asset.getPath());

        // Step 1.1. Query channels which contain given asset
        Set<String> channelPathsForAsset = new HashSet<>();
        try {
            NodeIterator channelsForAsset = getChannels(session, asset);
            channelPathsForAsset = mapToUniquePaths(channelsForAsset, new Transformer<String, String>() {
                @Override
                public String transform(String input) {
                    int parsysIndex = input.indexOf("/par");
                    return parsysIndex != -1 ? input.substring(0, parsysIndex) : input;
                }
            });
            log.debug("Found {} channels for asset {}", channelPathsForAsset.size(), asset.getPath());
        } catch (RepositoryException e) {
            log.error("Error querying channels for asset {}", asset.getPath(), e);
        }

        // Step 1.2. Query channels which contain asset tags
        Set<String> taggedChannelPaths = new HashSet<>();
        try {
            if (tags.length > 0) {
                NodeIterator taggedChannels = getChannels(session, tags);
                taggedChannelPaths = mapToUniquePaths(taggedChannels, new Transformer<String, String>() {
                    @Override
                    public String transform(String input) {
                        return input;
                    }
                });
            }
            log.debug("Found {} channels for tags", taggedChannelPaths.size());
        } catch (RepositoryException e) {
            log.error("Error querying channels for tags", e);
        }

        // Step 1.3. Compute asymmetric set difference
        Set<String> channelPathsToAddAsset = new HashSet<>(taggedChannelPaths);
        channelPathsToAddAsset.removeAll(channelPathsForAsset);
        channelPathsForAsset.removeAll(taggedChannelPaths);

        // Step 2. Remove asset from channels
        for (String channelPath : channelPathsForAsset) {
            removeAsset(resolver, channelPath, asset);
        }

        // Step 3. Add asset to channels
        for (String channelPath : channelPathsToAddAsset) {
            addAsset(resolver, channelPath, asset);
        }
    }

    private static NodeIterator getChannels(Session session, Asset asset) throws RepositoryException {
        String xpathQuery = buildXpathQuery(asset);
        log.debug("Xpath query for channels containing asset {} under content screens: {}",
                asset.getPath(), xpathQuery);

        QueryResult result = xpath(session, xpathQuery);
        return result.getNodes();
    }

    private static String buildXpathQuery(Asset asset) {
        String assetType = getAssetType(asset);
        String propertyNameForPath = getPropertyNameForPath(assetType);

        return "/jcr:root/content/screens//*[@" + propertyNameForPath + "='" + asset.getPath() +
                "'] [@" + SLING_RESOURCE_TYPE_PROPERTY + "='" + getResourceType(assetType) + "']";
    }

    private static String getAssetType(Asset asset) {
        return DamUtil.isImage(asset) ? IMAGE : DamUtil.isVideo(asset) ? VIDEO : "";
    }

    private static String getPropertyNameForPath(String assetType) {
        return IMAGE.equals(assetType) ? "fileReference" : VIDEO.equals(assetType) ? "asset" : "";
    }

    private static String getResourceType(String assetType) {
        final String resourceType = componentByAssetType.get(assetType);
        return resourceType != null ? resourceType : "";
    }

    private static QueryResult xpath(Session session, String xpathQuery) throws RepositoryException {
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        Query query = queryManager.createQuery(xpathQuery, XPATH);
        return query.execute();
    }

    private static Set<String> mapToUniquePaths(NodeIterator nodeIterator, Transformer<String, String> transformer) throws RepositoryException {
        Set<String> list = new HashSet<>();

        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.nextNode();
            list.add(transformer.transform(node.getPath()));
        }

        return list;
    }
    private static NodeIterator getChannels(Session session, Tag[] tags) throws RepositoryException {
        String tagsQuery = "/jcr:root/content/screens//*[" + buildXpathQuery(tags) + "] " +
                "[@" + SLING_RESOURCE_TYPE_PROPERTY + "='" + SCREENS_TAGCHANNEL_COMPONENT_RT +
                "' or @" + SLING_RESOURCE_SUPER_TYPE_PROPERTY + "='" + SCREENS_TAGCHANNEL_COMPONENT_RT + "']";
        log.debug("Xpath query for channels containing tags: {}", tagsQuery);

        QueryResult result = xpath(session, tagsQuery);
        return result.getNodes();
    }

    private static String buildXpathQuery(Tag[] tags) {
        Iterator<Tag> collectedTags = Arrays.asList(tags).iterator();

        StringBuffer tagsQuery = new StringBuffer();

        while (collectedTags.hasNext()) {
            Tag tag = collectedTags.next();
            tagsQuery.append("@").append(PN_TAGS).append("='").append(tag.getTagID()).append("'");
            if (collectedTags.hasNext()) {
                tagsQuery.append(XPATH_OR);
            }
        }

        return tagsQuery.toString();
    }

    /**
     * TODO: Would be nice to have this kind of API in screens-core, but it's not implemented yet
     *
     * ```
     * Channel channel = ChannelService.getChannel(channelNode);
     * if ChannelService.isPlaylist(channel) {
     * channel.addPlaylistItem(asset);
     * channel.addPlaylistItem(transition);
     * }
     * ```
     */
    private static void addAsset(ResourceResolver resolver, String channelPath, Asset asset) {
        log.debug("Add asset {} to channel {}", asset.getPath(), channelPath);

        Node channel = resolveNodeFromPath(resolver, channelPath);
        Node parsys = resolveNodeFromPath(resolver, channelPath + "/par");
        if (channel == null || parsys == null) {
            log.warn("Channel with path {} / parsys to add asset {} doesn't exist", channelPath, asset.getPath());
            return;
        }

        try {
            addAssetToParent(parsys, asset);
            resolver.commit();
            resolver.refresh();

            updateLastModified(channel);
            resolver.commit();
            resolver.refresh();
        } catch (PersistenceException | RepositoryException e) {
            log.error("Error adding asset {} to channel {}", asset.getPath(), channelPath, e);
        }
    }

    private static Node resolveNodeFromPath(ResourceResolver resolver, String nodePath) {
        Resource resource = resolver.getResource(nodePath);
        if (resource == null) {
            log.warn("Resource with path {} doesn't exist", nodePath);
            return null;
        }

        return resource.adaptTo(Node.class);
    }

    private static void addAssetToParent(Node parent, final Asset asset) throws RepositoryException {
        String assetType = getAssetType(asset);
        String propertyNameForPath = getPropertyNameForPath(assetType);

        final String imageNodeName = assetType + random.nextInt(50000);
        Node imageNode = parent.addNode(imageNodeName);
        imageNode.setProperty(SLING_RESOURCE_TYPE_PROPERTY, getResourceType(assetType));
        imageNode.setProperty(propertyNameForPath, asset.getPath());
        imageNode.setProperty(PN_SOURCE, "tagchannel");
        if (VIDEO.equals(assetType)) {
            imageNode.setProperty(PN_DURATION, "-1");
            imageNode.setProperty(PN_RENDERING, "contain");
        }
    }

    private static void updateLastModified(Node channel) throws RepositoryException {
        channel.setProperty(PN_PAGE_LAST_MOD_BY, "workflow");
        channel.setProperty(PN_PAGE_LAST_MOD, Calendar.getInstance());
    }

    private static void removeAsset(ResourceResolver resolver, String channelPath, Asset asset) {
        log.debug("Remove asset {} from channel {}", asset.getPath(), channelPath);

        Node channel = resolveNodeFromPath(resolver, channelPath);
        Resource parsysResource = resolver.getResource(channelPath + "/par");
        if (channel == null || parsysResource == null) {
            log.warn("Channel with path {} / parsys to remove asset {} doesn't exist", channelPath, asset.getPath());
            return;
        }

        try {
            List<Node> assetNodes = findNodes(parsysResource, asset);

            if (!assetNodes.isEmpty()) {
                for (Node assetNode : assetNodes) {
                    assetNode.remove();
                    resolver.commit();
                    resolver.refresh();
                }

                updateLastModified(channel);
                resolver.commit();
                resolver.refresh();
            }
        } catch (PersistenceException | RepositoryException e) {
            log.error("Error removing asset {} from channel {}", asset.getPath(), channelPath, e);
        }
    }

    private static List<Node> findNodes(Resource parent, Asset asset) {
        List<Node> nodes = new ArrayList<>();

        final String assetType = getAssetType(asset);
        final String propertyNameForPath = getPropertyNameForPath(assetType);
        final String resourceType = getResourceType(assetType);

        for (Resource child : parent.getChildren()) {
            ValueMap valueMap = child.getValueMap();

            if (resourceType.equals(valueMap.get(SLING_RESOURCE_TYPE_PROPERTY, String.class))
                    && asset.getPath().equals(valueMap.get(propertyNameForPath, String.class))) {
                nodes.add(child.adaptTo(Node.class));
            }
        }

        return nodes;
    }
}
