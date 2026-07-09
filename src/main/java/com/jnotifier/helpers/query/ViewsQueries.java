package com.jnotifier.helpers.query;

public class ViewsQueries {
    public static final String GET_PAGE_VIEWS = "SELECT\n" +
            "\tIP_ADDRESS,\n" +
            "\tVISITED_DATE,\n" +
            "\tCOUNT(IP_ADDRESS) AS PAGE_VIEWS,\n" +
            "\tVISITED_PAGE\n" +
            "FROM\n" +
            "\tPUBLIC.VIEWS\n" +
            "WHERE\n" +
            "\tVISITED_PAGE = :visitedPage \n" +
            "GROUP BY\n" +
            "\tIP_ADDRESS,\n" +
            "\tVISITED_DATE,\n" +
            "\tVISITED_PAGE\n" +
            "HAVING\n" +
            "\tVISITED_DATE IS NOT NULL\n" +
            "\tAND VISITED_PAGE IS NOT NULL;";
}
