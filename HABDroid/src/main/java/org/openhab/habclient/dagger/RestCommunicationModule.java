package org.openhab.habclient.dagger;

import org.openhab.domain.DocumentFactory;
import org.openhab.domain.IDocumentFactory;
import org.openhab.domain.IRestCommunication;
import org.openhab.habclient.RestCommunication;

import dagger.Module;
import dagger.Provides;

@Module
public class RestCommunicationModule {
    @Provides
    public IDocumentFactory provideDocumentFactory(DocumentFactory documentFactory) {
        return documentFactory;
    }

    @Provides
    public IRestCommunication provideRestCommunication(RestCommunication restCommunication) {
        return restCommunication;
    }
}
