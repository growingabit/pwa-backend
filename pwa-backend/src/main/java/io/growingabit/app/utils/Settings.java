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

  private ResourceFetcher resourceFetcher;
  private Parameters paramsInitiator;
  private CombinedConfiguration config = null;

  public Settings(ResourceFetcher resourceFetcher){
    this.resourceFetcher = resourceFetcher;
    this.paramsInitiator = new Parameters();
  }

  public final Configuration getConfig() {

    try{
      URL defaultPropertiesFile = this.resourceFetcher.fetchResource(Settings.DEFAULT_PROPERTIES_NAME);
      Preconditions.checkNotNull(defaultPropertiesFile, "At least default properties file " + Settings.DEFAULT_PROPERTIES_NAME + " should pre present");
      Configuration defaultConfig = this.loadConfigurationFile(defaultPropertiesFile);
      /*
        NOTE:
        Someone could be tempted to use DCL.
        Read http://www.javaworld.com/article/2074979/java-concurrency/double-checked-locking--clever--but-broken.html
        For now seems to be resonable simply sinchronize the execution
       */
      synchronized (DEFAULT_PROPERTIES_NAME) {
        if (config == null) {
          config = new CombinedConfiguration(new OverrideCombiner());

          /*
            Precedences over configuration files:
            - development.properties have maximum priority, overriding all other files
            - production.properties ovveride default but not dev
            - default is the last and ovverride nothing.
         */
          URL devPropertiesFile = this.resourceFetcher.fetchResource(Settings.DEV_PROPERTIES_NAME);
          if(devPropertiesFile != null){
            Configuration devConfig = this.loadConfigurationFile(devPropertiesFile);
            config.addConfiguration(devConfig);
          }

          URL productionPropertiesFile = this.resourceFetcher.fetchResource(Settings.PRODUCTION_PROPERTIES_NAME);
          if(productionPropertiesFile != null){
            Configuration productionConfig = this.loadConfigurationFile(productionPropertiesFile);
            config.addConfiguration(productionConfig);
          }

          config.addConfiguration(defaultConfig);

        }
      }
      return config;
    }catch (Exception e){
      throw new WebServiceException(e);
    }
  }

  private Configuration loadConfigurationFile(URL configFileUrl) throws ConfigurationException {
    Preconditions.checkNotNull(configFileUrl);

    return new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
        .configure(paramsInitiator.properties()
            .setURL(configFileUrl)
            .setListDelimiterHandler(new DefaultListDelimiterHandler(';'))
            .setIncludesAllowed(true)
            .setEncoding(StandardCharsets.UTF_8.name())
        )
        .getConfiguration();
  }
}
