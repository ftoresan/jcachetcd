package com.toresan.jcachetcd;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /*try (Client client = Client.builder().endpoints("http://localhost:2379").build()) {
            KV kvClient = client.getKVClient();

            ByteSequence key = ByteSequence.from("test_key", Charset.forName("utf-8"));
            ByteSequence value = ByteSequence.from("test_value", Charset.forName("utf-8"));

            // put the key-value
            kvClient.put(key, value).get();

            // get the CompletableFuture
            CompletableFuture<GetResponse> getFuture = kvClient.get(key);

            // get the value from CompletableFuture
            GetResponse response = getFuture.get();
            response.getKvs().stream().map(k -> k.getValue().toString(Charset.forName("utf-8"))).forEach(System.out::println);

            // delete the key
            kvClient.delete(key).get();
        }*/

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
        } finally {
            cacheManager.close();
        }
    }
}
