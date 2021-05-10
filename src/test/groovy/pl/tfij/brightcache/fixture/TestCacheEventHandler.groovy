package pl.tfij.brightcache.fixture

import groovy.transform.CompileStatic
import org.jetbrains.annotations.NotNull
import pl.tfij.brightcache.CacheEventHandler

@CompileStatic
class TestCacheEventHandler implements CacheEventHandler {
    private final Map<String, Integer> counters = new HashMap<>()

    @Override
    void handleL1Hit(Object key) {
        increaseCounter("l1Hit")
    }

    @Override
    void handleL1Miss(Object key) {
        increaseCounter("l1Miss")
    }

    @Override
    void handleL2Hit(Object key) {
        increaseCounter("l2Hit")
    }

    @Override
    void handleL2Miss(Object key) {
        increaseCounter("l2Hit")
    }

    @Override
    void handleAsyncValueLoaderError(Object key, @NotNull Exception ex) {
        increaseCounter("asyncValueLoaderError")
    }

    @Override
    void handleValueLoaderSucceed(Object key, Object loadedValue) {
        increaseCounter("valueLoaderSucceed")
    }

    @Override
    void handleValueLoaderError(Object key, @NotNull Exception ex) {
        increaseCounter("valueLoaderError")
    }

    @Override
    void handleRetryError(int tryNumber, @NotNull Exception ex) {
        increaseCounter("retryError")
    }

    private void increaseCounter(String key) {
        int counter = counters.getOrDefault(key, 0)
        counters[key] = counter+1
    }

    int l1HitCounter() {
        return counters["l1Hit"] ?: 0
    }

    int l1MissCounter() {
        return counters["l1Miss"] ?: 0
    }
}
