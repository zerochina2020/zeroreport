package com.zerochina.report.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL工具
 *
 * @author jiaquan
 * @date 2020/9/20
 */
public class SqlUtil {
    private static final int MAX_PAGE_SIZE = 200;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final Pattern ORDER_BY_PATTERN = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
    private static final String PAGE_KEYWORD = "limit";
    private static final String[] ILLEGAL_SQL_KEYWORD = {"exec", "insert", "delete", "update"};

    /**
     * 检测报表SQL
     * @param sql
     * @throws Exception
     */
    public static void checkReportSql(String sql) throws Exception {
        for (int i = 0; i < ILLEGAL_SQL_KEYWORD.length; i++) {
            if (sql.toLowerCase().indexOf(ILLEGAL_SQL_KEYWORD[i]) >= 0) {
                throw new Exception("报表SQL中含有非查询内容，请检查SQL");
            }
        }
    }

    /**
     * 获取总页数
     * @param totalCount 要分页的总记录数
     * @param pageSize 每页记录数大小
     * @return
     */
    public static int getTotalPage(final long totalCount, final int pageSize) {
        if (totalCount % pageSize == 0) {
            return (int) (totalCount / pageSize);
        } else {
            return (int) (totalCount / pageSize + 1);
        }
    }

    /**
     * 校验当前页数pageCurrent<br/>
     * 1、先根据总记录数totalCount和每页记录数pageSize，计算出总页数totalPage<br/>
     * 2、判断页面提交过来的当前页数pageCurrent是否大于总页数totalPage，大于则返回totalPage<br/>
     * 3、判断pageCurrent是否小于1，小于则返回1<br/>
     * 4、其它则直接返回pageCurrent
     *
     * @param totalCount  要分页的总记录数
     * @param pageSize    每页记录数大小
     * @param pageCurrent 输入的当前页数
     * @return pageCurrent
     */
    public static int checkPageCurrent(long totalCount, int pageSize, int pageCurrent) {
        int totalPage = getTotalPage(totalCount, pageSize);
        if (pageCurrent > totalPage) {
            if (totalPage < 1) {
                return 1;
            }
            return totalPage;
        } else if (pageCurrent < 1) {
            return 1;
        } else {
            return pageCurrent;
        }
    }

    /**
     * 校验页面输入的每页记录数pageSize是否合法<br/>
     * 1、当页面输入的每页记录数pageSize大于允许的最大每页记录数MAX_PAGE_SIZE时，返回MAX_PAGE_SIZE
     * 2、如果pageSize小于1，则返回默认的每页记录数DEFAULT_PAGE_SIZE
     *
     * @param pageSize 页面输入的每页记录数
     * @return checkPageSize
     */
    public static int checkPageSize(int pageSize) {
        if (pageSize > MAX_PAGE_SIZE) {
            return MAX_PAGE_SIZE;
        } else if (pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        } else {
            return pageSize;
        }
    }

    /**
     * 计算当前分页的开始记录的索引
     *
     * @param pageCurrent 当前第几页
     * @param pageSize    每页记录数
     * @return 当前页开始记录号
     */
    public static int countOffset(final int pageCurrent, final int pageSize) {
        return (pageCurrent - 1) * pageSize;
    }

    /**
     * 获取分页查询SQL语句
     * @param sql
     * @param totalCount
     * @param pageCurrent
     * @param pageSize
     * @return
     */
    public static String getPageSql(String sql, long totalCount, int pageCurrent, int pageSize) {
        pageCurrent = checkPageCurrent(totalCount, pageSize, pageCurrent);
        pageSize = checkPageSize(pageSize);
        if(sql.toLowerCase().contains(PAGE_KEYWORD)){
            sql = sql.substring(0, sql.toLowerCase().indexOf(PAGE_KEYWORD));
        }
        return sql + " limit " + countOffset(pageCurrent, pageSize) + "," + pageSize;
    }

    /**
     * 根据分页查询的SQL语句，获取统计总记录数的语句
     *
     * @param sql 分页查询的SQL
     * @return countSql
     */
    public static String getCountSql(String sql) {
        String countSql = sql.substring(sql.toLowerCase().indexOf("from"));
        return "select count(*) " + removeOrderBy(countSql);
    }

    /**
     * 移除SQL语句中的的order by子句（用于分页前获取总记录数，不需要排序）
     *
     * @param sql 原始SQL
     * @return 去除order by子句后的内容
     */
    private static String removeOrderBy(String sql) {
        Matcher matcher = ORDER_BY_PATTERN.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
