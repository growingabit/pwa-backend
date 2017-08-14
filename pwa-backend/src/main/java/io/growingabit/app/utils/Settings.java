package io.growingabit.app.utils;

import java.nio.charset.StandardCharsets;
import javax.xml.ws.WebServiceException;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class Settings {

  private static final String DEFAULT_PROPERTIES_NAME = "dev.properties";
  private static Configuration config = null;

  public static final Configuration getConfiguration() {

    // NOTE:
    // Someone could be tempted to use DCL.
    // Read http://www.javaworld.com/article/2074979/java-concurrency/double-checked-locking--clever--but-broken.html
    // For now seems to be resonable simply sinchronize the execution
    synchronized (DEFAULT_PROPERTIES_NAME) {
      if (config == null) {
        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
            .configure(params.properties()
                .setURL(Settings.class.getClassLoader().getResource(DEFAULT_PROPERTIES_NAME))
                .setThrowExceptionOnMissing(true)
                .setListDelimiterHandler(new DefaultListDelimiterHandler(';'))
                .setIncludesAllowed(true)
                .setEncoding(StandardCharsets.UTF_8.name())
            );
        try {
          config = builder.getConfiguration();
        } catch (ConfigurationException exception) {
          throw new WebServiceException(exception);
        }
      }
    }
    return config;
  }
}
