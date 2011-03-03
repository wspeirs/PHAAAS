/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.session;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @class BoundedLRUTimeExpiringCache
 * 
 * This class implments a bounded LRU cache, with the added expiration option
 * of time. Essentially objects are removed from the cache if one of two
 * conditions is true:
 * 1) The max size of the cache is reached. In this case the LRU entry is removed.
 * 2) The max time for an entry is reached. In this case the expired entry is removed. 
 */
public class BoundedLRUTimeExpiringCache<K, V> {

	private static final long serialVersionUID = 5301860528530301150L;
	private Map<K, V> lruCache;
	private Map<Long, K> timeMap;
	private long maxTime = 0;
	private Thread thread = null;
	private ExpirationThread expirationThread = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BoundedLRUTimeExpiringCache<Integer, Integer> cache = new BoundedLRUTimeExpiringCache<Integer, Integer>(10, 100);
		
		// insert some items into our cache
		for(int i=0; i < 20; ++i) {
			cache.put(i, i);
//			try { Thread.sleep(10); }
//			catch (InterruptedException e) { e.printStackTrace(); }
		}
		
		// start the time expiration of the cache
//		cache.start();
		
		// wait for 101ms
//		try { Thread.sleep(100); }
//		catch (InterruptedException e) { e.printStackTrace(); }
		
		// see what's in the cache
		for(int i=0; i < 20; ++i) {
			if(cache.containsKey(i))
				System.out.println("FOUND: " + i);
		}
		
		try { cache.stop(); }
		catch(InterruptedException e) { e.printStackTrace(); }
	}
	
	public BoundedLRUTimeExpiringCache(int maxCapacity, long maxTime) {
		this.lruCache = Collections.synchronizedMap(new BoundedLRUCache<K, V>(maxCapacity));
		this.timeMap = Collections.synchronizedMap(new LinkedHashMap<Long, K>(maxCapacity+1, 1.0f));
		this.maxTime = maxTime;
	}

	/**
	 * @class BoundedLRUCache
	 * A bounded LRU cache implementation used by BoundedLRUTimeExpiringCache.
	 */
	private class BoundedLRUCache<Kc, Vc> extends LinkedHashMap<Kc, Vc> {
		private static final long serialVersionUID = 5301860528530301150L;
		private int maxCapacity;
		
		/**
		 * Construct a LinkedHashMap with accessOrder and an init capacity > max.
		 * @param maxCapacity The maximum number of items ever placed in the cache.
		 */
		public BoundedLRUCache(int maxCapacity) {
			super(maxCapacity + 1, 1.0f, true);
			this.maxCapacity = maxCapacity;
		}
		
		protected boolean removeEldestEntry(Map.Entry<Kc, Vc> eldest) {
			if(this.size() == maxCapacity) {
				Set<Map.Entry<Long, K>> entrySet = timeMap.entrySet();
				
				// go through the timeMap and remove this item as well
				synchronized(timeMap) {
					Iterator<Map.Entry<Long, K>> it = entrySet.iterator();
					
					while(it.hasNext()) {
						Map.Entry<Long, K> e = it.next();
						
						if(e.getValue().equals(eldest.getKey())) {
							timeMap.remove(e.getKey());
							break;
						}
					}
				}
				
				return true;
			} else {
				return false;
			}
		}
	}

	private class ExpirationThread implements Runnable {
		
		private boolean isRunning = true;

		public void run() {
			// this thread simply runs forever
			while(isRunning) {
				try {
					if(timeMap.size() == 0) {
							Thread.sleep(100);	// sleep until there is something in the map
							continue;
					}
					
					Set<Map.Entry<Long, K>> entrySet = timeMap.entrySet();
					Long sleepTime = 0L;
					
					synchronized(timeMap) {
						Iterator<Map.Entry<Long, K>> it = entrySet.iterator();
						Long curTime = System.currentTimeMillis();
						
						while(it.hasNext()) {
							Map.Entry<Long, K> entry = it.next();
							
							System.out.println("ENTRY: " + entry.getValue() + " :" + (curTime - entry.getKey()));
							
							if(curTime - entry.getKey() > maxTime) {
								System.out.println("REMOVING");
								it.remove();		// remove the item from the time map
								lruCache.remove(entry.getValue());	// remove it from the LRU cache as well
							} else {
								sleepTime = maxTime - (curTime - entry.getKey());
								break;	// the list is ordered, so we're done here
							}
						}
						
						System.out.println("SLEEPING: " + sleepTime);
						Thread.sleep(sleepTime);	// sleep until the next one is ready to be removed
					}
				} catch (InterruptedException e) {
					stop();	// stop this thread
				}
			}
		}
		
		public void start() {
			isRunning = true;
		}
		
		public void stop() {
			isRunning = false;
		}
	}
	
	/**
	 * Starts the internal thread to begin collecting cache items.
	 */
	public void start() {
		// if we don't have a thread yet, create one and start it
		if(expirationThread == null) {
			expirationThread = new ExpirationThread();
			thread = new Thread(expirationThread, "Expiration Thread");
			thread.start();
		} else { // we have a thread, so start it again
			expirationThread.start();
			if(!thread.isAlive())
				thread.start();
		}
	}
	
	/**
	 * Stop the internal thread used to collect cache items.
	 * @throws InterruptedException
	 */
	public void stop() throws InterruptedException {
		// no thread, so return immediately
		if(expirationThread == null)
			return;
		
		expirationThread.stop();	// stop the thread
		thread.interrupt();			// interrupt the thread in case it's waiting
		thread.join();				// wait for it to finish
	}
	
	public void clear() {
		timeMap.clear();	// clear out the time map
		lruCache.clear();	// clear out the LRU cache
	}

	public boolean containsKey(Object key) {
		return lruCache.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return lruCache.containsValue(value);
	}

	public V get(Object key) {
		return lruCache.get(key);
	}

	public boolean isEmpty() {
		return lruCache.isEmpty();
	}

	public V put(K key, V value) {
		timeMap.put(System.currentTimeMillis(), key);	// put the item in the timeMap
		return lruCache.put(key, value);	// put it in the lruCache
	}

	public int size() {
		return lruCache.size();
	}
}
