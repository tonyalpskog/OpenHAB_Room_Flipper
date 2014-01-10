/**
 * openHAB, the open Home Automation Bus.
 * Copyright (C) 2010-2012, openHAB.org <admin@openhab.org>
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Eclipse (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public License
 * (EPL), the licensors of this Program grant you additional permission
 * to convey the resulting work.
 */

package org.openhab.habdroid.core;

import android.os.Message;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DocumentHttpResponseHandler extends AsyncHttpResponseHandler {

    protected static final int SUCCESS_MESSAGE = 0;
    protected static final int FAILURE_MESSAGE = 1;
    protected static final int START_MESSAGE = 2;
    protected static final int FINISH_MESSAGE = 3;

    public void onSuccess(Document response) {}

    public void onSuccess(int statusCode, Header[] headers, Document response) {
        onSuccess(statusCode, response);
    }

    public void onSuccess(int statusCode, Document response) {
        onSuccess(response);
    }

    //
    // Pre-processing of messages (in original calling thread, typically the UI thread)
    //

    protected void handleSuccessMessage(int statusCode, Header[] headers, Document response) {
        onSuccess(statusCode, headers, response);
    }

    // Methods which emulate android's Handler and Message methods
    protected void handleMessage(Message msg) {
        Object[] response;

        switch(msg.what) {
            case SUCCESS_MESSAGE:
                response = (Object[])msg.obj;
                Document responseDocument = null;
                Log.d("DocumentHttpResponseHandler", String.format("response[%d]", response.length));
                try {
                    Log.d("DocumentHttpResponseHandler", "Got response = " + (String) response[2].toString());
                    responseDocument = parseResponse(response[2]);
                    onSuccess(responseDocument);//TODO - Added by TA
//                    handleSuccessMessage(((Integer) response[0]).intValue(), (Header[]) response[1], responseDocument);
                } catch (ParserConfigurationException e) {
//                    handleFailureMessage(e, (String) response[2]);//TODO  - removed by TA
                    Log.e("DocumentHttpResponseHandler", "Got ParserConfigurationException in handleMessage()");
                } catch (IOException e) {
//                    handleFailureMessage(e, (String) response[2]);//TODO  - removed by TA
                    Log.e("DocumentHttpResponseHandler", "Got IOException in handleMessage()");
                } catch (SAXException e) {
//                    handleFailureMessage(e, (String) response[2]);//TODO  - removed by TA
                    Log.e("DocumentHttpResponseHandler", "Got SAXException in handleMessage()");
                }
                break;
            case FAILURE_MESSAGE:
                response = (Object[])msg.obj;
//                handleFailureMessage((Throwable)response[0], (String)response[1]);//TODO  - removed by TA
                Log.e("DocumentHttpResponseHandler", "Got FAILURE_MESSAGE in handleMessage()");
                break;
            case START_MESSAGE:
                onStart();
                break;
            case FINISH_MESSAGE:
                onFinish();
                break;
        }
    }

    protected Document parseResponse(Object responseBody) throws ParserConfigurationException, IOException, SAXException {
        if(responseBody instanceof byte[])
            return parseResponse((byte[]) responseBody);
        else if(responseBody instanceof String)
            return parseResponse((String) responseBody);
        else
            return null;
    }

    protected Document parseResponse(String responseBody) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document;
        DocumentBuilder builder = factory.newDocumentBuilder();
        if (responseBody != null) {
            document = builder.parse(new ByteArrayInputStream(responseBody.getBytes()));
            return document;
        } else
            return null;
    }

    protected Document parseResponse(byte[] responseBody) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document;
        DocumentBuilder builder = factory.newDocumentBuilder();
        if (responseBody != null) {
            document = builder.parse(new ByteArrayInputStream(responseBody));
            return document;
        } else
            return null;
    }
}
