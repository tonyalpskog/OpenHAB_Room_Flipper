package org.openhab.domain;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DocumentFactory implements IDocumentFactory {
    private final DocumentBuilderFactory mBuilderFactory;

    @Inject
    public DocumentFactory() {
        mBuilderFactory = DocumentBuilderFactory.newInstance();
    }

    @Override
    public Document build(String responseBody) throws ParserConfigurationException, IOException, SAXException {
        final DocumentBuilder builder = mBuilderFactory.newDocumentBuilder();
        if (responseBody != null) {
            return builder.parse(new ByteArrayInputStream(responseBody.getBytes()));
        } else
            return null;
    }
}
