package com.moon.core.net.enums;

import com.moon.core.enums.EnumDescriptor;

/**
 * @author moonsky
 */
public enum ContentType implements EnumDescriptor {
    /**
     * application/json
     */
    application_json,
    /**
     * text/plain
     */
    text_plain,
    /**
     * text/html
     */
    text_html,
    /**
     * all
     */
    any("*/*"),
    /**
     * all
     */
    all("*/*"),
    ;

    private final String contentType;

    ContentType() {
        String name = name();
        this.contentType = name.replace('_', '/');
    }

    ContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getText() { return contentType; }
}
