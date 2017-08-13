package io.growingabit.app.dao;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import io.growingabit.utils.BaseDatastoreTest;
import io.growingabit.app.model.BaseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Before;
import org.junit.Test;

public class BaseDaoTest extends BaseDatastoreTest {

  private BaseDao<DummyModel> baseDao;

  @Before
  public void setUp() {
    ObjectifyService.register(DummyModel.class);
    this.baseDao = new BaseDao<DummyModel>(DummyModel.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExistNullKey() {
    baseDao.exist(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExistEmptyKey() {
    baseDao.exist("");
  }

  @Test
  public void testExist() {
    DummyModel model = new DummyModel(null);
    baseDao.persist(model);
    boolean exist = baseDao.exist(model.getWebSafeKey());
    assertThat(exist).isTrue();
  }

  @Test
  public void testNotExist() {
    DummyModel model = new DummyModel(null);
    baseDao.persist(model);
    String modelKey = model.getWebSafeKey();
    baseDao.delete(model);
    boolean exist = baseDao.exist(model.getWebSafeKey());
    assertThat(exist).isFalse();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindNullKey() {
    baseDao.find(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindEmptyKey() {
    baseDao.find("");
  }

  @Test
  public void testFind() {
    DummyModel model = new DummyModel(null);
    baseDao.persist(model);
    DummyModel findedModel = baseDao.find(model.getWebSafeKey());
    assertThat(findedModel).isEqualTo(model);
  }

  @Test
  public void testFindAll() {
    assertThat(baseDao.findAll()).isEmpty();

    DummyModel model = new DummyModel(null);
    DummyModel anotherModel = new DummyModel(null);
    DummyModel yeatAnotherModel = new DummyModel(null);

    baseDao.persist(model);
    baseDao.persist(anotherModel);
    baseDao.persist(yeatAnotherModel);

    assertThat(baseDao.findAll()).containsExactly(model, anotherModel, yeatAnotherModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindAllWithNegativeLimit() {
    baseDao.findAll(-1);
  }

  @Test
  public void testFindAllWithLimit() {
    final int numModel = ThreadLocalRandom.current().nextInt(1, 11);
    for (int i = 0; i < numModel; i++) {
      baseDao.persist(new DummyModel(null));
    }

    assertThat(baseDao.findAll(0)).hasSize(numModel);

    int randomNum = ThreadLocalRandom.current().nextInt(1, numModel + 1);
    assertThat(baseDao.findAll(randomNum)).hasSize(randomNum);
  }

  @Test
  public void testFindAllbyKeys() {
    DummyModel model = new DummyModel(null);
    DummyModel anotherModel = new DummyModel(null);
    DummyModel yeatAnotherModel = new DummyModel(null);

    baseDao.persist(model);
    baseDao.persist(anotherModel);
    baseDao.persist(yeatAnotherModel);

    assertThat(baseDao.findAll(new ArrayList<String>())).isEmpty();

    List<String> keys = Lists.newArrayList(model.getWebSafeKey(), anotherModel.getWebSafeKey());
    assertThat(baseDao.findAll(keys)).containsExactly(model, anotherModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindAllbyKeysNull() {
    assertThat(baseDao.findAll(null)).isEmpty();
  }

  @Test
  public void testkeys() {
    assertThat(baseDao.keys()).isEmpty();

    DummyModel model = new DummyModel(null);
    DummyModel anotherModel = new DummyModel(null);
    DummyModel yeatAnotherModel = new DummyModel(null);

    baseDao.persist(model);
    baseDao.persist(anotherModel);
    baseDao.persist(yeatAnotherModel);

    assertThat(baseDao.keys()).containsExactly(Key.create(model), Key.create(anotherModel), Key.create(yeatAnotherModel));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testKeysWithNegativeLimit() {
    baseDao.keys(-1);
  }

  @Test
  public void testKeysWithLimit() {
    final int numModel = ThreadLocalRandom.current().nextInt(1, 11);
    for (int i = 0; i < numModel; i++) {
      baseDao.persist(new DummyModel(null));
    }

    assertThat(baseDao.keys(0)).hasSize(numModel);

    int randomNum = ThreadLocalRandom.current().nextInt(1, numModel + 1);
    assertThat(baseDao.keys(randomNum)).hasSize(randomNum);
  }

  @Test(expected = IllegalArgumentException.class)
  public void persistNull() {
    DummyModel model = null;
    baseDao.persist(model);
  }

  @Test
  public void persist() {
    DummyModel model = new DummyModel(null);
    Key<DummyModel> key = baseDao.persist(model);
    assertThat(key).isEqualTo(Key.create(model));
    assertThat(baseDao.find(key.toWebSafeString())).isEqualTo(model);
  }

  @Test(expected = IllegalArgumentException.class)
  public void persistAllWithNull() {
    List<DummyModel> models = null;
    baseDao.persist(models);
  }

  @Test
  public void persistAll() {
    DummyModel model = new DummyModel(null);
    DummyModel anotherModel = new DummyModel(null);
    DummyModel yeatAnotherModel = new DummyModel(null);

    List<DummyModel> models = Lists.newArrayList(model, anotherModel, yeatAnotherModel);
    Map<Key<DummyModel>, DummyModel> keys = baseDao.persist(models);
    assertThat(baseDao.findAll()).hasSize(3);
  }

  @Test
  public void deleteNull() {
    final int numModel = ThreadLocalRandom.current().nextInt(1, 11);
    for (int i = 0; i < numModel; i++) {
      baseDao.persist(new DummyModel(null));
    }

    DummyModel model = null;
    baseDao.delete(model);

    assertThat(baseDao.findAll()).hasSize(numModel);
  }

  @Test(expected = NotFoundException.class)
  public void deleteModel() {
    DummyModel model = new DummyModel(null);
    baseDao.persist(model);
    baseDao.delete(model);
    baseDao.find(model.getWebSafeKey());
  }

  @Test
  public void deleteByKeyNull() {
    final int numModel = ThreadLocalRandom.current().nextInt(1, 11);
    for (int i = 0; i < numModel; i++) {
      baseDao.persist(new DummyModel(null));
    }

    String key = null;
    baseDao.delete(key);

    assertThat(baseDao.findAll()).hasSize(numModel);
  }

  @Test(expected = NotFoundException.class)
  public void deleteByKey() {
    DummyModel model = new DummyModel(null);
    baseDao.persist(model);
    baseDao.delete(model.getWebSafeKey());
    baseDao.find(model.getWebSafeKey());
  }


  @Test
  public void deleteByKeysNull() {
    final int numModel = ThreadLocalRandom.current().nextInt(1, 11);
    for (int i = 0; i < numModel; i++) {
      baseDao.persist(new DummyModel(null));
    }

    List<String> keys = null;
    baseDao.delete(keys);

    assertThat(baseDao.findAll()).hasSize(numModel);
  }

  @Test
  public void deleteByKeys() {
    DummyModel model = new DummyModel(null);
    DummyModel anotherModel = new DummyModel(null);
    DummyModel unDeleted = new DummyModel(null);

    baseDao.persist(model);
    baseDao.persist(anotherModel);
    baseDao.persist(unDeleted);

    List<String> models = Lists.newArrayList(model.getWebSafeKey(), anotherModel.getWebSafeKey());
    baseDao.delete(models);

    assertThat(baseDao.findAll()).containsExactly(unDeleted);
  }

  @Test
  public void deleteAll() {
    final int numModel = ThreadLocalRandom.current().nextInt(1, 11);
    for (int i = 0; i < numModel; i++) {
      baseDao.persist(new DummyModel(null));
    }

    baseDao.deleteAll();

    assertThat(baseDao.findAll()).isEmpty();
  }


  @Entity
  private class DummyModel extends BaseModel {

    @Id
    private Long id;
    private String property;

    public DummyModel(String property) {
      this.property = property;
    }

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getProperty() {
      return property;
    }

    public void setProperty(String property) {
      this.property = property;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }

      DummyModel model = (DummyModel) obj;

      if (!id.equals(model.id)) {
        return false;
      }
      return property != null ? property.equals(model.property) : model.property == null;
    }
  }


}
