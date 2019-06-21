package org.metabrainz.mobile.test.handler;

import org.junit.Before;
import org.junit.Test;
import org.metabrainz.mobile.api.data.obsolete.ReleaseSearchResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ReleaseSearchTest extends BaseXmlParsingTestCase {

    private static final String RELEASE_SEARCH_FIXTURE = "releaseSearch_songs about leaving.xml";
    private LinkedList<ReleaseSearchResult> releases;

    @Before
    public void doParsing() throws IOException {
        InputStream stream = getFileStream(RELEASE_SEARCH_FIXTURE);
        assertNotNull(stream);
        releases = new ResponseParser().parseReleaseSearch(stream);
        stream.close();
    }

    @Test
    public void testReleaseSearch() {
        assertEquals(25, releases.size());
    }

}
