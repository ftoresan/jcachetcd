package com.toresan.jcachetcd;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        CachingProvider cachingProvider = Caching.getCachingProvider();

        CacheManager cacheManager = cachingProvider.getCacheManager();
        try {

            Cache<String, Integer> cache = cacheManager.getCache("Test");

            cache.put("test_key", 1000);

            System.out.println(cache.get("test_key"));

            Integer oldValue = cache.getAndPut("test_key", 2000);

            System.out.println("Contains test_key: " + cache.containsKey("test_key"));
            System.out.println("Contains invalid_key: " + cache.containsKey("invalid_key"));

            System.out.println("Old : " + oldValue);

            System.out.println("Remove new: " + cache.remove("new"));
            System.out.println(cache.putIfAbsent("new", 10));
            System.out.println(cache.putIfAbsent("new", 20));

            cache.getAll(Set.of("test_key", "new")).forEach((k, v) -> System.out.println("Key : " + k + " Value: " + v));

            System.out.println("Current: " + cache.get("test_key"));

            System.out.println("Remove if 1000: " + cache.remove("test_key", 1000));

            System.out.println("Current: " + cache.get("test_key"));

            System.out.println("Remove if 2000: " + cache.remove("test_key", 2000));

            System.out.println("Current: " + cache.get("test_key"));

            cache.put("other_key", 42);

            System.out.println("Get and remove: " + cache.getAndRemove("other_key"));

            System.out.println("Other now: " + cache.get("other_key"));
        } finally {
            cacheManager.close();
        }
    }
}
