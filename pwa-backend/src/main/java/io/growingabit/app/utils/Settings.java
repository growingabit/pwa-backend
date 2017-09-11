package io.growingabit.app.utils;

import com.google.common.base.Preconditions;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.xml.ws.WebServiceException;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.OverrideCombiner;

public class Settings {

  public static final String DEFAULT_PROPERTIES_NAME = "default.properties";
  public static final String DEV_PROPERTIES_NAME = "development.properties";
  public static final String PRODUCTION_PROPERTIES_NAME = "production.properties";

  private final ResourceFetcher resourceFetcher;
  private final Parameters paramsInitiator;
  private static CombinedConfiguration config = null;

  public Settings(final ResourceFetcher resourceFetcher) {
    this.resourceFetcher = resourceFetcher;
    this.paramsInitiator = new Parameters();
  }

  public static Configuration getConfig() {
    return new Settings(new ResourceFetcher()).getConfiguration();
  }

  public final Configuration getConfiguration() {

    try {
      final URL defaultPropertiesFile = this.resourceFetcher.fetchResource(Settings.DEFAULT_PROPERTIES_NAME);
      Preconditions.checkNotNull(defaultPropertiesFile, "At least default properties file " + Settings.DEFAULT_PROPERTIES_NAME + " should pre present");
      final Configuration defaultConfig = this.loadConfigurationFile(defaultPropertiesFile);
      /*
        NOTE:
        Someone could be tempted to use DCL.
        Read http://www.javaworld.com/article/2074979/java-concurrency/double-checked-locking--clever--but-broken.html
        For now seems to be resonable simply sinchronize the execution
       */
      synchronized (DEFAULT_PROPERTIES_NAME) {
        if (this.config == null) {
          this.config = new CombinedConfiguration(new OverrideCombiner());

          /*
            Precedences over configuration files:
            - development.properties have maximum priority, overriding all other files
            - production.properties ovveride default but not dev
            - default is the last and ovverride nothing.
          */
          final URL devPropertiesFile = this.resourceFetcher.fetchResource(Settings.DEV_PROPERTIES_NAME);
          if (devPropertiesFile != null) {
            final Configuration devConfig = this.loadConfigurationFile(devPropertiesFile);
            this.config.addConfiguration(devConfig);
          }

          final URL productionPropertiesFile = this.resourceFetcher.fetchResource(Settings.PRODUCTION_PROPERTIES_NAME);
          if (productionPropertiesFile != null) {
            final Configuration productionConfig = this.loadConfigurationFile(productionPropertiesFile);
            this.config.addConfiguration(productionConfig);
          }

          this.config.addConfiguration(defaultConfig);

        }
      }
      return this.config;
    } catch (final Exception e) {
      throw new WebServiceException(e);
    }
  }

  private Configuration loadConfigurationFile(final URL configFileUrl) throws ConfigurationException {
    Preconditions.checkNotNull(configFileUrl);

    return new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
        .configure(this.paramsInitiator.properties()
            .setURL(configFileUrl)
            .setListDelimiterHandler(new DefaultListDelimiterHandler(';'))
            .setIncludesAllowed(true)
            .setEncoding(StandardCharsets.UTF_8.name())
        )
        .getConfiguration();
  }
}
