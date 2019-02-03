package com.toresan.jcachetcd;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.GetResponse;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (Client client = Client.builder().endpoints("http://localhost:2379").build()) {
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
        }

        CachingProvider cachingProvider = Caching.getCachingProvider();

        CacheManager cacheManager = cachingProvider.getCacheManager();

        Cache<String, Integer> cache = cacheManager.getCache("Test");

        cache.put("test_key", 1000);

        System.out.println(cache.get("test_key"));

        cacheManager.close();
    }
}
