package com.toresan.jcachetcd.spi;

import com.toresan.jcachetcd.JCachetcdCacheManager;
import io.etcd.jetcd.Client;

import javax.cache.CacheManager;
import javax.cache.configuration.OptionalFeature;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentMap;

public class JCachetcdCachingProvider implements CachingProvider {

    private final Map<ClassLoader, ConcurrentMap<URI, JCachetcdCacheManager>> cacheManagers = new WeakHashMap<>();

    @Override
    public CacheManager getCacheManager(URI uri, ClassLoader classLoader, Properties properties) {
        uri = uri == null ? getDefaultURI() : uri;
        classLoader = classLoader == null ? getDefaultClassLoader() : classLoader;
        properties = properties == null ? getDefaultProperties() : properties;

        return createCacheManager();
    }

    @Override
    public ClassLoader getDefaultClassLoader() {
        return null;
    }

    @Override
    public URI getDefaultURI() {
        return null;
    }

    @Override
    public Properties getDefaultProperties() {
        return null;
    }

    @Override
    public CacheManager getCacheManager(URI uri, ClassLoader classLoader) {
        return getCacheManager(uri, classLoader, getDefaultProperties());
    }

    @Override
    public CacheManager getCacheManager() {
        return getCacheManager(getDefaultURI(), getDefaultClassLoader());
    }

    @Override
    public void close() {

    }

    @Override
    public void close(ClassLoader classLoader) {

    }

    @Override
    public void close(URI uri, ClassLoader classLoader) {

    }

    @Override
    public boolean isSupported(OptionalFeature optionalFeature) {
        return false;
    }

    private JCachetcdCacheManager createCacheManager() {
        Client client = Client.builder().endpoints("http://localhost:2379").build();

        return new JCachetcdCacheManager(client);
    }
}
