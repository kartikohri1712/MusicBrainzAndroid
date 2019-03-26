package org.metabrainz.mobile.api.data;

import com.google.gson.annotations.SerializedName;

public class MBEntity {
    @SerializedName("id")
    private String id;
    @SerializedName("sort-name")
    private String sortName;
    private String disambiguation;
}
