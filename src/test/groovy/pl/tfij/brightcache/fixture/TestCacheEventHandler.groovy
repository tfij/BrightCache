package pl.tfij.brightcache.fixture

import groovy.transform.CompileStatic
import pl.tfij.brightcache.CacheEventHandler

@CompileStatic
class TestCacheEventHandler implements CacheEventHandler {
    private final Map<String, Integer> counters = new HashMap<>()

    @Override
    void onL1HitOccurred(Object key) {
        increaseCounter("l1Hit")
    }

    @Override
    void onL1MissOccurred(Object key) {
        increaseCounter("l1Miss")
    }

    @Override
    void onL2HitOccurred(Object key) {
        increaseCounter("l2Hit")
    }

    @Override
    void onL2MissOccurred(Object key) {
        increaseCounter("l2Hit")
    }

    @Override
    void onAsyncValueLoaderErrorOccurred(Object key, Exception ex) {
        increaseCounter("asyncValueLoaderError")
    }

    @Override
    void onValueLoaderSucceedOccurred(Object key, Object loadedValue) {
        increaseCounter("valueLoaderSucceed")
    }

    @Override
    void onValueLoaderErrorOccurred(Object key, Exception ex) {
        increaseCounter("valueLoaderError")
    }

    @Override
    void onRetryErrorOccurred(int tryNumber, Exception ex) {
        increaseCounter("retryError")
    }

    @Override
    void onAsyncValueSkippedOccurred(Object key) {
        increaseCounter("asyncValueSkip")
    }

    @Override
    void onAsyncValueLoaderTriggeredOccurred(Object key) {
        increaseCounter("asyncValueLoaderTriggered")
    }

    private void increaseCounter(String key) {
        int counter = counters.getOrDefault(key, 0)
        counters[key] = counter+1
    }

    void reset() {
        counters.removeAll { true }
    }

    int l1HitCounter() {
        return counters["l1Hit"] ?: 0
    }

    int l1MissCounter() {
        return counters["l1Miss"] ?: 0
    }

    int l2HitCounter() {
        return counters["l2Hit"] ?: 0
    }

    int l2MissCounter() {
        return counters["l2Miss"] ?: 0
    }

    int asyncValueSkippedCounter() {
        return  counters["asyncValueSkip"] ?: 0
    }

    int asyncValueLoaderTriggeredCounter() {
        return  counters["asyncValueLoaderTriggered"] ?: 0
    }
}
