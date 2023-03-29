package com.moon.core.util.xml.dom;

import com.moon.core.util.ListUtil;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * @author moonsky
 */
public class ElementSet implements AttrOperator<ElementSet> {

    private final ArrayList elements = new ArrayList();

    public ElementSet(NodeList list) {
        ArrayList elements = this.elements;
        final int len = list == null ? 0 : list.getLength();
        ListUtil.increaseCapacity(elements, len);
        for (int i = 0; i < len; i++) {
            elements.add(list.item(i));
        }
    }

    @Override
    public String attr(String name) {

        return name;
    }

    @Override
    public ElementSet attr(String name, Object value) {

        return this;
    }

    public ElementSet find(String selector) {
        // other ElementSet
        return null;
    }
}
