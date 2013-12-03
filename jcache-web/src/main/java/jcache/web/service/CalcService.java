package jcache.web.service;

import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CachePut;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;
import javax.cache.annotation.CacheValue;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@CacheDefaults(cacheName = "calcCache")
public class CalcService {
    @CacheResult
    public int add(int left, int right) {
        try { Thread.sleep(3 * 1000L); } catch (InterruptedException e) { }
        return left + right;
    }

    @CachePut
    public void update(int left, int right, @CacheValue int result) {
    }

    @CacheRemove
    public void remove(int left, int right) {
    }

    @CacheRemoveAll
    public void removeAll() {
    }

    /*  // こういうの作ると、addとキーが被る…
    @CacheResult
    public int multiply(int left, int right) {
        try { Thread.sleep(3 * 1000L); } catch (InterruptedException e) { }
        return left * right;
    }
    */
}
