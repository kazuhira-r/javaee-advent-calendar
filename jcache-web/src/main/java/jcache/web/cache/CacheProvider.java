package jcache.web.cache;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;

public class CacheProvider {
    @Produces
    @ApplicationScoped
    public Cache<String, String> getCache() {
        return Caching.getCachingProvider().getCacheManager().getCache("simpleCache");
    }

    public void disposeCache(@Disposes Cache<String, String> cache) {
        cache.close();
    }
}
