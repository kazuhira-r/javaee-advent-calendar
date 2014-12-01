package javaeeadventcalendar.jcache;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.*;

import java.util.List;
import java.util.stream.StreamSupport;
import java.util.stream.Collectors;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.CacheException;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;

import org.junit.Test;

public class JCacheMixedTest {
    @Test(expected = CacheException.class)
    public void testNoSpecifyImplemantation() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
    }

    @Test
    public void testSpecifyImplementation() {
        CachingProvider riCachingProvider =
            Caching.getCachingProvider("org.jsr107.ri.spi.RICachingProvider");
        CachingProvider ehcacheCachingProvider =
            Caching.getCachingProvider("org.ehcache.jcache.JCacheCachingProvider");
        CachingProvider infinispanCachingProvider =
            Caching.getCachingProvider("org.infinispan.jcache.JCachingProvider");
        CachingProvider hazelcastCachingProvider =
            Caching.getCachingProvider("com.hazelcast.cache.impl.HazelcastServerCachingProvider");
            // もしくは
            // Caching.getCachingProvider("com.hazelcast.client.cache.impl.HazelcastClientCachingProvider");

        CacheManager riCacheManager =
            riCachingProvider.getCacheManager();
        CacheManager ehcacheCacheManager =
            ehcacheCachingProvider.getCacheManager();
        CacheManager infinispanCacheManager =
            infinispanCachingProvider.getCacheManager();
        CacheManager hazelcastCacheManager =
            hazelcastCachingProvider.getCacheManager();

        Configuration<String, Integer> config =
            new MutableConfiguration<String, Integer>()
            .setTypes(String.class, Integer.class);

        riCacheManager.createCache("cache", config);
        ehcacheCacheManager.createCache("cache", config);
        infinispanCacheManager.createCache("cache", config);
        hazelcastCacheManager.createCache("cache", config);

        riCacheManager.close();
        ehcacheCacheManager.close();
        infinispanCacheManager.close();
        hazelcastCacheManager.close();

        riCachingProvider.close();
        ehcacheCachingProvider.close();
        infinispanCachingProvider.close();
        hazelcastCachingProvider.close();
    }

    @Test
    public void testCacheProviderIteration() {
        for (CachingProvider cachingProvider : Caching.getCachingProviders()) {
            // ...
        }

        List<Class<?>> cachingProviderClasses =
            StreamSupport
            .stream(Caching.getCachingProviders().spliterator(), false)
            .map(CachingProvider::getClass)
            .collect(Collectors.toList());

        assertThat(cachingProviderClasses,
                   containsInAnyOrder(org.jsr107.ri.spi.RICachingProvider.class,
                                      org.ehcache.jcache.JCacheCachingProvider.class,
                                      org.infinispan.jcache.JCachingProvider.class,
                                      com.hazelcast.cache.impl.HazelcastCachingProvider.class));
    }
}
