package com.hippout.hippoutlocalizationlib.locale;

import javax.annotation.*;
import java.util.*;

/**
 * A Map that will always have no elements.
 *
 * @author Wyatt Kalmer
 * @api.Note FOR INTERNAL USE ONLY.
 * @since 1.0.0
 */
class NullMap<K, V> implements Map<K, V> {

    /**
     * Constructs a NullMap, a Map that will always have no elements.
     */
    NullMap()
    {
        // Nothing
    }

    /**
     * Returns 0.
     *
     * @return 0.
     */
    @Override
    public int size()
    {
        return 0;
    }

    /**
     * Returns true.
     *
     * @return true.
     */
    @Override
    public boolean isEmpty()
    {
        return true;
    }

    /**
     * Returns false.
     *
     * @param key unused.
     * @return false.
     */
    @Override
    public boolean containsKey(Object key)
    {
        return false;
    }

    /**
     * Returns false.
     *
     * @param value unused.
     * @return false.
     */
    @Override
    public boolean containsValue(Object value)
    {
        return false;
    }

    /**
     * Returns null.
     *
     * @return null.
     */
    @Override
    public V get(Object key)
    {
        return null;
    }

    /**
     * Returns null.
     *
     * @return null.
     */
    @Override
    public V put(Object key, Object value)
    {
        return null;
    }

    /**
     * Returns null.
     *
     * @return null.
     */
    @Override
    public V remove(Object key)
    {
        return null;
    }

    /**
     * Does nothing.
     *
     * @param m unused.
     */
    @Override
    public void putAll(@Nonnull Map<? extends K, ? extends V> m)
    {
    }

    /**
     * Does nothing.
     */
    @Override
    public void clear()
    {
    }

    /**
     * Returns an empty Set.
     *
     * @return an empty Set.
     */
    @Override
    @Nonnull
    public Set<K> keySet()
    {
        return Collections.emptySet();
    }


    /**
     * Returns an empty Collection.
     *
     * @return an empty Collection.
     */
    @Override
    @Nonnull
    public Collection<V> values()
    {
        return Collections.emptyList();
    }

    /**
     * Returns an empty Set.
     *
     * @return an empty Set.
     */
    @Override
    @Nonnull
    public Set<Entry<K, V>> entrySet()
    {
        return Collections.emptySet();
    }
}
