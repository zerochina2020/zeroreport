package com.zerochina.report.service;

import com.zerochina.report.domain.Result;
import com.zerochina.report.domain.ZeroReportConfig;
import com.zerochina.report.util.SqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 报表服务类
 * @author jiaquan
 * @date 2020/9/19
 */
@Slf4j
public class ZeroReportService {
    private static final String CREATE_CONFIG_SQL = "INSERT INTO zero_report_config (`report_name`,`report_code`,`visible_role`,`visible_user`,`report_remark`,`report_sql`,`created_date`,`last_updated_date`) VALUES (?,?,?,?,?,?,CURRENT_TIMESTAMP (),CURRENT_TIMESTAMP ())";
    private static final String UPDATE_CONFIG_SQL = "UPDATE zero_report_config SET `report_name`=?,`report_code`=?,`visible_role`=?,`visible_user`=?,`report_remark`=?,`report_sql`=?,`last_updated_date`=CURRENT_TIMESTAMP () WHERE report_code=?";
    private static final String DELETE_CONFIG_SQL = "DELETE FROM zero_report_config WHERE report_code=?";
    private static final String QUERY_PAGE_CONFIG_SQL = "SELECT * FROM zero_report_config";
    private static final String QUERY_ONE_CONFIG_SQL = "SELECT * FROM zero_report_config WHERE report_code=? ORDER BY last_updated_date DESC LIMIT 1";
    private final JdbcTemplate jdbcTemplate;

    public ZeroReportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 分页查询配置信息
     * @return
     */
    public Result findZeroReportConfigByPage(Integer page, Integer limit){
        Result result = new Result("SUCCESS", "获取成功");
        try {
            String countSql = SqlUtil.getCountSql(QUERY_PAGE_CONFIG_SQL);
            long count = jdbcTemplate.queryForObject(countSql, Long.class);
            String pageSql = SqlUtil.getPageSql(QUERY_PAGE_CONFIG_SQL, count, page, limit);
            List<ZeroReportConfig> reportConfigs = jdbcTemplate.query(pageSql, (resultSet, i) -> ZeroReportConfig.parseZeroReportConfig(resultSet));
            result.setTotalSize(Long.toString(count));
            result.setData(reportConfigs);
        } catch (DataAccessException e) {
            log.error("分页查询报表配置异常，异常信息:{}", e.getMessage());
            result.setErrorCode("ERROR");
            result.setMessage("分页查询报表配置异常");
        }
        return result;
    }

    /**
     * 根据配置编码查询配置信息
     * @param reportCode
     * @return
     */
    public ZeroReportConfig findZeroReportConfigByReportCode(String reportCode){
        try {
            return jdbcTemplate.queryForObject(QUERY_ONE_CONFIG_SQL, (resultSet, i) -> ZeroReportConfig.parseZeroReportConfig(resultSet), reportCode);
        } catch (DataAccessException e) {
            return null;
        }
    }

    /**
     * 新增或更新配置信息
     * @param reportConfig
     * @return
     */
    public void saveZeroReportConfig(ZeroReportConfig reportConfig) throws Exception {
        if(StringUtils.isEmpty(reportConfig.getReportCode())){
            throw new Exception("报表编码不能为空");
        }
        verifySql(reportConfig.getReportSql());
        try {
            ZeroReportConfig old = findZeroReportConfigByReportCode(reportConfig.getReportCode());
            if(null==old){
                jdbcTemplate.update(CREATE_CONFIG_SQL, reportConfig.getReportName(), reportConfig.getReportCode(),
                        reportConfig.getVisibleRole(), reportConfig.getVisibleUser(), reportConfig.getReportRemark(),
                        reportConfig.getReportSql());
            }else {
                jdbcTemplate.update(UPDATE_CONFIG_SQL, reportConfig.getReportName(), reportConfig.getVisibleRole(),
                        reportConfig.getVisibleUser(), reportConfig.getReportRemark(), reportConfig.getReportSql(),
                        reportConfig.getReportCode());
            }
        } catch (DataAccessException e) {
            log.error("新增或更新报表配置信息异常，异常信息：{}", e.getMessage());
            throw new Exception("新增或更新报表配置信息异常，异常信息："+e.getMessage());
        }
    }

    /**
     * 验证报表SQL
     * @param sql
     * @return
     */
    private void verifySql(String sql) throws Exception {
        if(StringUtils.isEmpty(sql)){
            throw new Exception("报表SQL不能为空");
        }
        SqlUtil.checkReportSql(sql);
        try {
            jdbcTemplate.queryForList(sql);
        } catch (DataAccessException e) {
            log.error("SQL校验异常，异常信息：{}", e.getMessage());
            throw new Exception("SQL执行出错，请检查SQL");
        }
    }
}
