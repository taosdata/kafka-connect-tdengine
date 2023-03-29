package com.moon.core.util.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;

/**
 * @author moonsky
 */
public class XmlParser {

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
//        SAXParserFactory factory = SAXParserFactory.newInstance();
//        SAXParser parser = factory.newSAXParser();
//        XMLReader reader = parser.getXMLReader();

        File xmlFile = new File("D:/test.xml");

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder domBuilder = domFactory.newDocumentBuilder();

        Document document = domBuilder.parse(xmlFile);

        System.out.println(document.getXmlEncoding());
        System.out.println(document.getPrefix());
        System.out.println(document.getDoctype());
        System.out.println(document.getXmlVersion());
        Element domElement = document.getDocumentElement();
        domElement.getChildNodes();
        System.out.println();
    }
}
