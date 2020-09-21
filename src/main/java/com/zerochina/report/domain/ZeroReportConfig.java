package com.zerochina.report.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * 报表模板
 * @author jiaquan
 * @date 2020/9/20
 */
@Data
public class ZeroReportConfig {
    private String id;
    /**
     * 报表名称
     */
    private String reportName;
    /**
     * 报表编码
     */
    private String reportCode;
    /**
     * 可查看报表角色(多个,号分隔)
     */
    private String visibleRole;
    /**
     * 可查看报表人员(多个,号分隔)
     */
    private String visibleUser;
    /**
     * 报表备注
     */
    private String reportRemark;
    /**
     * 报表访问链接
     */
    private String reportLink;
    /**
     * 报表查询SQL
     */
    private String reportSql;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdDate;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastUpdatedDate;

    /**
     * 解析报表配置信息
     * @param resultSet
     * @return
     * @throws SQLException
     */
    public static ZeroReportConfig parseZeroReportConfig(ResultSet resultSet) throws SQLException {
        ZeroReportConfig reportConfig = new ZeroReportConfig();
        reportConfig.setId(resultSet.getString("id"));
        reportConfig.setReportName(resultSet.getString("report_name"));
        reportConfig.setReportCode(resultSet.getString("report_code"));
        reportConfig.setVisibleRole(resultSet.getString("visible_role"));
        reportConfig.setVisibleUser(resultSet.getString("visible_user"));
        reportConfig.setReportRemark(resultSet.getString("report_remark"));
        reportConfig.setReportLink("/zeroreport/template/"+reportConfig.getReportCode());
        reportConfig.setReportSql(resultSet.getString("report_sql"));
        reportConfig.setCreatedDate(resultSet.getTimestamp("created_date").toLocalDateTime());
        reportConfig.setLastUpdatedDate(resultSet.getTimestamp("last_updated_date").toLocalDateTime());
        return reportConfig;
    }
}
