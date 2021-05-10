package pl.tfij.brightcache

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import pl.tfij.brightcache.fixture.DummyUserProviderWithCount
import pl.tfij.brightcache.fixture.TestCacheEventHandler
import pl.tfij.brightcache.fixture.User
import spock.lang.Specification

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class BrightCacheSpec extends Specification {

    def "Should create BrightCache with default CacheEventHandler"() {
        given: "sample cache config data"
        Cache<String, User> l1Cache = sampleL1Cache()
        Cache<String, User> l2Cache = sampleL2Cache()
        ExecutorService cacheRefreshExecutor = sampleExecutorService()

        when: "I create instance of BrightCache with no CacheEventHandler"
        new BrightCache(l1Cache, l2Cache, cacheRefreshExecutor)

        then: "no exception was thrown"
        noExceptionThrown()
    }

    def "Should cache value"() {
        given: "configured cache"
        TestCacheEventHandler handler = new TestCacheEventHandler()
        BrightCache<String, User> cache = sampleBrightCache(handler)

        and: "dummy user provider"
        DummyUserProviderWithCount valueLoaderWithCount = new DummyUserProviderWithCount()

        when: "I try to get value from empty cache"
        User user1 = cache.get("sampleKey", { valueLoaderWithCount.load(it) })

        then: "value is returned from valueLoader function"
        valueLoaderWithCount.counter("sampleKey") == 1
        handler.l1MissCounter() == 1
        handler.l1HitCounter() == 0

        when: "I try to get the value again"
        User user2 = cache.get("sampleKey", { valueLoaderWithCount.load(it) })

        then: "value is returned from cache"
        user1 == user2
        valueLoaderWithCount.counter("sampleKey") == 1
        handler.l1MissCounter() == 1
        handler.l1HitCounter() == 1
    }

    private static BrightCache<String, User> sampleBrightCache(TestCacheEventHandler handler) {
        Cache<String, User> l1Cache = sampleL1Cache()
        Cache<String, User> l2Cache = sampleL2Cache()
        ExecutorService cacheRefreshExecutor = sampleExecutorService()
        return new BrightCache(l1Cache, l2Cache, cacheRefreshExecutor, handler)
    }

    private static Cache<String, User> sampleL1Cache() {
        CacheBuilder.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build()
    }

    private static Cache<String, User> sampleL2Cache() {
        CacheBuilder.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(24, TimeUnit.HOURS)
                .build()
    }

    private static ExecutorService sampleExecutorService() {
        Executors.newFixedThreadPool(1)
    }

}
