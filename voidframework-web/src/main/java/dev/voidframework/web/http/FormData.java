package dev.voidframework.web.http;

import java.util.HashMap;
import java.util.List;

/**
 * Data extracted from the body content.
 *
 * @since 1.3.0
 */
public final class FormData extends HashMap<String, List<FormItem>> {

    /**
     * Retrieves the first form item associated to the given name.
     *
     * @param name The item name
     * @return The form item, otherwise, {@code null}
     * @since 1.3.0
     */
    public FormItem getFirst(final String name) {

        final List<FormItem> formItemList = this.get(name);
        if (formItemList == null || formItemList.isEmpty()) {
            return null;
        }

        return formItemList.get(0);
    }
}
