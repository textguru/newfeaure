package kr.or.nqis.qis.exam.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * 캐시 설정.
 * - ehcache.xml 기반 (Spring Cache Abstraction 사용)
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@EnableCaching
@Configuration
public class CacheConfig {
}
