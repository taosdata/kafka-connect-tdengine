package com.moon.core.util;

import com.moon.core.util.interfaces.Parser;

import java.util.Map;

/**
 * @author moonsky
 */
class PropertiesYamlParser extends PropertiesParser implements Parser<PropertiesHashMap, String> {

    public PropertiesYamlParser() { }

    public PropertiesYamlParser(String namespace) { super(namespace); }

    public PropertiesYamlParser(String namespace, boolean bubbleDelimiters) { super(namespace, bubbleDelimiters); }

    public PropertiesYamlParser(
        String namespace, boolean bubbleDelimiters, Map<String, PropertiesHashMap> parsedSources
    ) { super(namespace, bubbleDelimiters, parsedSources); }

    @Override
    protected PropertiesParser getParser(
        String namespace, boolean bubbleDelimiters, Map<String, PropertiesHashMap> parsedSources
    ) { return new PropertiesParser(namespace, bubbleDelimiters, parsedSources); }

    @Override
    protected PropertiesHashMap getResources(String sourcePath) { throw new UnsupportedOperationException(); }
}
