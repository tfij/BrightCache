package pl.tfij.brightcache

/**
 * This interface provides a cache event handler. It is designed to use for error logging or collecting cache metrics.
 */
interface CacheEventHandler<K, V> {
    /**
     * The method is called when the value for a given key is stored in the L1 cache. The value will be return from
     * L1 cache.
     */
    fun handleL1Hit(key: K)

    /**
     * The method is called when the value for a given key is not stored in the L1 cache. After invoking it, BrightCache
     * will try to get value from L2 cache.
     */
    fun handleL1Miss(key: K)

    /**
     * The method is called when the value for a given key is stored in the L2 cache. After invoking it, BrightCache
     * will try to asynchronously reload the value into the L2 cache.
     */
    fun handleL2Hit(key: K)

    /**
     * The method is called when the value for a given key is not stored in the L2 cache. After invoking it, BrightCache
     * will try to synchronously load the value into the L2 cache.
     */
    fun handleL2Miss(key: K)

    /**
     * The method is called when an error occurs on the loading value in the async process. The BrightCache can provide
     * value for such a key till the L2 TTL not exceeded.
     */
    fun handleAsyncValueLoaderError(key: K, ex: Exception)

    /**
     * The method is called when value is successfully loaded and stored in the cache.
     */
    fun handleValueLoaderSucceed(key: K, loadedValue: V)

    /**
     * The method is called when an error occurs on the loading value after the retry counter is exceeded.
     */
    fun handleValueLoaderError(key: K, ex: Exception)

    /**
     * The method is called when an exception is thrown on retry. The exception will be caught and BrighrCache repeats
     * the retry until getting a successful result or to retry counter is exceeded.
     */
    fun handleRetryError(tryNumber: Int, ex: Exception)
}

class DummyCacheEventHandler<K, V> : CacheEventHandler<K, V> {
    override fun handleL1Hit(key: K) {}

    override fun handleL1Miss(key: K) {}

    override fun handleL2Hit(key: K) {
        TODO("Not yet implemented")
    }

    override fun handleL2Miss(key: K) {
        TODO("Not yet implemented")
    }

    override fun handleAsyncValueLoaderError(key: K, ex: Exception) {
        TODO("Not yet implemented")
    }

    override fun handleValueLoaderSucceed(key: K, loadedValue: V) {
        TODO("Not yet implemented")
    }

    override fun handleValueLoaderError(key: K, ex: Exception) {
        TODO("Not yet implemented")
    }

    override fun handleRetryError(tryNumber: Int, ex: Exception) {
        TODO("Not yet implemented")
    }

}
