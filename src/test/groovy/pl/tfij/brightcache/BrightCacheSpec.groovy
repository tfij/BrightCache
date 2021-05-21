package pl.tfij.brightcache

import com.google.common.cache.Cache
import pl.tfij.brightcache.fixture.DummyUserProviderWithCount
import pl.tfij.brightcache.fixture.SelfThreadExecutorService
import pl.tfij.brightcache.fixture.TestCacheEventHandler
import pl.tfij.brightcache.fixture.User
import spock.lang.Specification

import java.util.concurrent.ExecutorService

import static pl.tfij.brightcache.fixture.SampleBrightCacheFactory.sampleL1Cache
import static pl.tfij.brightcache.fixture.SampleBrightCacheFactory.sampleL2Cache

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
        User user1 = cache.get("sampleKey", { valueLoaderWithCount.randomUser(it) })

        then: "value is returned from valueLoader function"
        valueLoaderWithCount.counter("sampleKey") == 1
        handler.l1MissCounter() == 1
        handler.l1HitCounter() == 0

        when: "I try to get the value again"
        User user2 = cache.get("sampleKey", { valueLoaderWithCount.randomUser(it) })

        then: "value is returned from cache"
        user1 == user2
        valueLoaderWithCount.counter("sampleKey") == 1
        handler.l1MissCounter() == 1
        handler.l1HitCounter() == 1
    }

    def "Should return value from L2 if L1 expired"() {
        given: "configured cache"
        Cache<String, User> l1Cache = sampleL1Cache()
        Cache<String, User> l2Cache = sampleL2Cache()
        ExecutorService cacheRefreshExecutor = sampleExecutorService()
        TestCacheEventHandler handler = new TestCacheEventHandler()
        BrightCache<String, User> cache = new BrightCache<>(l1Cache, l2Cache, cacheRefreshExecutor, handler)

        and: "dummy user provider"
        DummyUserProviderWithCount valueLoaderWithCount = new DummyUserProviderWithCount()

        and: "a user is cached both in L1 and L2"
        User user1 = cache.get("sampleKey", { valueLoaderWithCount.randomUser(it) })
        handler.reset()
        valueLoaderWithCount.reset()

        when: "L1 expired"
        l1Cache.invalidateAll()

        and: "I try to get value from the cache"
        User user2 = cache.get("sampleKey", { valueLoaderWithCount.randomUser(it) })

        then: "value is returned from L2 cache"
        user1 == user2
        handler.l1MissCounter() == 1
        handler.l1HitCounter() == 0
        handler.l2MissCounter() == 0
        handler.l2HitCounter() == 1

        and: "value loader function was invoked (async value refresh)"
        valueLoaderWithCount.counter("sampleKey") == 1

        when: "I again try to get value from the cache"
        User user3 = cache.get("sampleKey", { valueLoaderWithCount.randomUser(it) })

        then: "returned value is refreshed"
        user1 != user3

        and: "value is returned from L1 cache"
        handler.l1MissCounter() == 1
        handler.l1HitCounter() == 1
        handler.l2MissCounter() == 0
        handler.l2HitCounter() == 1
    }

    private static BrightCache<String, User> sampleBrightCache(TestCacheEventHandler handler) {
        Cache<String, User> l1Cache = sampleL1Cache()
        Cache<String, User> l2Cache = sampleL2Cache()
        ExecutorService cacheRefreshExecutor = sampleExecutorService()
        return new BrightCache<>(l1Cache, l2Cache, cacheRefreshExecutor, handler)
    }

    private static ExecutorService sampleExecutorService() {
        return new SelfThreadExecutorService()
    }

}
