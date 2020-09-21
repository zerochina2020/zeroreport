package com.zerochina.report.starter;

import com.zerochina.report.service.ZeroReportService;
import com.zerochina.report.servlet.ZeroReportServlet;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Zero Report starter自动配置类
 *
 * @author jiaquan
 * @date 2020/8/7
 */
@Configuration
@ConditionalOnWebApplication
@AutoConfigurationPackage
@Slf4j
public class ZeroReportAutoConfiguration {

    @Bean
    public Liquibase encryptLiquibase(DataSource dataSource) throws Exception {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            JdbcConnection jdbcConnection = new JdbcConnection(connection);
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);
            database.setDatabaseChangeLogLockTableName("zero_database_changelog_lock");
            database.setDatabaseChangeLogTableName("zero_database_changelog");
            Liquibase liquibase = new Liquibase("db/zeroreport/master.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts(), new LabelExpression());
            return liquibase;
        } catch (Exception e) {
            log.debug("Zero Report报表插件表结构更新异常，异常信息：{}", e.getMessage());
            throw new Exception("Zero Report报表插件表结构更新异常，异常信息：" + e.getMessage());
        } finally {
            if (null != connection) {
                connection.close();
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public ZeroReportService zeroReportService(JdbcTemplate jdbcTemplate) {
        return new ZeroReportService(jdbcTemplate);
    }

    @Bean
    public ServletRegistrationBean encryptViewServletRegistrationBean(ZeroReportService zeroReportService) {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        registrationBean.setServlet(new ZeroReportServlet(zeroReportService));
        registrationBean.addUrlMappings("/zeroreport/*");
        return registrationBean;
    }
}
