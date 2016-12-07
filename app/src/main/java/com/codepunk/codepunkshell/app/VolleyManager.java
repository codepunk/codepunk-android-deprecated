/*
 * Copyright 2016 Codepunk, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codepunk.codepunkshell.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;

import java.net.HttpURLConnection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Class that manages all Volley api calls.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class VolleyManager {

  /**
   * For singleton creation.
   */
  private static final Object sLock = new Object();

  /**
   * The singleton instance.
   */
  @SuppressWarnings("StaticFieldLeak")
  private static VolleyManager sInstance;

  /**
   * The application context associated with this VolleyManager.
   */
  private final Context mAppContext;

  /**
   * The {@link RequestQueue} associated with this VolleyManager.
   */
  private RequestQueue mRequestQueue;

  /**
   * The {@link ImageLoader} associated with this VolleyManager.
   */
  private ImageLoader mImageLoader;

  /**
   * Queue of {@link OnRequestQueueReadyListener} that are awaiting the contruction of
   * a {@link RequestQueue}.
   */
  private final ConcurrentLinkedQueue<OnRequestQueueReadyListener>
      mPendingOnRequestQueueReadyListeners;

  /**
   * Constructor that accepts a {@link Context}.
   * @param context The context to use to get the application context to associate with this
   *                VolleyManager.
   */
  private VolleyManager(Context context) {
    mAppContext = context.getApplicationContext();
    mPendingOnRequestQueueReadyListeners = new ConcurrentLinkedQueue<>();
    HttpURLConnection.setFollowRedirects(true);
  }

  /**
   * Returns the singleton instance.
   * @param context The {@link Context} to use to create the instance.
   * @return The singleton instance.
   */
  public static VolleyManager getInstance(Context context) {
    synchronized (sLock) {
      if (sInstance == null) {
        // getApplicationContext() is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        sInstance = new VolleyManager(context);
      }
      return sInstance;
    }
  }

  /**
   * Asynchronously returns an instance of {@link RequestQueue}. The reason this is done
   * asynchronously is due to Volley's {@link DiskBasedCache}. The more files in the cache, the
   * longer DiskBasedCache takes to go through every entry in the cache as it initializes.
   * @param listener Listener that awaits a successfully-created RequestQueue.
   * @see <a href=
   * "http://stackoverflow.com/questions/20916478/performance-issue-with-volleys-diskbasedcache">
   * http://stackoverflow.com/questions/20916478/performance-issue-with-volleys-diskbasedcache</a>
   */
  public synchronized void getRequestQueue(final OnRequestQueueReadyListener listener) {
    synchronized (mPendingOnRequestQueueReadyListeners) {
      if (mRequestQueue == null) {
        mPendingOnRequestQueueReadyListeners.add(listener);
        if (mPendingOnRequestQueueReadyListeners.size() == 1) {
          asyncNewRequestQueue();
        }
      } else if (listener != null) {
        listener.onRequestQueueReady(mRequestQueue);
      }
    }
  }

  /**
   * Asynchronously returns an instance of {@link ImageLoader}. The reason this is done
   * asynchronously is due to Volley's {@link DiskBasedCache}. The more files in the cache, the
   * longer DiskBasedCache takes to go through every entry in the cache as it initializes.
   * @param listener Listener that awaits a successfully-created ImageLoader.
   * @see <a href=
   * "http://stackoverflow.com/questions/20916478/performance-issue-with-volleys-diskbasedcache">
   * http://stackoverflow.com/questions/20916478/performance-issue-with-volleys-diskbasedcache</a>
   */
  public synchronized void getImageLoader(final OnImageLoaderReadyListener listener) {
    if (mImageLoader == null) {
      getRequestQueue(new OnRequestQueueReadyListener() {
        @Override
        public void onRequestQueueReady(RequestQueue requestQueue) {
          mImageLoader = new ImageLoader(requestQueue, new CustomImageCache());
          if (listener != null) {
            listener.onImageLoaderReady(mImageLoader);
          }
        }
      });
    } else if (listener != null) {
      listener.onImageLoaderReady(mImageLoader);
    }
  }

  /**
   * Convenience method for adding requests to the request queue, even if the queue has not
   * yet finished initializing.
   * @param request The request to add
   */
  public void addToRequestQueue(final Request request) {
    getRequestQueue(new OnRequestQueueReadyListener() {
      @Override
      public void onRequestQueueReady(RequestQueue requestQueue) {
        requestQueue.add(request);
      }
    });
  }

  /**
   * Asynchronously get this VolleyManager's {@link RequestQueue} instance. This instance is
   * delivered asynchronously because Volley's RequestQueue implementation may have a lengthy
   * initialization time due to its {@link DiskBasedCache} implementation.
   * @see <a href=
   * "http://stackoverflow.com/questions/20916478/performance-issue-with-volleys-diskbasedcache">
   * http://stackoverflow.com/questions/20916478/performance-issue-with-volleys-diskbasedcache</a>
   */
  private void asyncNewRequestQueue() {
    AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, RequestQueue>() {
      @Override
      protected RequestQueue doInBackground(Void... params) {
        return Volley.newRequestQueue(mAppContext);
      }

      @Override
      protected void onPostExecute(RequestQueue requestQueue) {
        mRequestQueue = requestQueue;
        synchronized (mPendingOnRequestQueueReadyListeners) {
          while (!mPendingOnRequestQueueReadyListeners.isEmpty()) {
            OnRequestQueueReadyListener listener = mPendingOnRequestQueueReadyListeners.poll();
            listener.onRequestQueueReady(requestQueue);
          }
        }
      }
    });
  }

  /**
   * Interface for listening for a {@link RequestQueue} whose initialization has completed.
   * @see {@link VolleyManager#getRequestQueue(OnRequestQueueReadyListener)}
   */
  public interface OnRequestQueueReadyListener {
    void onRequestQueueReady(RequestQueue requestQueue);
  }

  /**
   * Interface for listening for a {@link ImageLoader} whose initialization has completed.
   * @see {@link VolleyManager#getImageLoader(OnImageLoaderReadyListener)}
   */
  public interface OnImageLoaderReadyListener {
    void onImageLoaderReady(ImageLoader imageLoader);
  }

  /**
   * An {@link ImageCache} implementation that defines an LRU cache.
   */
  private static final class CustomImageCache implements ImageCache {
    /**
     * Default LRU cache size
     */
    private static final int LRU_CACHE_SIZE = 20;

    /**
     * The LRU bitmap cache.
     */
    private final LruCache<String, Bitmap> mCache;

    /**
     * Constructor that initializes the cache.
     */
    CustomImageCache() {
      mCache = new LruCache<>(LRU_CACHE_SIZE);
    }

    /**
     * Returns the bitmap with the given url from the cache.
     * @param url The bitmap url.
     * @return The cached bitmap if it exists, and null otherwise.
     */
    @Override
    public Bitmap getBitmap(String url) {
      return mCache.get(url);
    }

    /**
     * Places a bitmap in the cache.
     * @param url The bitmap url.
     * @param bitmap The bitmap to store in the cache.
     */
    @Override
    public void putBitmap(String url, Bitmap bitmap) {
      mCache.put(url, bitmap);
    }
  }
}
