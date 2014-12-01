package javaeeadventcalendar.jcache;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.CacheException;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;

// 起動時に、以下の様にCachingProviderの実装を指定
// -Djavax.cache.spi.CachingProvider=org.jsr107.ri.spi.RICachingProvider
public class JCacheMixed {
    public static void main(String... args) {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        System.out.println(cachingProvider);
    }
}
