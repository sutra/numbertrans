/*
 * Copyright (c) 2006, Jonathan Clark <jon_DOT_h_DOT_clark_AT_gmail_DOT_com> 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of my affiliates nor the names of thier contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIEDWARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package info.jonclark.cache;

import info.jonclark.util.StringUtils;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Caches objects based on a key.
 * <p>
 * Note: It is recommended that a numerical value be used as the Key since these
 * will provide much more efficient keys. Classes such as String have a
 * relatively slower hashing algorithm.
 * 
 * @author Jonathan Clark
 */
public class KeyCache<K, V> {
	private int nHits = 0;
	private int nMisses = 0;
	private int nQueries = 0;

	private final int nMinHistory;
	private final int nMaxHistory;

	private int nHistoryIterator = 0;
	private final K[] recentKeys;
	private final V[] recentValues;

	private final HashMap<K, SoftReference<V>> hash;
	private final CacheGenerator<K, V> generator;

	private final Logger log;
	private final String cacheName;

	private static final int DEFAULT_MIN_HISTORY = 100;
	private static final int DEFAULT_MAX_HISTORY = 1024;
	private static final int DEFAULT_LOG_FREQUENCY = 100;
	private static final String DEFAULT_CACHE_NAME = "KEY_CACHE";

	public KeyCache(CacheGenerator<K, V> generator, final int nMinHistory, final int nMaxHistory,
			String cacheName, Logger log) {
		assert nMinHistory < nMaxHistory : "nMinHistory < nMaxHistory";
		assert generator != null;
		assert log != null;

		this.nMinHistory = nMinHistory;
		this.nMaxHistory = nMaxHistory;
		this.generator = generator;
		this.log = log;
		this.cacheName = cacheName;

		recentKeys = createKeyArray(nMinHistory);
		recentValues = createValueArray(nMinHistory);

		hash = new HashMap<K, SoftReference<V>>(nMaxHistory);
	}

	public KeyCache(CacheGenerator<K, V> generator, Logger log) {
		this(generator, DEFAULT_MIN_HISTORY, DEFAULT_MAX_HISTORY, DEFAULT_CACHE_NAME, log);
	}

	/**
	 * Get a value for a given key. First, the cache will be searched for the
	 * key. If it is not cached, the CacheGenerator will be called to generate
	 * the value.
	 * 
	 * @param key
	 *            The key for which we wish to get a value.
	 * @return The value corresponding to the key.
	 * @throws Exception
	 *             If an error is encountered while generating a new cache
	 *             value.
	 */
	public V getValue(final K key) throws Exception {
		nQueries++;
		if (nQueries % DEFAULT_LOG_FREQUENCY == 0) {
			log.info(cacheName + ": " + getStats());
		}

		SoftReference<V> ref = hash.get(key);
		if (ref != null && ref.get() != null) {
			nHits++;
			return ref.get();
		} else {
			nMisses++;

			// Flush cache if overflowing
			if (hash.size() >= nMaxHistory)
				flushStaleCache();

			final V value = generator.getUncachedValue(key);
			synchronized (hash) {
				hash.put(key, new SoftReference<V>(value));

				if (nMinHistory > 0) {
					recentKeys[nHistoryIterator] = key;
					recentValues[nHistoryIterator] = value;
					nHistoryIterator++;
					nHistoryIterator %= nMinHistory;
				}
			}

			return value;
		}
	}

	/**
	 * Get a string with statistics about the performance of this KeyCache.
	 */
	public String getStats() {
		return nQueries + " queries, " + nHits + " hits, " + nMisses + " misses, "
				+ StringUtils.getPercentage(nHits, nQueries) + " % hits";
	}

	/**
	 * A quick hack to get around limitations in Java generics. Contains warning
	 * suppression to one line.
	 */
	@SuppressWarnings("unchecked")
	private K[] createKeyArray(final int nSize) {
		return (K[]) new Object[nSize];
	}

	/**
	 * A quick hack to get around limitations in Java generics. Contains warning
	 * suppression to one line.
	 */
	@SuppressWarnings("unchecked")
	private V[] createValueArray(final int nSize) {
		return (V[]) new Object[nSize];
	}

	/**
	 * Clear the current contents of the cache and repopulate with recent
	 * history.
	 */
	private void flushStaleCache() {
		synchronized (hash) {
			log.info(cacheName + ": Flushing cache; " + getStats());

			// Clear all entries, then replace recent ones
			hash.clear();
			for (int i = 0; i < nMinHistory; i++)
				if (recentKeys[i] != null)
					hash.put(recentKeys[i], new SoftReference<V>(recentValues[i]));
		}
	} // end flushStaleCache()
}
