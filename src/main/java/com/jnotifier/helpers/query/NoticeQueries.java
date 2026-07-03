package com.jnotifier.helpers.query;

public class NoticeQueries {
    public static final String GET_NOTICE_BY_DELETION_STATUS = "SELECT n FROM Notice n WHERE n.isDeleted = :isDeleted AND n.createdBy = :createdBy";
    public static final String GET_NOTICE_BY_ACTIVE_STATUS = "SELECT n FROM Notice n WHERE n.isActive = :isActive AND n.createdBy = :createdBy";
}
