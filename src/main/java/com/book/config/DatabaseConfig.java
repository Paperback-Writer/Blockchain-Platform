package com.book.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        return new MockDataSource();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
    
    /**
     * 模拟数据源实现，所有方法都不执行实际操作
     */
    private static class MockDataSource implements DataSource {
        @Override
        public Connection getConnection() throws SQLException {
            throw new SQLException("这是一个模拟数据源，不支持真实连接");
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            throw new SQLException("这是一个模拟数据源，不支持真实连接");
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            throw new SQLException("这是一个模拟数据源，不支持unwrap操作");
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return false;
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            return new PrintWriter(System.out);
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {
            // 不执行任何操作
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {
            // 不执行任何操作
        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return 0;
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            throw new SQLFeatureNotSupportedException();
        }
    }
}