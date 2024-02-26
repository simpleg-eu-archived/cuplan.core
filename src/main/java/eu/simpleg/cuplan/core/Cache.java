package eu.simpleg.cuplan.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class Cache implements AutoCloseable {
    private final ConcurrentHashMap<String, CacheItem<Object>> cache = new ConcurrentHashMap<>();
    private final Timer timer;

    public Cache(long cleanEveryTimeMillis) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<String> keysToBeRemoved = new ArrayList<>();
                for (String key : cache.keySet()) {
                    CacheItem<Object> value = cache.get(key);

                    if (value == null || value.isExpired()) {
                        keysToBeRemoved.add(key);
                    }
                }

                for (String key : keysToBeRemoved) {
                    cache.remove(key);
                }
            }
        }, cleanEveryTimeMillis, cleanEveryTimeMillis);
    }

    @Override
    public void close() throws Exception {
        timer.cancel();
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }

    public void set(String key, Object value, long expirationTimeMillis) {
        long expiration = System.currentTimeMillis() + expirationTimeMillis;
        CacheItem<Object> cacheItem = new CacheItem<>(value, expiration);

        cache.put(key, cacheItem);
    }

    public Option<Object> tryGetValue(String key) {
        CacheItem<Object> cacheItem = cache.get(key);

        if (cacheItem != null && !cacheItem.isExpired()) {
            return Option.some(cacheItem.value());
        }

        if (cacheItem != null) {
            cache.remove(key);
        }

        return Option.none();
    }

    public boolean hasKey(String key) {
        return cache.containsKey(key);
    }

    public void remove(String key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    private record CacheItem<T>(T value, long expiration) {

        public boolean isExpired() {
            return System.currentTimeMillis() > expiration;
        }
    }
}
