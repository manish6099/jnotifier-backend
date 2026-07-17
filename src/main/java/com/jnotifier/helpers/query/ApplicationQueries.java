package com.jnotifier.helpers.query;

public class ApplicationQueries {
    public static final String GET_ALL_ARCHIVED_PUBLIC_JOBS = "SELECT a FROM Application a WHERE a.status = false";
}
