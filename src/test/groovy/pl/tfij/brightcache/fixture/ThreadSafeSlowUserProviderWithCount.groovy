package pl.tfij.brightcache.fixture

import groovy.transform.CompileStatic

import java.util.concurrent.atomic.AtomicInteger

@CompileStatic
class ThreadSafeSlowUserProviderWithCount {
    private AtomicInteger counter = new AtomicInteger(0)
    private final String id;
    private volatile boolean isReady = false

    ThreadSafeSlowUserProviderWithCount(String id) {
        this.id = id
    }

    User getUser() {
        counter.incrementAndGet()
        while (true) {
            if (isReady) {
                break
            }
        }
        return new User(id, "sampleFirstName${UUID.randomUUID().toString()}")
    }

    void markAsReady() {
        isReady = true
    }

    int getCounter() {
        return counter.intValue()
    }
}
