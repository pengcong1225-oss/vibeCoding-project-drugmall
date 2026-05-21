package com.drugmall.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 数据库初始化组件
 * 在应用启动时检查并创建缺失的表（仅开发阶段使用）
 */
@Slf4j
@Component
public class DatabaseInitializer implements ApplicationRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) {
        try (Connection conn = dataSource.getConnection()) {
            log.info("正在初始化搜索相关表...");
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("sql/search-init.sql"));
            log.info("搜索相关表初始化完成");
        } catch (Exception e) {
            log.warn("搜索表初始化失败（可能已存在）: {}", e.getMessage());
        }
    }
}
