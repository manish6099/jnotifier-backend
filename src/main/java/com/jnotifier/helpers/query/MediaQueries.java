package com.jnotifier.helpers.query;

public class MediaQueries {
    public static final String GET_ALL_ACTIVE_MEDIA = "SELECT m FROM Media m WHERE m.isPublic = true";
}
