package com.zenit.habclient.test.command;

import org.openhab.habdroid.core.DocumentHttpResponseHandler;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Tony Alpskog in 2014.
 */
public class DocumentHttpResponseHandlerWrapper extends DocumentHttpResponseHandler {

    @Override
    public Document parseResponse(String responseBody) throws ParserConfigurationException, IOException, SAXException {
        return super.parseResponse(responseBody);
    }
}
