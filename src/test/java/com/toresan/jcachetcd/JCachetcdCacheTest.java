package com.toresan.jcachetcd;

import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.api.PutResponse;
import com.ibm.etcd.api.RangeResponse;
import com.ibm.etcd.client.kv.KvClient;
import org.junit.Test;

import java.io.Serializable;
import java.time.LocalDate;

import static com.toresan.jcachetcd.util.TransportConverter.toByteString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class JCachetcdCacheTest {

    @Test
    public void testGet() {
        KvClient client = mock(KvClient.class);
        setupRangeResponse(client, "test_key", "test_value");

        JCachetcdCache<String, String> cache = new JCachetcdCache<>(client);
        String value = cache.get("test_key");

        assertEquals("test_value", value);
    }

    @Test
    public void testGetPojo() {
        KvClient client = mock(KvClient.class);
        setupRangeResponse(client, 200L, new MyDTO("Homer Simpson", 62, LocalDate.of(1956, 5, 11)));

        JCachetcdCache<Long, MyDTO> cache = new JCachetcdCache<>(client);
        MyDTO value = cache.get(200L);

        assertEquals("Homer Simpson", value.getName());
        assertEquals(62, (int) value.getAge());
        assertEquals(LocalDate.of(1956, 5, 11), value.getBirthDate());
    }

    @Test(expected = NullPointerException.class)
    public void testGetNullKey() {
        KvClient client = mock(KvClient.class);
        setupRangeResponse(client, "test_key", "test_value");

        JCachetcdCache<String, String> cache = new JCachetcdCache<>(client);
        cache.get(null);
    }

    @Test
    public void testGetNoValue() {
        KvClient client = mock(KvClient.class);
        setupEmptyResponse(client, "test_key");

        JCachetcdCache<String, String> cache = new JCachetcdCache<>(client);
        String value = cache.get("test_key");

        assertNull(value);
    }

    @Test
    public void testPut() throws InterruptedException {
        KvClient client = mock(KvClient.class);
        KvClient.FluentPutRequest req = mock(KvClient.FluentPutRequest.class);
        PutResponse resp = mock(PutResponse.class);

        when(client.put(any(), any())).thenReturn(req);
        when(req.sync()).thenReturn(null);

        JCachetcdCache<String, Integer> cache = new JCachetcdCache<>(client);
        cache.put("some_key", 42);

        verify(client).put(toByteString("some_key"), toByteString(42));
        verify(req).sync();
    }

    @Test(expected = NullPointerException.class)
    public void testPutNullKey() {
        KvClient client = mock(KvClient.class);

        JCachetcdCache<String, Integer> cache = new JCachetcdCache<>(client);
        cache.put(null, 42);
    }

    @Test
    public void testGetAll() {
        KvClient client = mock(KvClient.class);
        setupRangeResponse(client, "test_key", "test_value");

        JCachetcdCache<String, String> cache = new JCachetcdCache<>(client);
        String value = cache.get("test_key");

        assertEquals("test_value", value);
    }

    private void setupRangeResponse(KvClient client, Object key, Object value) {
        KvClient.FluentRangeRequest req = mock(KvClient.FluentRangeRequest.class);
        RangeResponse resp = mock(RangeResponse.class);
        KeyValue kv = mock(KeyValue.class);

        when(client.get(toByteString(key))).thenReturn(req);
        when(req.sync()).thenReturn(resp);
        when(resp.getKvs(0)).thenReturn(kv);
        when(kv.getValue()).thenReturn(toByteString(value));
        when(resp.getKvsCount()).thenReturn(1);
    }

    private void setupEmptyResponse(KvClient client, Object key) {
        KvClient.FluentRangeRequest req = mock(KvClient.FluentRangeRequest.class);
        RangeResponse resp = mock(RangeResponse.class);

        when(client.get(toByteString(key))).thenReturn(req);
        when(req.sync()).thenReturn(resp);
        when(resp.getKvs(0)).thenThrow(new IndexOutOfBoundsException(0));
        when(resp.getKvsCount()).thenReturn(0);
    }

    private static class MyDTO implements Serializable {
        private String name;
        private Integer age;
        private LocalDate birthDate;

        public MyDTO(String name, Integer age, LocalDate birthDate) {
            this.name = name;
            this.age = age;
            this.birthDate = birthDate;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public LocalDate getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
        }
    }
}
