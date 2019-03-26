package org.metabrainz.mobile.api.handler;

import java.util.LinkedList;

import org.metabrainz.mobile.api.data.obsolete.UserSearchResult;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CollectionListHandler extends MBHandler {

    private LinkedList<UserSearchResult> collections = new LinkedList<UserSearchResult>();
    private UserSearchResult editorCollection;

    public LinkedList<UserSearchResult> getResults() {
        return collections;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

        if (localName.equals("collection")) {
            editorCollection = new UserSearchResult();
            editorCollection.setMbid(atts.getValue("id"));
        } else if (localName.equals("name")) {
            buildString();
        } else if (localName.equals("editor")) {
            buildString();
        } else if (localName.equals("release-list")) {
            editorCollection.setCount(Integer.parseInt(atts.getValue("count")));
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {

        if (localName.equals("name")) {
            editorCollection.setName(getString());
        } else if (localName.equals("editor")) {
            editorCollection.setEditor(getString());
        } else if (localName.equals("collection")) {
            collections.add(editorCollection);
        }
    }

}
