package io.growingabit.app.utils;

import static com.google.common.truth.Truth.assertThat;

import javax.xml.ws.WebServiceException;

import org.apache.commons.configuration2.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.growingabit.testUtils.Utils;

@RunWith(MockitoJUnitRunner.class)
public class SettingsTest {

  @Before
  public void setup() throws NoSuchFieldException, IllegalAccessException {
    Utils.clearSettings();
  }

  @Test
  public void allFileOverwritingTest() {
    final Configuration configuration = new Settings(new ResourceFetcher()).getConfig();

    // dev overwrite all
    assertThat(configuration.getString("dev-prod-def")).isEqualTo("dev");

    // dev overwrite prod
    assertThat(configuration.getString("dev-prod")).isEqualTo("dev");

    // dev overwrite default
    assertThat(configuration.getString("dev-def")).isEqualTo("dev");

    // prod overwrite default
    assertThat(configuration.getString("prod-def")).isEqualTo("prod");

    // dev only propertiy
    assertThat(configuration.getString("dev")).isEqualTo("dev");

    // prod only propertiy
    assertThat(configuration.getString("prod")).isEqualTo("prod");

    // default only propertiy
    assertThat(configuration.getString("def")).isEqualTo("def");
  }

  @Test
  public void missingDevFileTest() {
    final ResourceFetcher resourceFetcher = Mockito.mock(ResourceFetcher.class);
    Mockito.when(resourceFetcher.fetchResource(Mockito.anyString())).thenCallRealMethod();
    Mockito.when(resourceFetcher.fetchResource(Settings.DEV_PROPERTIES_NAME)).thenReturn(null);
    final Configuration configuration = new Settings(resourceFetcher).getConfiguration();

    assertThat(configuration.getString("dev-prod-def")).isEqualTo("prod");

    assertThat(configuration.getString("dev-prod")).isEqualTo("prod");

    assertThat(configuration.getString("dev-def")).isEqualTo("def");

    assertThat(configuration.getString("prod-def")).isEqualTo("prod");

    assertThat(configuration.getString("dev")).isNull();

    assertThat(configuration.getString("prod")).isEqualTo("prod");

    assertThat(configuration.getString("def")).isEqualTo("def");

  }

  @Test
  public void missingProdFileTest() {
    final ResourceFetcher resourceFetcher = Mockito.mock(ResourceFetcher.class);
    Mockito.when(resourceFetcher.fetchResource(Mockito.anyString())).thenCallRealMethod();
    Mockito.when(resourceFetcher.fetchResource(Settings.PRODUCTION_PROPERTIES_NAME)).thenReturn(null);
    final Configuration configuration = new Settings(resourceFetcher).getConfiguration();

    assertThat(configuration.getString("dev-prod-def")).isEqualTo("dev");

    assertThat(configuration.getString("dev-prod")).isEqualTo("dev");

    assertThat(configuration.getString("dev-def")).isEqualTo("dev");

    assertThat(configuration.getString("prod-def")).isEqualTo("def");

    assertThat(configuration.getString("dev")).isEqualTo("dev");

    assertThat(configuration.getString("prod")).isNull();

    assertThat(configuration.getString("def")).isEqualTo("def");

  }

  @Test
  public void missingDevAndProdFilesTest() {
    final ResourceFetcher resourceFetcher = Mockito.mock(ResourceFetcher.class);
    Mockito.when(resourceFetcher.fetchResource(Mockito.anyString())).thenCallRealMethod();
    Mockito.when(resourceFetcher.fetchResource(Settings.DEV_PROPERTIES_NAME)).thenReturn(null);
    Mockito.when(resourceFetcher.fetchResource(Settings.PRODUCTION_PROPERTIES_NAME)).thenReturn(null);
    final Configuration configuration = new Settings(resourceFetcher).getConfiguration();

    assertThat(configuration.getString("dev-prod-def")).isEqualTo("def");

    assertThat(configuration.getString("dev-prod")).isNull();

    assertThat(configuration.getString("dev-def")).isEqualTo("def");

    assertThat(configuration.getString("prod-def")).isEqualTo("def");

    assertThat(configuration.getString("dev")).isNull();

    assertThat(configuration.getString("prod")).isNull();

    assertThat(configuration.getString("def")).isEqualTo("def");
  }

  @Test(expected = WebServiceException.class)
  public void missingDefFileTest() {
    final ResourceFetcher resourceFetcher = Mockito.mock(ResourceFetcher.class);
    Mockito.when(resourceFetcher.fetchResource(Mockito.anyString())).thenCallRealMethod();
    Mockito.when(resourceFetcher.fetchResource(Settings.DEFAULT_PROPERTIES_NAME)).thenReturn(null);
    new Settings(resourceFetcher).getConfiguration();
  }

  @Test()
  public void skipDoubleConfigCreationTest() {
    final ResourceFetcher resourceFetcher = Mockito.spy(ResourceFetcher.class);
    final Settings settings = new Settings(resourceFetcher);
    final Configuration config1 = settings.getConfiguration();
    final Configuration config2 = settings.getConfiguration();

    assertThat(config1).isEqualTo(config2);
  }

  @Test()
  public void skipDoubleConfigCreationTest2() {
    final ResourceFetcher resourceFetcher = Mockito.spy(ResourceFetcher.class);
    final Configuration config1 = Settings.getConfig();
    final Configuration config2 = Settings.getConfig();
    assertThat(config1).isEqualTo(config2);
  }

  @Test()
  public void returnSameConfig() {
    final ResourceFetcher resourceFetcher = Mockito.spy(ResourceFetcher.class);
    final Settings settings = new Settings(resourceFetcher);
    final Configuration config1 = settings.getConfiguration();
    final Configuration config2 = Settings.getConfig();
    assertThat(config1).isEqualTo(config2);
  }
}
