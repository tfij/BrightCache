# BrightCache

BrightCache use two layers od cache - L1 and L2.

## Why BrightCache

The main features of the BrithtCache are:

1. **Improved response times** - in regular cache, if the value expires, a blocking call is made to retrieve the value
   and the client has to wait for the result. In BrightCache, the value is returned immediately from L2 and value
   refreshed in a separate thread.
   
1. **Failure immunity extension** - If everything works, L1 returns relatively fresh data. However, L2 can store data 
   for the long term, which makes the service more resistant to failures.

## Limitations

BrightCache is applicable in situations where all items fit in cache. Otherwise, BrightCache becomes a regular cache.
L2 TTL should be greater than L1 TTL and the size of L2 should be greater then or equal to size of L1.
Under the hood, BrightCache uses a regular guava cache.

## BrightCache operation

1. when a request comes in and the response is not in the BrightCache (both L1 and L2) then the valueLoader function 
   is performed and the result is returned to the client and stored in L1 and L2
   
1. when a request comes in and the response is not in the L1 but is stored in L2 then
    1. the client receives a response from L2 and
    1. in a separate thread, valueLoader function is executed and its result is stored to L1 and L2

1. when a request comes in and the response is in L1 then the client receives a response from L1

## How to use it

Create BrightCache instance

```java
Cache<String, User> l1Cache = CacheBuilder.newBuilder()
   .maximumSize(properties.cacheSize)
   .expireAfterWrite(30, TimeUnit.MINUTES)
   .build();

Cache<String, User> l2Cache = CacheBuilder.newBuilder()
   .maximumSize(properties.cacheSize)
   .expireAfterWrite(24, TimeUnit.HOURS)
   .build<String, User>();

BrightCache brightCache = return BrightCache(l1Cache, l2Cache, cacheRefreshExecutor)
```

Simply get value from cache and provide fallback lambda

```java
brightCache.get("Donald", () -> loadUserFromDb("Donald"))
```

## Cache metrics and error logging

BrightCache provide `CacheEventHandler` interface to support collecting cache metrics and logging errors.
You can provide your own implementation of the interface and pass it on by creating the BrighrCache instance.
