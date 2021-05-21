package pl.tfij.brightcache

/**
 * This interface provides a cache event handler. It is designed to use for error logging or collecting cache metrics.
 */
interface CacheEventHandler<K, V> {
    /**
     * The method is called when the value for a given key is stored in the L1 cache. The value will be return from
     * L1 cache.
     */
    fun onL1HitOccurred(key: K) {}

    /**
     * The method is called when the value for a given key is not stored in the L1 cache. After invoking it, BrightCache
     * will try to get value from L2 cache.
     */
    fun onL1MissOccurred(key: K) {}

    /**
     * The method is called when the value for a given key is stored in the L2 cache. After invoking it, BrightCache
     * will try to asynchronously reload the value into the L2 cache.
     */
    fun onL2HitOccurred(key: K) {}

    /**
     * The method is called when the value for a given key is not stored in the L2 cache. After invoking it, BrightCache
     * will try to synchronously load the value into the L2 cache.
     */
    fun onL2MissOccurred(key: K) {}

    /**
     * The method is called when an error occurs on the loading value in the async process. The BrightCache can provide
     * value for such a key till the L2 TTL not exceeded.
     */
    fun onAsyncValueLoaderErrorOccurred(key: K, ex: Exception) {}

    /**
     * The method is called when value is successfully loaded and stored in the cache.
     */
    fun onValueLoaderSucceedOccurred(key: K, loadedValue: V) {}

    /**
     * The method is called when an error occurs on the loading value after the retry counter is exceeded.
     */
    fun onValueLoaderErrorOccurred(key: K, ex: Exception) {}

    /**
     * The method is called when an exception is thrown on retry. The exception will be caught and BrighrCache repeats
     * the retry until getting a successful result or to retry counter is exceeded.
     */
    fun onRetryErrorOccurred(tryNumber: Int, ex: Exception) {}

    /**
     * This method is called when async refresh is skipped due to other call already start refresh for the key
     */
    fun onAsyncValueSkippedOccurred(key: K) {}

    /**
     * This method is called when async refresh for the key is triggered
     */
    fun onAsyncValueLoaderTriggeredOccurred(key: K) {}
}

class DummyCacheEventHandler<K, V> : CacheEventHandler<K, V>
