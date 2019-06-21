package org.metabrainz.mobile.test.handler;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ReleaseWithRelationshipsTest extends BaseXmlParsingTestCase {

    private static final String RELEASE_LOOKUP_FIXTURE = "releaseLookup_ade577f6-6087-4a4f-8e87-38b0f8169814.xml";
    private Release release;

    @Before
    public void doParsing() throws IOException {
        InputStream stream = getFileStream(RELEASE_LOOKUP_FIXTURE);
        assertNotNull(stream);
        release = new ResponseParser().parseRelease(stream);
        stream.close();
    }

    @Test
    public void testArtistName() {
        assertEquals(1, release.getArtists().size());
        assertEquals("The Beatles", release.getArtists().get(0).getName());
    }

}
