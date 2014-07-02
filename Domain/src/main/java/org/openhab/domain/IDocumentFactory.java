package org.openhab.domain;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

public interface IDocumentFactory {
    Document build(String responseBody) throws ParserConfigurationException, IOException, SAXException;
}
