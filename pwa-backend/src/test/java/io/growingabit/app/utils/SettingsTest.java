package io.growingabit.app.utils;

import static com.google.common.truth.Truth.assertThat;

import javax.xml.ws.WebServiceException;
import org.apache.commons.configuration2.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SettingsTest {

  @Test
  public void allFileOverwritingTest() {
    Configuration configuration = new Settings(new ResourceFetcher()).getConfig();

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
  public void missingDevFileTest(){
    ResourceFetcher resourceFetcher = Mockito.mock(ResourceFetcher.class);
    Mockito.when(resourceFetcher.fetchResource(Mockito.anyString())).thenCallRealMethod();
    Mockito.when(resourceFetcher.fetchResource(Settings.DEV_PROPERTIES_NAME)).thenReturn(null);
    Configuration configuration = new Settings(resourceFetcher).getConfig();

    assertThat(configuration.getString("dev-prod-def")).isEqualTo("prod");

    assertThat(configuration.getString("dev-prod")).isEqualTo("prod");

    assertThat(configuration.getString("dev-def")).isEqualTo("def");

    assertThat(configuration.getString("prod-def")).isEqualTo("prod");

    assertThat(configuration.getString("dev")).isNull();

    assertThat(configuration.getString("prod")).isEqualTo("prod");

    assertThat(configuration.getString("def")).isEqualTo("def");

  }

  @Test
  public void missingProdFileTest(){
    ResourceFetcher resourceFetcher = Mockito.mock(ResourceFetcher.class);
    Mockito.when(resourceFetcher.fetchResource(Mockito.anyString())).thenCallRealMethod();
    Mockito.when(resourceFetcher.fetchResource(Settings.PRODUCTION_PROPERTIES_NAME)).thenReturn(null);
    Configuration configuration = new Settings(resourceFetcher).getConfig();

    assertThat(configuration.getString("dev-prod-def")).isEqualTo("dev");

    assertThat(configuration.getString("dev-prod")).isEqualTo("dev");

    assertThat(configuration.getString("dev-def")).isEqualTo("dev");

    assertThat(configuration.getString("prod-def")).isEqualTo("def");

    assertThat(configuration.getString("dev")).isEqualTo("dev");

    assertThat(configuration.getString("prod")).isNull();

    assertThat(configuration.getString("def")).isEqualTo("def");

  }

  @Test
  public void missingDevAndProdFilesTest(){
    ResourceFetcher resourceFetcher = Mockito.mock(ResourceFetcher.class);
    Mockito.when(resourceFetcher.fetchResource(Mockito.anyString())).thenCallRealMethod();
    Mockito.when(resourceFetcher.fetchResource(Settings.DEV_PROPERTIES_NAME)).thenReturn(null);
    Mockito.when(resourceFetcher.fetchResource(Settings.PRODUCTION_PROPERTIES_NAME)).thenReturn(null);
    Configuration configuration = new Settings(resourceFetcher).getConfig();

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
    ResourceFetcher resourceFetcher = Mockito.mock(ResourceFetcher.class);
    Mockito.when(resourceFetcher.fetchResource(Mockito.anyString())).thenCallRealMethod();
    Mockito.when(resourceFetcher.fetchResource(Settings.DEFAULT_PROPERTIES_NAME)).thenReturn(null);
    new Settings(resourceFetcher).getConfig();
  }

  @Test()
  public void skipDoubleConfigCreationTest() {
    ResourceFetcher resourceFetcher = Mockito.spy(ResourceFetcher.class);
    Settings settings = new Settings(resourceFetcher);
    Configuration config1 = settings.getConfig();
    Configuration config2 = settings.getConfig();

    assertThat(config1).isEqualTo(config2);
  }
}
