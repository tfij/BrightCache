package pl.tfij.brightcache

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
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
