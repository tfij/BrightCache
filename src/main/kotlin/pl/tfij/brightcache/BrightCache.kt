package pl.tfij.brightcache

import com.google.common.cache.Cache
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService

class BrightCache<K: Any, V>(
    private val l1Cache: Cache<K, V>,
    private val l2Cache: Cache<K, V>,
    private val executor: ExecutorService,
    private val cacheEventHandler: CacheEventHandler<K, V>) {

    private val keysWithCalculatingValues = ConcurrentHashMap.newKeySet<K>()

    constructor(
        l1Cache: Cache<K, V>,
        l2Cache: Cache<K, V>,
        executor: ExecutorService) : this(l1Cache, l2Cache, executor, DummyCacheEventHandler())

    fun get(key: K, valueLoader: (K) -> V): V {
        val value = l1Cache.getIfPresent(key)
        return if (value != null) {
            cacheEventHandler.onL1HitOccurred(key)
            value
        } else {
            cacheEventHandler.onL1MissOccurred(key)
            val valueL2 = l2Cache.getIfPresent(key)
            return if (valueL2 != null) {
                cacheEventHandler.onL2HitOccurred(key)
                refreshValueAsync(key, valueLoader)
                valueL2
            } else {
                cacheEventHandler.onL2MissOccurred(key)
                loadValue(key, valueLoader)
            }
        }
    }

    private fun refreshValueAsync(key: K, valueLoader: (K) -> V) {
        try {
            val isAdded = keysWithCalculatingValues.add(key)
            if (isAdded) {
                cacheEventHandler.onAsyncValueLoaderTriggeredOccurred(key)
                CompletableFuture.supplyAsync({ loadValue(key, valueLoader) }, executor)
                    .whenComplete { _, _ -> keysWithCalculatingValues.remove(key) }
            } else {
                cacheEventHandler.onAsyncValueSkippedOccurred(key)
            }
        } catch (ex: Exception) {
            keysWithCalculatingValues.remove(key)
            cacheEventHandler.onAsyncValueLoaderErrorOccurred(key, ex)
        }
    }

    private fun loadValue(key: K, valueLoader: (K) -> V): V {
        try {
            return retry(2) {
                val newValue = valueLoader(key)
                l1Cache.put(key, newValue)
                l2Cache.put(key, newValue)
                cacheEventHandler.onValueLoaderSucceedOccurred(key, newValue)
                newValue
            }
        } catch (ex: Exception) {
            cacheEventHandler.onValueLoaderErrorOccurred(key, ex)
            throw ex
        }
    }

    private fun <T> retry(times: Int, action: () -> T): T {
        (1..times).forEach { i ->
            try {
                return action()
            } catch (ex: Exception) {
                cacheEventHandler.onRetryErrorOccurred(i, ex)
            }
        }
        throw Exception("Retry counter is exceeded.")
    }
}
