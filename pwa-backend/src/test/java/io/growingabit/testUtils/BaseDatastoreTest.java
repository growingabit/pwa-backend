package io.growingabit.testUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cache.AsyncCacheFilter;
import com.googlecode.objectify.util.Closeable;

public class BaseDatastoreTest {

  private LocalServiceTestHelper helper;
  private Closeable session;

  @BeforeClass
  public static void baseSetUpBeforeClass() {
    ObjectifyService.setFactory(new ObjectifyFactory());
  }

  @Before
  public final void setUpDatastore() {
    this.helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig().setApplyAllHighRepJobPolicy(), new LocalTaskQueueTestConfig(), new LocalMailServiceTestConfig());
    this.session = ObjectifyService.begin();
    this.helper.setUp();
  }

  @After
  public final void tearDownDatastore() {
    AsyncCacheFilter.complete();
    this.session.close();
    this.helper.tearDown();
    this.helper = null;
  }

}
