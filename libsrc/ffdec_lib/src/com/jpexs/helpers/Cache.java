/*
 *  Copyright (C) 2010-2015 JPEXS, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.jpexs.helpers;

import com.jpexs.decompiler.flash.helpers.Freed;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 *
 * @author JPEXS
 * @param <K>
 * @param <V>
 */
public class Cache<K, V> implements Freed {

    private Map<K, V> cache;

    private static final List<Cache> instances = new ArrayList<>();

    public static final int STORAGE_FILES = 1;

    public static final int STORAGE_MEMORY = 2;

    private final boolean weak;

    private final String name;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                for (Cache c : instances) {
                    c.clear();
                    c.free();
                }
            }

        });
    }

    public static <K, V> Cache<K, V> getInstance(boolean weak, String name) {
        Cache<K, V> instance = new Cache<>(weak, name);
        instances.add(instance);
        return instance;
    }

    private static int storageType = STORAGE_FILES;

    public static void clearAll() {
        for (Cache c : instances) {
            c.clear();
            c.initCache();
        }
    }

    public static void setStorageType(int storageType) {
        if (storageType == Cache.storageType) {
            return;
        }
        switch (storageType) {
            case STORAGE_FILES:
            case STORAGE_MEMORY:
                break;
            default:
                throw new IllegalArgumentException("storageType must be one of STORAGE_FILES or STORAGE_MEMORY");
        }
        if (storageType != Cache.storageType) {
            clearAll();
        }
        Cache.storageType = storageType;
    }

    public static int getStorageType() {
        return storageType;
    }

    private void initCache() {
        int thisStorageType = storageType;
        Map<K, V> newCache = null;
        if (thisStorageType == STORAGE_FILES) {
            try {
                newCache = new FileHashMap<>(File.createTempFile("ffdec_cache_" + name + "_", ".tmp"));
            } catch (IOException ex) {
                thisStorageType = STORAGE_MEMORY;
            }
        }
        if (thisStorageType == STORAGE_MEMORY) {
            if (weak) {
                newCache = new WeakHashMap<>();
            } else {
                newCache = new HashMap<>();
            }
        }
        if (this.cache instanceof Freed) {
            ((Freed) this.cache).free();
        }
        this.cache = newCache;
    }

    private Cache(boolean weak, String name) {
        this.weak = weak;
        this.name = name;
        initCache();
    }

    public synchronized boolean contains(K key) {
        return cache.containsKey(key);
    }

    public synchronized void clear() {
        cache.clear();
    }

    public synchronized void remove(K key) {
        if (cache.containsKey(key)) {
            cache.remove(key);
        }
    }

    public synchronized V get(K key) {
        return cache.get(key);
    }

    public synchronized void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public boolean isFreeing() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void free() {
        if (cache instanceof Freed) {
            ((Freed) cache).free();
        }
    }
}
