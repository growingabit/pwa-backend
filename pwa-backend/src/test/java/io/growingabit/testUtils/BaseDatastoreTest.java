package io.growingabit.testUtils;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cache.AsyncCacheFilter;
import com.googlecode.objectify.util.Closeable;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

public class BaseDatastoreTest {

  private LocalServiceTestHelper helper;
  private Closeable session;

  @BeforeClass
  public static void baseSetUpBeforeClass() {
    ObjectifyService.setFactory(new ObjectifyFactory());
  }

  @Before
  public final void setUpDatastore() {
    this.helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig().setApplyAllHighRepJobPolicy());
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
