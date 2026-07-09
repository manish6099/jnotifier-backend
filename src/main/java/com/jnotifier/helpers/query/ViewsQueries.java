package com.jnotifier.helpers.query;

public class ViewsQueries {
    public static final String GET_PAGE_VIEWS =
            "SELECT new com.jnotifier.payload.pojo.PageViewsPojo(" +
                    "   v.ipAddress, " +
                    "   v.visitedDate, " +
                    "   COUNT(v.ipAddress), " +
                    "   v.visitedPage" +
                    ") " +
                    "FROM Views v " + // Assumes your @Entity class is named 'Views'
                    "WHERE v.visitedPage = :visitedPage " +
                    "  AND v.visitedDate IS NOT NULL " +
                    "  AND v.visitedPage IS NOT NULL " +
                    "GROUP BY v.ipAddress, v.visitedDate, v.visitedPage";
}