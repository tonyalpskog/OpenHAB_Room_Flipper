package org.openhab.test.habclient.command;

import org.openhab.habclient.HABApplication;

import org.w3c.dom.Document;

public interface IAsyncHttpClientAsAsyncTask {
    void doAsync(Listener l);

    public interface Listener{
        void onValueChanged(String resultDescription, Document document);
    }
}