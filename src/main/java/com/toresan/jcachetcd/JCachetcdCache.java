package com.toresan.jcachetcd;

import com.google.protobuf.ByteString;
import com.ibm.etcd.api.PutRequest;
import com.ibm.etcd.api.RangeRequest;
import com.ibm.etcd.api.RangeResponse;
import com.ibm.etcd.api.TxnResponse;
import com.ibm.etcd.client.kv.KvClient;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class JCachetcdCache<K, V> implements Cache<K, V> {

    private KvClient kv;

    JCachetcdCache(KvClient kv) {
        this.kv = kv;
    }

    @Override
    public V get(K key) {
        RangeResponse response = kv.get(ByteString.copyFrom(toByteArray(key))).sync();

        try {
            return (V) toObject(response.getKvs(0).getValue().toByteArray());
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public Map<K, V> getAll(Set<? extends K> keys) {
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return false;
    }

    @Override
    public void loadAll(Set<? extends K> keys, boolean replaceExistingValues, CompletionListener completionListener) {

    }

    @Override
    public void put(K key, V value) {
        try {
            kv.put(ByteString.copyFrom(toByteArray(key)), ByteString.copyFrom(toByteArray(value))).sync();
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public V getAndPut(K key, V value) {
        ByteString protoKey = ByteString.copyFrom(toByteArray(key));
        RangeRequest getRequest = kv.get(protoKey).asRequest();
        PutRequest putRequest = kv.put(protoKey, ByteString.copyFrom(toByteArray(value))).asRequest();
        try {
            TxnResponse response = kv.batch().get(getRequest).put(putRequest).sync();
            return (V) toObject(response.getResponses(0).getResponseRange().getKvs(0).getValue().toByteArray());
        } catch (Throwable t) {
            throw new CacheException(t);
        }

    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {

    }

    @Override
    public boolean putIfAbsent(K key, V value) {
        return false;
    }

    @Override
    public boolean remove(K key) {
        return false;
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

    private byte[] toByteArray(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object toObject(byte[] bytes) {
        ObjectInput in = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
            in = new ObjectInputStream(bis);
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
