package io.growingabit.common.dao;

import static com.google.common.truth.Truth.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import io.growingabit.common.model.BaseModel;
import io.growingabit.testUtils.BaseGaeTest;

public class BaseDaoTest extends BaseGaeTest {

  private BaseDao<DummyModel> baseDao;

  @Before
  public void setUp() {
    ObjectifyService.register(DummyModel.class);
    this.baseDao = new BaseDao<>(DummyModel.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExistNullKey() {
    this.baseDao.exist(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExistEmptyKey() {
    this.baseDao.exist("");
  }

  @Test
  public void testExist() {
    final DummyModel model = new DummyModel(null);
    this.baseDao.persist(model);
    final boolean exist = this.baseDao.exist(model.getWebSafeKey());
    assertThat(exist).isTrue();
  }

  @Test
  public void testNotExist() {
    final DummyModel model = new DummyModel(null);
    this.baseDao.persist(model);
    this.baseDao.delete(model);
    final boolean exist = this.baseDao.exist(model.getWebSafeKey());
    assertThat(exist).isFalse();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindNullKey() {
    final Key<DummyModel> key = null;
    this.baseDao.find(key);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindNullKeyString() {
    final String key = null;
    this.baseDao.find(key);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindEmptyKey() {
    this.baseDao.find("");
  }

  @Test
  public void testFind() {
    final DummyModel model = new DummyModel(null);
    this.baseDao.persist(model);
    final DummyModel findedModel = this.baseDao.find(model.getWebSafeKey());
    assertThat(findedModel).isEqualTo(model);
  }

  @Test
  public void testFindByKey() {
    final DummyModel model = new DummyModel(null);
    final Key<DummyModel> key = this.baseDao.persist(model);
    final DummyModel findedModel = this.baseDao.find(key);
    assertThat(findedModel).isEqualTo(model);
  }

  @Test
  public void testFindAll() {
    assertThat(this.baseDao.findAll()).isEmpty();

    final DummyModel model = new DummyModel(null);
    final DummyModel anotherModel = new DummyModel(null);
    final DummyModel yeatAnotherModel = new DummyModel(null);

    this.baseDao.persist(model);
    this.baseDao.persist(anotherModel);
    this.baseDao.persist(yeatAnotherModel);

    assertThat(this.baseDao.findAll()).containsExactly(model, anotherModel, yeatAnotherModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindAllWithNegativeLimit() {
    this.baseDao.findAll(-1);
  }

  @Test
  public void testFindAllWithLimit() {
    final int numModel = ThreadLocalRandom.current().nextInt(1, 11);
    for (int i = 0; i < numModel; i++) {
      this.baseDao.persist(new DummyModel(null));
    }

    assertThat(this.baseDao.findAll(0)).hasSize(numModel);

    final int randomNum = ThreadLocalRandom.current().nextInt(1, numModel + 1);
    assertThat(this.baseDao.findAll(randomNum)).hasSize(randomNum);
  }

  @Test
  public void testFindAllbyKeys() {
    final DummyModel model = new DummyModel(null);
    final DummyModel anotherModel = new DummyModel(null);
    final DummyModel yeatAnotherModel = new DummyModel(null);

    this.baseDao.persist(model);
    this.baseDao.persist(anotherModel);
    this.baseDao.persist(yeatAnotherModel);

    assertThat(this.baseDao.findAll(new ArrayList<String>())).isEmpty();

    final List<String> keys = Lists.newArrayList(model.getWebSafeKey(), anotherModel.getWebSafeKey());
    assertThat(this.baseDao.findAll(keys)).containsExactly(model, anotherModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindAllbyKeysNull() {
    assertThat(this.baseDao.findAll(null)).isEmpty();
  }

  @Test
  public void testkeys() {
    assertThat(this.baseDao.keys()).isEmpty();

    final DummyModel model = new DummyModel(null);
    final DummyModel anotherModel = new DummyModel(null);
    final DummyModel yeatAnotherModel = new DummyModel(null);

    this.baseDao.persist(model);
    this.baseDao.persist(anotherModel);
    this.baseDao.persist(yeatAnotherModel);

    assertThat(this.baseDao.keys()).containsExactly(Key.create(model), Key.create(anotherModel), Key.create(yeatAnotherModel));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testKeysWithNegativeLimit() {
    this.baseDao.keys(-1);
  }

  @Test
  public void testKeysWithLimit() {
    final int numModel = ThreadLocalRandom.current().nextInt(1, 11);
    for (int i = 0; i < numModel; i++) {
      this.baseDao.persist(new DummyModel(null));
    }

    assertThat(this.baseDao.keys(0)).hasSize(numModel);

    final int randomNum = ThreadLocalRandom.current().nextInt(1, numModel + 1);
    assertThat(this.baseDao.keys(randomNum)).hasSize(randomNum);
  }

  @Test(expected = IllegalArgumentException.class)
  public void persistNull() {
    final DummyModel model = null;
    this.baseDao.persist(model);
  }

  @Test
  public void persist() {
    final DummyModel model = new DummyModel(null);
    final Key<DummyModel> key = this.baseDao.persist(model);
    assertThat(key).isEqualTo(Key.create(model));
    assertThat(this.baseDao.find(key.toWebSafeString())).isEqualTo(model);
  }

  @Test(expected = IllegalArgumentException.class)
  public void persistAllWithNull() {
    final List<DummyModel> models = null;
    this.baseDao.persist(models);
  }

  @Test
  public void persistAll() {
    final DummyModel model = new DummyModel(null);
    final DummyModel anotherModel = new DummyModel(null);
    final DummyModel yeatAnotherModel = new DummyModel(null);

    final List<DummyModel> models = Lists.newArrayList(model, anotherModel, yeatAnotherModel);
    this.baseDao.persist(models);
    assertThat(this.baseDao.findAll()).hasSize(3);
  }

  @Test
  public void deleteNull() {
    final int numModel = ThreadLocalRandom.current().nextInt(1, 11);
    for (int i = 0; i < numModel; i++) {
      this.baseDao.persist(new DummyModel(null));
    }

    final DummyModel model = null;
    this.baseDao.delete(model);

    assertThat(this.baseDao.findAll()).hasSize(numModel);
  }

  @Test(expected = NotFoundException.class)
  public void deleteModel() {
    final DummyModel model = new DummyModel(null);
    this.baseDao.persist(model);
    this.baseDao.delete(model);
    this.baseDao.find(model.getWebSafeKey());
  }

  @Test
  public void deleteByKeyNull() {
    final int numModel = ThreadLocalRandom.current().nextInt(1, 11);
    for (int i = 0; i < numModel; i++) {
      this.baseDao.persist(new DummyModel(null));
    }

    final String key = null;
    this.baseDao.delete(key);

    assertThat(this.baseDao.findAll()).hasSize(numModel);
  }

  @Test(expected = NotFoundException.class)
  public void deleteByKey() {
    final DummyModel model = new DummyModel(null);
    this.baseDao.persist(model);
    this.baseDao.delete(model.getWebSafeKey());
    this.baseDao.find(model.getWebSafeKey());
  }


  @Test
  public void deleteByKeysNull() {
    final int numModel = ThreadLocalRandom.current().nextInt(1, 11);
    for (int i = 0; i < numModel; i++) {
      this.baseDao.persist(new DummyModel(null));
    }

    final List<String> keys = null;
    this.baseDao.delete(keys);

    assertThat(this.baseDao.findAll()).hasSize(numModel);
  }

  @Test
  public void deleteByKeys() {
    final DummyModel model = new DummyModel(null);
    final DummyModel anotherModel = new DummyModel(null);
    final DummyModel unDeleted = new DummyModel(null);

    this.baseDao.persist(model);
    this.baseDao.persist(anotherModel);
    this.baseDao.persist(unDeleted);

    final List<String> models = Lists.newArrayList(model.getWebSafeKey(), anotherModel.getWebSafeKey());
    this.baseDao.delete(models);

    assertThat(this.baseDao.findAll()).containsExactly(unDeleted);
  }

  @Test
  public void deleteAll() {
    final int numModel = ThreadLocalRandom.current().nextInt(1, 11);
    for (int i = 0; i < numModel; i++) {
      this.baseDao.persist(new DummyModel(null));
    }

    this.baseDao.deleteAll();

    assertThat(this.baseDao.findAll()).isEmpty();
  }


  @Entity
  private class DummyModel extends BaseModel {

    @Id
    private Long id;
    private String property;

    public DummyModel(final String property) {
      this.property = property;
    }

    public Long getId() {
      return this.id;
    }

    public void setId(final Long id) {
      this.id = id;
    }

    public String getProperty() {
      return this.property;
    }

    public void setProperty(final String property) {
      this.property = property;
    }

    @Override
    public boolean equals(final Object obj) {

      if (obj == null) {
        return false;
      }
      if (obj == this) {
        return true;
      }
      if (obj.getClass() != getClass()) {
        return false;
      }
      final DummyModel rhs = (DummyModel) obj;
      return new EqualsBuilder()
          .appendSuper(super.equals(obj))
          .append(this.id, rhs.id)
          .append(this.property, rhs.property)
          .isEquals();
    }

    @Override
    public int hashCode() {
      return new HashCodeBuilder(17, 37)
          .append(this.id)
          .append(this.property)
          .toHashCode();
    }

  }
}
