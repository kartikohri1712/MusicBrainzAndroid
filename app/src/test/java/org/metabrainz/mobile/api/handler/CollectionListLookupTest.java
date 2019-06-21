package org.metabrainz.mobile.test.handler;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CollectionListLookupTest extends BaseXmlParsingTestCase {

    private static final String COLLECTION_LIST_FIXTURE = "collectionListLookup.xml";
    private LinkedList<UserSearchResult> collections;

    @Before
    public void doParsing() throws IOException {
        InputStream stream = getFileStream(COLLECTION_LIST_FIXTURE);
        assertNotNull(stream);
        collections = new ResponseParser().parseCollectionListLookup(stream);
        stream.close();
    }

    @Test
    public void testNumberOfResults() {
        assertEquals(collections.size(), 2);
    }

    @Test
    public void testFirstResult() {
        UserSearchResult first = collections.get(0);
        assertEquals("afc8bea7-5ffc-488d-b32f-38e71bdd9e4e", first.getMbid());
        assertEquals("Want list", first.getName());
        assertEquals("jdamcd", first.getEditor());
        assertEquals(1, first.getCount());
    }

    @Test
    public void testLastResult() {
        UserSearchResult last = collections.get(1);
        assertEquals("c6f9fb72-e233-47f4-a2f6-19f16442d93a", last.getMbid());
        assertEquals("My Collection", last.getName());
        assertEquals("jdamcd", last.getEditor());
        assertEquals(2, last.getCount());
    }

}
