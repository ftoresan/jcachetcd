package com.toresan.jcachetcd;

import com.google.protobuf.ByteString;
import com.ibm.etcd.api.*;
import com.ibm.etcd.client.kv.KvClient;
import com.toresan.jcachetcd.util.TransportConverter;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.toresan.jcachetcd.util.TransportConverter.toByteString;
import static com.toresan.jcachetcd.util.TransportConverter.toObject;

public class JCachetcdCache<K, V> implements Cache<K, V> {

    private KvClient kv;

    JCachetcdCache(KvClient kv) {
        this.kv = kv;
    }

    @Override
    public V get(K key) {
        checkNotNull(key);
        RangeResponse response = kv.get(toByteString(key)).sync();

        try {
            return toObject(response.getKvs(0).getValue());
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public Map<K, V> getAll(Set<? extends K> keys) {
        checkNotNull(keys);
        KvClient.FluentTxnOps<?> batch = kv.batch();
        keys.stream().map(TransportConverter::toByteString).forEach(k -> batch.get(kv.get(k).asRequest()));
        TxnResponse response = batch.sync();
        Map<Object, Object> map = response.getResponsesList().stream().map(r -> r.getResponseRange().getKvs(0))
                .collect(Collectors.toMap(k -> toObject(k.getKey()), k -> toObject(k.getValue())));
        return (Map<K, V>) map;
    }

    @Override
    public boolean containsKey(K key) {
        return kv.get(toByteString(key)).sync().getCount() > 0;
    }

    @Override
    public void loadAll(Set<? extends K> keys, boolean replaceExistingValues, CompletionListener completionListener) {

    }

    @Override
    public void put(K key, V value) {
        checkNotNull(key);
        try {
            kv.put(toByteString(key), toByteString(value)).sync();
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public V getAndPut(K key, V value) {
        ByteString protoKey = toByteString(key);
        RangeRequest getRequest = kv.get(protoKey).asRequest();
        PutRequest putRequest = kv.put(protoKey, toByteString(value)).asRequest();
        try {
            TxnResponse response = kv.batch().get(getRequest).put(putRequest).sync();
            return toObject(response.getResponses(0).getResponseRange().getKvs(0).getValue());
        } catch (Throwable t) {
            throw new CacheException(t);
        }

    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {

    }

    @Override
    public boolean putIfAbsent(K key, V value) {
        checkNotNull(key);
        ByteString protoKey = toByteString(key);
        TxnResponse txnResponse = kv.txnIf().notExists(protoKey).then().put(kv.put(protoKey, toByteString(value)).asRequest()).sync();

        return txnResponse.getResponsesCount() > 0;
    }

    @Override
    public boolean remove(K key) {
        checkNotNull(key);
        DeleteRangeResponse response = kv.delete(toByteString(key)).sync();
        return response.getDeleted() > 0;
    }

    @Override
    public boolean remove(K key, V oldValue) {
        return false;
    }

    @Override
    public V getAndRemove(K key) {
        return null;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return false;
    }

    @Override
    public boolean replace(K key, V value) {
        return false;
    }

    @Override
    public V getAndReplace(K key, V value) {
        return null;
    }

    @Override
    public void removeAll(Set<? extends K> keys) {

    }

    @Override
    public void removeAll() {

    }

    @Override
    public void clear() {

    }

    @Override
    public <C extends Configuration<K, V>> C getConfiguration(Class<C> clazz) {
        return null;
    }

    @Override
    public <T> T invoke(K key, EntryProcessor<K, V, T> entryProcessor, Object... arguments) throws EntryProcessorException {
        return null;
    }

    @Override
    public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> keys, EntryProcessor<K, V, T> entryProcessor, Object... arguments) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public CacheManager getCacheManager() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        return null;
    }

    @Override
    public void registerCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {

    }

    @Override
    public void deregisterCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {

    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return null;
    }
}
