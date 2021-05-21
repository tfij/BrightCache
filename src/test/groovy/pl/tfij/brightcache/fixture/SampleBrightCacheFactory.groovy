package pl.tfij.brightcache.fixture

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import groovy.transform.CompileStatic

import java.util.concurrent.TimeUnit

@CompileStatic
class SampleBrightCacheFactory {
    static Cache<String, User> sampleL1Cache() {
        return CacheBuilder.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build()
    }

    static Cache<String, User> sampleL2Cache() {
        return CacheBuilder.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(24, TimeUnit.HOURS)
                .build()
    }
}
