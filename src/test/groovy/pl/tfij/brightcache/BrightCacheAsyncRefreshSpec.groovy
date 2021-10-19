package pl.tfij.brightcache

import com.google.common.cache.Cache
import pl.tfij.brightcache.fixture.TestCacheEventHandler
import pl.tfij.brightcache.fixture.ThreadSafeSlowUserProviderWithCount
import pl.tfij.brightcache.fixture.User
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import static pl.tfij.brightcache.fixture.SampleBrightCacheFactory.sampleL1Cache
import static pl.tfij.brightcache.fixture.SampleBrightCacheFactory.sampleL2Cache

class BrightCacheAsyncRefreshSpec extends Specification {

    def "Should call once async refresh function if many calls try to get value for the key at same time and value in L1 expired 2"() {
        given: "configured BrightCache with a thread pool"
        Cache<String, User> l1Cache = sampleL1Cache()
        Cache<String, User> l2Cache = sampleL2Cache()
        ExecutorService cacheRefreshExecutor = Executors.newFixedThreadPool(10)
        TestCacheEventHandler handler = new TestCacheEventHandler()
        BrightCache<String, User> cache = new BrightCache<>(l1Cache, l2Cache, cacheRefreshExecutor, handler)

        and: "slow user provider provider with counter"
        String firstUserId = "sampleUserId-1"
        ThreadSafeSlowUserProviderWithCount firstUserProvider = new ThreadSafeSlowUserProviderWithCount(firstUserId)

        and: "another slow user provider with counter"
        String secondUserId = "sampleUserId-2"
        ThreadSafeSlowUserProviderWithCount secondUserProvider = new ThreadSafeSlowUserProviderWithCount(secondUserId)

        and: "users are in cache L1 but not in L2"
        cache.get(firstUserId, {new User("initUserId-1", "initUserName-1") } )
        cache.get(secondUserId, {new User("initUserId-2", "initUserName-2") } )
        l1Cache.invalidateAll()

        when: "I get value two times for first user"
        cache.get(firstUserId, { firstUserProvider.getUser() } )
        cache.get(firstUserId, { firstUserProvider.getUser() } )

        and: "I get value once for second user"
        cache.get(secondUserId, { secondUserProvider.getUser() } )

        and: "wait until all async calls occurred"
        new PollingConditions().within(1) { handler.asyncValueSkippedCounter() == 1 }
        new PollingConditions().within(1) { handler.asyncValueLoaderTriggeredCounter() == 2 }

        and: "userProviders finished"
        firstUserProvider.markAsReady()
        secondUserProvider.markAsReady()

        then: "async function was call once for each user"
        new PollingConditions().within(1) { firstUserProvider.counter == 1 }
        new PollingConditions().within(1) { secondUserProvider.counter == 1 }
    }

}
