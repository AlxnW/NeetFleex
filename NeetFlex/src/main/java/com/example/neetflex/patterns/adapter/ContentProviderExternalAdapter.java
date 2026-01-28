package com.example.neetflex.patterns.adapter;

import com.example.neetflex.model.contents.Content;
import org.springframework.stereotype.Component;

/**
 * Adapter that converts the ContentProviderExternal interface to the IContentProvider interface.
 * This allows the external content provider to be used where an IContentProvider is expected.
 */
@Component
public class ContentProviderExternalAdapter implements IContentProvider {

    private final ContentProviderExternal contentProviderExternal;

    public ContentProviderExternalAdapter() {
        this.contentProviderExternal = new ContentProviderExternal();
        System.out.println("[Adapter] ContentProviderExternalAdapter initialized with " + 
                           contentProviderExternal.listAvailableContentIds().length + " external content items.");
    }

    /**
     * Adapts the getContentByName method of ContentProviderExternal to the getContent method of IContentProvider.
     * @param id The content ID
     * @return A string with content information
     */
    @Override
    public String getContent(String id) {
        // For the adapter, we need to determine appropriate values for the additional parameters
        // In a real implementation, these might come from configuration or be derived from the ID
        String defaultName = "External Content " + id;
        String defaultUrl = "https://external-content.com/content/" + id;

        return contentProviderExternal.getContentByName(id, defaultName, defaultUrl);
    }

    /**
     * Adapts the fetchContentFromUrl method of ContentProviderExternal to the findContentbyId method of IContentProvider.
     * @param id The content ID
     * @return A Content object
     */
    @Override
    public Content findContentbyId(String id) {
        // Similar to getContent, we need to provide values for the additional parameters
        String defaultName = "External Content " + id;
        String defaultUrl = "https://external-content.com/content/" + id;

        Content content = contentProviderExternal.fetchContentFromUrl(id, defaultName, defaultUrl);

        if (content != null) {
            System.out.println("[Adapter] Successfully adapted external content: " + content.getTitle());
        } else {
            System.out.println("[Adapter] Failed to adapt external content with ID: " + id);
        }

        return content;
    }

    /**
     * Lists all available content IDs from the external provider.
     * @return An array of content IDs
     */
    public String[] listAvailableIds() {
        return contentProviderExternal.listAvailableContentIds();
    }
}
