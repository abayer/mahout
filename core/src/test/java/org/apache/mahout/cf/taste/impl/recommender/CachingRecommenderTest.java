/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.mahout.cf.taste.impl.recommender;

import org.apache.mahout.cf.taste.impl.TasteTestCase;
import org.apache.mahout.cf.taste.model.Item;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.Rescorer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Tests {@link CachingRecommender}.</p>
 */
public final class CachingRecommenderTest extends TasteTestCase {

  public void testRecommender() throws Exception {
    AtomicInteger recommendCount = new AtomicInteger();
    Recommender mockRecommender = new MockRecommender(recommendCount);

    Recommender cachingRecommender = new CachingRecommender(mockRecommender);
    cachingRecommender.recommend("1", 1);
    assertEquals(1, recommendCount.get());
    cachingRecommender.recommend("2", 1);
    assertEquals(2, recommendCount.get());
    cachingRecommender.recommend("1", 1);
    assertEquals(2, recommendCount.get());
    cachingRecommender.recommend("2", 1);
    assertEquals(2, recommendCount.get());
    cachingRecommender.refresh(null);
    cachingRecommender.recommend("1", 1);
    assertEquals(3, recommendCount.get());
    cachingRecommender.recommend("2", 1);
    assertEquals(4, recommendCount.get());
    cachingRecommender.recommend("3", 1);
    assertEquals(5, recommendCount.get());

    // Results from this recommend() method can be cached...
    Rescorer<Item> rescorer = NullRescorer.getItemInstance();
    cachingRecommender.refresh(null);
    cachingRecommender.recommend("1", 1, rescorer);
    assertEquals(6, recommendCount.get());
    cachingRecommender.recommend("2", 1, rescorer);
    assertEquals(7, recommendCount.get());
    cachingRecommender.recommend("1", 1, rescorer);
    assertEquals(7, recommendCount.get());
    cachingRecommender.recommend("2", 1, rescorer);
    assertEquals(7, recommendCount.get());

    // until you switch Rescorers
    cachingRecommender.recommend("1", 1, null);
    assertEquals(8, recommendCount.get());
    cachingRecommender.recommend("2", 1, null);
    assertEquals(9, recommendCount.get());

    cachingRecommender.refresh(null);
    cachingRecommender.estimatePreference("test1", "1");
    assertEquals(10, recommendCount.get());
    cachingRecommender.estimatePreference("test1", "2");
    assertEquals(11, recommendCount.get());
    cachingRecommender.estimatePreference("test1", "2");
    assertEquals(11, recommendCount.get());
  }

}
