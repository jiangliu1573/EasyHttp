package com.cosmos.easyhttp.cache;

/**
 * Created by Mark.
 *
 * Des: 网络请求缓存接口.
 */
public interface Cache<K, V> {

    V get(K key);

    void put(K key, V value);

    void remove(K key);
}
