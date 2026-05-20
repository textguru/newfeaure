package kr.or.nqis.qis.exam;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 지사별 시행종목 조회 모듈 Spring Boot Application.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@EnableCaching
@SpringBootApplication
@MapperScan("kr.or.nqis.qis.exam.dao")
public class ExamApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExamApplication.class, args);
    }
}
