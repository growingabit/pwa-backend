package io.growingabit.common.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;

import io.growingabit.common.model.BaseModel;

public class BaseDao<T extends BaseModel> {

  protected final Class<T> clazz;

  public BaseDao(final Class<T> clazz) {
    this.clazz = clazz;
  }

  public boolean exist(final String webSafeStringKey) throws IllegalArgumentException {
    Preconditions.checkArgument(StringUtils.isNotEmpty(webSafeStringKey), "webSafeStringKey cannot be null");
    return ObjectifyService.ofy().load().key(Key.create(webSafeStringKey)).now() != null;
  }

  public T find(final Key<T> key) throws NotFoundException, IllegalArgumentException {
    Preconditions.checkArgument(key != null, "key cannot be null");
    return ObjectifyService.ofy().load().key(key).safe();
  }

  public T find(final String webSafeStringKey) throws NotFoundException, IllegalArgumentException {
    Preconditions.checkArgument(StringUtils.isNotEmpty(webSafeStringKey), "webSafeStringKey cannot be null");
    final Key<T> key = Key.create(webSafeStringKey);
    return ObjectifyService.ofy().load().key(key).safe();
  }

  public List<T> findAll() {
    return this.findAll(0);
  }

  public List<T> findAll(final int limit) throws IllegalArgumentException {
    Preconditions.checkArgument(limit >= 0, "limit must be positive or equal to 0");
    return ObjectifyService.ofy().load().type(this.clazz).limit(limit).list();
  }

  public List<T> findAll(final List<String> webSafeStringKeys) throws IllegalArgumentException {
    Preconditions.checkArgument((webSafeStringKeys != null), "keys list is null!");
    return Lists.newArrayList(ObjectifyService.ofy().load().keys(this.getKeysList(webSafeStringKeys)).values());
  }

  public List<Key<T>> keys() {
    return this.keys(0);
  }

  public List<Key<T>> keys(final int limit) throws IllegalArgumentException {
    Preconditions.checkArgument(limit >= 0, "limit must be positive or equal to 0");
    return ObjectifyService.ofy().load().type(this.clazz).limit(limit).keys().list();
  }

  public Key<T> persist(final T entity) {
    Preconditions.checkArgument(entity != null);
    return ObjectifyService.ofy().save().entity(entity).now();
  }

  public Map<Key<T>, T> persist(final List<T> entity) {
    Preconditions.checkArgument(entity != null);
    return ObjectifyService.ofy().save().entities(entity).now();
  }

  public void delete(final T entity) {
    if (entity != null) {
      ObjectifyService.ofy().delete().entity(entity).now();
    }

  }

  public void delete(final String webSafeStringKey) {
    if (webSafeStringKey != null) {
      ObjectifyService.ofy().delete().key(Key.create(webSafeStringKey)).now();
    }
  }

  public void delete(final List<String> webSafeStringKeys) {
    if (webSafeStringKeys != null) {
      ObjectifyService.ofy().delete().keys(this.getKeysList(webSafeStringKeys)).now();
    }

  }

  public void deleteAll() {
    final List<Key<T>> keys = this.keys();
    ObjectifyService.ofy().delete().keys(keys).now();
  }

  private List<Key<T>> getKeysList(final List<String> webSafeStringKeys) {
    final List<Key<T>> keys = Lists.newArrayListWithCapacity(webSafeStringKeys.size());
    for (final String currentWebSafeString : webSafeStringKeys) {
      final Key<T> key = Key.create(currentWebSafeString);
      keys.add(key);
    }
    return keys;
  }
}
