package com.voidframework.core.helper;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Helper to handle XML document.
 */
public final class Xml {

    /**
     * Converts an XML to string.
     *
     * @param xml The XML to convert.
     * @return The string representation.
     */
    public static String toString(final Document xml) {
        final Writer writer = new StringWriter();
        try {
            TransformerFactory.newInstance()
                .newTransformer()
                .transform(new DOMSource(xml), new StreamResult(writer));
            writer.flush();
        } catch (final TransformerException | IOException e) {
            throw new RuntimeException(e);
        }

        return writer.toString();
    }

    /**
     * Converts a byte array to an XML document.
     *
     * @param data data to convert in XML
     * @return The XML document
     */
    public static Document toXml(final byte[] data) {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            final InputStream inputStream = new ByteArrayInputStream(data);
            return builder.parse(inputStream);
        } catch (final ParserConfigurationException | SAXException | IOException e) {
            return null;
        }
    }
}
