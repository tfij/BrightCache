package pl.tfij.brightcache

import com.google.common.cache.Cache
import java.util.concurrent.ExecutorService

class BrightCache<K: Any, V>(
    private val l1Cache: Cache<K, V>,
    private val l2Cache: Cache<K, V>,
    private val executor: ExecutorService,
    private val cacheEventHandler: CacheEventHandler<K, V>) {

    constructor(
        l1Cache: Cache<K, V>,
        l2Cache: Cache<K, V>,
        executor: ExecutorService) : this(l1Cache, l2Cache, executor, DummyCacheEventHandler())

    fun get(key: K, valueLoader: (K) -> V): V {
        val value = l1Cache.getIfPresent(key)
        return if (value != null) {
            cacheEventHandler.handleL1Hit(key)
            value
        } else {
            cacheEventHandler.handleL1Miss(key)
            val valueL2 = l2Cache.getIfPresent(key)
            return if (valueL2 != null) {
                cacheEventHandler.handleL2Hit(key)
                refreshValueAsync(key, valueLoader)
                valueL2
            } else {
                cacheEventHandler.handleL2Miss(key)
                loadValue(key, valueLoader)
            }
        }
    }

    private fun refreshValueAsync(key: K, valueLoader: (K) -> V) {
        try {
            executor.submit { loadValue(key, valueLoader) }
        } catch (ex: Exception) {
            cacheEventHandler.handleAsyncValueLoaderError(key, ex)
        }
    }

    private fun loadValue(key: K, valueLoader: (K) -> V): V {
        try {
            return retry(2) {
                val newValue = valueLoader(key)
                l1Cache.put(key, newValue)
                l2Cache.put(key, newValue)
                cacheEventHandler.handleValueLoaderSucceed(key, newValue)
                newValue
            }
        } catch (ex: Exception) {
            cacheEventHandler.handleValueLoaderError(key, ex)
            throw ex
        }
    }

    private fun <T> retry(times: Int, action: () -> T): T {
        (1..times).forEach { i ->
            try {
                return action()
            } catch (ex: Exception) {
                cacheEventHandler.handleRetryError(i, ex)
            }
        }
        throw Exception("Retry counter is exceeded.")
    }
}
