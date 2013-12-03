package jcache.example;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.Cache.Entry;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.expiry.ModifiedExpiryPolicy;
import javax.cache.expiry.TouchedExpiryPolicy;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.MutableEntry;
import javax.cache.spi.CachingProvider;

import org.junit.Test;

public class JCacheTest {
    @Test
    public void simpleTest() {
        try (CachingProvider provider = Caching.getCachingProvider();
             CacheManager cacheManager = provider.getCacheManager();
             Cache<String, String> cache = cacheManager.createCache("simpleCache",
                                                                    new MutableConfiguration<String, String>())) {
            // putとget
            cache.put("key1", "value1");
            assertThat(cache.get("key1"), is("value1"));

            // remove
            cache.remove("key1");
            assertThat(cache.get("key1"), nullValue());

            // 全削除
            cache.clear();
            //cache.removeAll(); // 左記でも可。clearの場合は、Listenerが起動しないらしい
            assertThat(cache.get("key1"), nullValue());

            // イテレーション
            for (Cache.Entry<String, String> entry : cache) {
                 // iteration...
            }
        }
    }

    @Test
    public void expireTest() throws Exception {
        CachingProvider provider = Caching.getCachingProvider();
        CacheManager cacheManager = provider.getCacheManager();

        // 有効期限なしのキャッシュ（デフォルト）
        Cache<String, String> eternalCache
            = cacheManager.createCache("eternalCache",
                                       new MutableConfiguration<String, String>()
                                       .setExpiryPolicyFactory(EternalExpiryPolicy.factoryOf()));
        // some code...
        eternalCache.close();

        // putしてから、5秒後に有効期限切れするキャッシュ
        Cache<String, String> createdCache
            = cacheManager.createCache("createdCache",
                                       new MutableConfiguration<String, String>()
                                       .setExpiryPolicyFactory(CreatedExpiryPolicy
                                                               .factoryOf(new Duration(TimeUnit.SECONDS, 5))));
        createdCache.put("key1", "value1");
        Thread.sleep(3 * 1000L);

        assertThat(createdCache.get("key1"), is("value1"));

        Thread.sleep(3 * 1000L);

        // Creationの場合は、getやreplaceのアクセスでは有効期限は延長されない
        assertThat(createdCache.get("key1"), nullValue());

        createdCache.close();


        // 5秒以内にアクセスがあった場合は、有効期限が延長されるキャッシュ
        Cache<String, String> accessedCache
            = cacheManager.createCache("accessedCache",
                                       new MutableConfiguration<String, String>()
                                       .setExpiryPolicyFactory(AccessedExpiryPolicy
                                                               .factoryOf(new Duration(TimeUnit.SECONDS, 5))));
        accessedCache.put("key1", "value1");
        Thread.sleep(3 * 1000L);
        assertThat(accessedCache.get("key1"), is("value1"));

        // 1度getしているので、有効期限が伸びている
        Thread.sleep(4 * 1000L);
        assertThat(accessedCache.get("key1"), is("value1"));

        // 5秒以上経過すると、有効期限切れ
        Thread.sleep(6 * 1000L);
        assertThat(accessedCache.get("key1"), nullValue());

        accessedCache.close();

        cacheManager.close();
        provider.close();
    }

    @Test
    public void entryProcessorTest() {
        try (CachingProvider provider = Caching.getCachingProvider();
             CacheManager cacheManager = provider.getCacheManager();
             Cache<String, Integer> cache = cacheManager.createCache("cache",
                                                                     new MutableConfiguration<String, Integer>())) {

             java.util.Set<String> keySet = new java.util.HashSet<>();
             for (int i = 1; i <= 10; i++) {
                 cache.put("key" + i, i);
                 keySet.add("key" + i);
             }

             // あらかじめ、処理対象のキー集合を渡す必要がある
             Map<String, Integer> result =
                 cache.invokeAll(keySet,
                                 new EntryProcessor<String, Integer, Integer>() {
                                     @Override
                                     public Integer process(MutableEntry<String, Integer> entry, Object... arguments) {
                                         if (entry.exists()) {
                                             Integer current = entry.getValue();
                                             // キャッシュエントリの値を変更
                                             entry.setValue(entry.getValue() * 2);
                                             // EntryProcessorの戻り値
                                             return current + 1;
                                         } else {
                                             entry.setValue(0);
                                             return -1;
                                         }
                                     }});

             // キャッシュの中の値は2倍に、invokeAllの戻り値は+1に
             for (int i = 1; i <= 10; i++) {
                 assertThat(cache.get("key" + i), is(i * 2));
                 assertThat(result.get("key" + i), is(i + 1));
             }
         }
    }
}