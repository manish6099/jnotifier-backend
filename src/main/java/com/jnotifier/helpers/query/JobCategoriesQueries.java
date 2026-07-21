package com.jnotifier.helpers.query;

public class JobCategoriesQueries {
    public static final String GET_ALL_JOB_CATEGORIES_BY_ADMIN = "SELECT jc FROM JobCategories jc WHERE jc.createdBy = :createdBy";
}
