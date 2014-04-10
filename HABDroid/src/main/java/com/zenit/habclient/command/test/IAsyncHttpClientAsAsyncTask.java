package com.zenit.habclient.command.test;

import android.content.Context;

import com.zenit.habclient.HABApplication;

import org.w3c.dom.Document;

public interface IAsyncHttpClientAsAsyncTask {
    void doAsync(HABApplication habApplication, Listener l);

    public interface Listener{
        void onValueChanged(String resultDescription, Document document);
    }
}