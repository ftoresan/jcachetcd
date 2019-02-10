package com.toresan.jcachetcd;

import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.api.RangeResponse;
import com.ibm.etcd.client.kv.KvClient;
import org.junit.Test;

import static com.toresan.jcachetcd.util.TransportConverter.toByteString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JCachetcdCacheTest {

    @Test
    public void testGet() {
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
    }
}
