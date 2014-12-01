package javaeeadventcalendar.jcache;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.*;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;

import org.junit.Test;

public class JCacheTest {
    @Test
    public void testSimple() throws InterruptedException {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();

        Configuration<String, Integer> config =
            new MutableConfiguration<String, Integer>()
            .setTypes(String.class, Integer.class)
            .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, 5)));

        Cache<String, Integer> cache =
            cacheManager.createCache("simpleCache", config);

        // put
        cache.put("key", 10);
        assertThat(cache.get("key"), is(10));

        // remove
        cache.remove("key");
        assertThat(cache.get("key"), nullValue());

        // Expire
        cache.put("key1", 10);
        cache.put("key2", 20);

        TimeUnit.SECONDS.sleep(3);
        cache.get("key1");

        TimeUnit.SECONDS.sleep(3);

        assertThat(cache.get("key1"), is(10));         // 1度アクセスしているので、まだ有効なエントリ
        assertThat(cache.get("key2"), nullValue());  // こちらは有効期限切れ

        TimeUnit.SECONDS.sleep(6);

        assertThat(cache.get("key1"), nullValue());         // こちらも有効期限切れ

        // Cache名、キーと値の型を指定して、Cacheを取得
        Cache<String, Integer> definedCache =
            cacheManager.getCache("simpleCache", String.class, Integer.class);
        assertThat(definedCache, not(nullValue()));

        // Cache、CacheManager、CachingProviderはCloseableです
        cache.close();
        cacheManager.close();
        cachingProvider.close();
    }

    @Test
    public void testMultipleCacheManagerAndCache() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        CachingProvider cachingProvider = Caching.getCachingProvider();

        // URIで、CacheManagerを分ける
        // JCacheの実装によっては、URIから設定ファイルを読み込むことも可能
        CacheManager cacheManager1 =
            cachingProvider.getCacheManager(URI.create("cacheManager1"), classLoader);
        CacheManager cacheManager2 =
            cachingProvider.getCacheManager(URI.create("cacheManager2"), classLoader);

        Configuration<String, Integer> config1 =
            new MutableConfiguration<String, Integer>()
            .setTypes(String.class, Integer.class);

        Configuration<String, String> config2 =
            new MutableConfiguration<String, String>()
            .setTypes(String.class, String.class);

        // それぞれのCacheManagerに、Cacheを作成
        cacheManager1.createCache("cache1FromCacheManager1", config1);
        cacheManager1.createCache("cache2FromCacheManager1", config1);

        cacheManager2.createCache("cacheFromCacheManager2", config2);

        // CacheManagerごとに、Cacheは別々に管理される
        assertThat(cacheManager1.getCacheNames(),
                   containsInAnyOrder("cache1FromCacheManager1", "cache2FromCacheManager1"));
        assertThat(cacheManager1.getCache("cacheFromCacheManager2"), nullValue());

        assertThat(cacheManager2.getCacheNames(),
                   containsInAnyOrder("cacheFromCacheManager2"));
        assertThat(cacheManager2.getCache("cache1FromCacheManager1"), nullValue());
        assertThat(cacheManager2.getCache("cache2FromCacheManager1"), nullValue());

        cacheManager1.close();
        cacheManager2.close();
        cachingProvider.close();
    }
}
