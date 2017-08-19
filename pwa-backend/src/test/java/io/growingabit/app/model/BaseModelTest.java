package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import io.growingabit.testUtils.BaseDatastoreTest;
import io.growingabit.app.dao.BaseDao;
import org.junit.Before;
import org.junit.Test;


public class BaseModelTest extends BaseDatastoreTest {

  private BaseDao<DummyModel> baseDao;

  @Before
  public void setUp() {
    ObjectifyService.register(DummyModel.class);
    this.baseDao = new BaseDao<DummyModel>(DummyModel.class);
  }

  @Test
  public void creationDateTest() {
    DummyModel model = new DummyModel();
    assertThat(model.getCreationDate()).isEqualTo(-1L);
    this.baseDao.persist(model);
    assertThat(model.getCreationDate()).isGreaterThan(0L);
  }

  @Test
  public void modifiedDateTest() throws InterruptedException {
    DummyModel model = new DummyModel();
    this.baseDao.persist(model);
    assertThat(model.getCreationDate()).isEqualTo(model.getModifiedDate());
    Thread.sleep(200);
    this.baseDao.persist(model);
    assertThat(model.getModifiedDate()).isGreaterThan(model.getCreationDate());
  }

  @Test
  public void webSafeKeyTest() throws InterruptedException {
    DummyModel model = new DummyModel();
    assertThat(model.getWebSafeKey()).isNull();
    this.baseDao.persist(model);
    assertThat(model.getWebSafeKey()).isNotNull();
  }

  @Entity
  private class DummyModel extends BaseModel {

    @Id
    private Long id;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }
  }

}
