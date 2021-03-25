package acceptance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/** Integration Test for TaskanaEngineConfiguration. */
public final class TaskanaEngineTestConfiguration {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(TaskanaEngineTestConfiguration.class);
  private static final int POOL_TIME_TO_WAIT = 50;
  private static final DataSource DATA_SOURCE;
  private static String schemaName = null;

  static {
    // TODO: select container depending on System properties

    JdbcDatabaseContainer<?> container;
    // TODO: set locale changes? - why do we need them?
      container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:10"));
//    container =
//        new Db2Container(
//                DockerImageName.parse("taskana/db2:11.1").asCompatibleSubstituteFor("ibmcom/db2"))
//            .withCommand("-d")
//            .waitingFor(
//                new LogMessageWaitStrategy()
//                    .withRegEx(".*DB2START processing was successful.*")
//                    .withStartupTimeout(Duration.of(1, ChronoUnit.MINUTES)))
//            .withUsername("db2inst1")
//            .withPassword("db2inst1-pwd")
//            .withDatabaseName("TSKDB");
    container.start();
    DATA_SOURCE = createDataSource(container);
    //    String userHomeDirectory = System.getProperty("user.home");
    //    String propertiesFileName = userHomeDirectory + "/taskanaUnitTest.properties";
    //    File f = new File(propertiesFileName);
    //    if (f.exists() && !f.isDirectory()) {
    //      DATA_SOURCE = createDataSourceFromProperties(propertiesFileName);
    //    } else {
    //      DATA_SOURCE = createDefaultDataSource();
    //    }
  }

  private TaskanaEngineTestConfiguration() {}

  /**
   * returns the Datasource used for Junit test. If the file {user.home}/taskanaUnitTest.properties
   * is present, the Datasource is created according to the properties jdbcDriver, jdbcUrl,
   * dbUserName and dbPassword. Assuming, the database has the name tskdb, a sample properties file
   * for DB2 looks as follows: jdbcDriver=com.ibm.db2.jcc.DB2Driver
   * jdbcUrl=jdbc:db2://localhost:50000/tskdb dbUserName=db2user dbPassword=db2password If any of
   * these properties is missing, or the file doesn't exist, the default Datasource for h2 in-memory
   * db is created.
   *
   * @return dataSource for unit test
   */
  public static DataSource getDataSource() {
    return DATA_SOURCE;
  }

  /**
   * returns the SchemaName used for Junit test. If the file {user.home}/taskanaUnitTest.properties
   * is present, the SchemaName is created according to the property schemaName. a sample properties
   * file for DB2 looks as follows: jdbcDriver=com.ibm.db2.jcc.DB2Driver
   * jdbcUrl=jdbc:db2://localhost:50000/tskdb dbUserName=db2user dbPassword=db2password
   * schemaName=TASKANA If any of these properties is missing, or the file doesn't exist, the
   * default schemaName TASKANA is created used.
   *
   * @return String for unit test
   */
  public static String getSchemaName() {
    if (schemaName == null) {
      String userHomeDirectroy = System.getProperty("user.home");
      String propertiesFileName = userHomeDirectroy + "/taskanaUnitTest.properties";
      File f = new File(propertiesFileName);
      if (f.exists() && !f.isDirectory()) {
        schemaName = getSchemaNameFromPropertiesObject(propertiesFileName);
      } else {
        schemaName = "taskana";
      }
    }
    return schemaName;
  }

  /**
   * create data source from properties file.
   *
   * @param propertiesFileName the name of the property file
   * @return the parsed datasource.
   */
  public static DataSource createDataSourceFromProperties(String propertiesFileName) {
    DataSource ds;
    try (InputStream input = new FileInputStream(propertiesFileName)) {
      Properties prop = new Properties();
      prop.load(input);
      boolean propertiesFileIsComplete = true;
      String warningMessage = "";
      String jdbcDriver = prop.getProperty("jdbcDriver");
      if (jdbcDriver == null || jdbcDriver.length() == 0) {
        propertiesFileIsComplete = false;
        warningMessage += ", jdbcDriver property missing";
      }
      String jdbcUrl = prop.getProperty("jdbcUrl");
      if (jdbcUrl == null || jdbcUrl.length() == 0) {
        propertiesFileIsComplete = false;
        warningMessage += ", jdbcUrl property missing";
      }
      String dbUserName = prop.getProperty("dbUserName");
      if (dbUserName == null || dbUserName.length() == 0) {
        propertiesFileIsComplete = false;
        warningMessage += ", dbUserName property missing";
      }
      String dbPassword = prop.getProperty("dbPassword");
      if (dbPassword == null || dbPassword.length() == 0) {
        propertiesFileIsComplete = false;
        warningMessage += ", dbPassword property missing";
      }

      if (propertiesFileIsComplete) {
        ds =
            new PooledDataSource(
                Thread.currentThread().getContextClassLoader(),
                jdbcDriver,
                jdbcUrl,
                dbUserName,
                dbPassword);
        ((PooledDataSource) ds)
            .forceCloseAll(); // otherwise the MyBatis pool is not initialized correctly
      } else {
        LOGGER.warn("propertiesFile " + propertiesFileName + " is incomplete" + warningMessage);
        LOGGER.warn("Using default Datasource for Test");
        ds = createDefaultDataSource();
      }

    } catch (IOException e) {
      LOGGER.warn("createDataSourceFromProperties caught Exception " + e);
      LOGGER.warn("Using default Datasource for Test");
      ds = createDefaultDataSource();
    }

    return ds;
  }

  static String getSchemaNameFromPropertiesObject(String propertiesFileName) {
    String schemaName = "TASKANA";
    try (InputStream input = new FileInputStream(propertiesFileName)) {
      Properties prop = new Properties();
      prop.load(input);
      boolean propertiesFileIsComplete = true;
      String warningMessage = "";
      schemaName = prop.getProperty("schemaName");
      if (schemaName == null || schemaName.length() == 0) {
        propertiesFileIsComplete = false;
        warningMessage += ", schemaName property missing";
      }

      if (!propertiesFileIsComplete) {
        LOGGER.warn("propertiesFile " + propertiesFileName + " is incomplete" + warningMessage);
        LOGGER.warn("Using default Datasource for Test");
        schemaName = "TASKANA";
      }

    } catch (FileNotFoundException e) {
      LOGGER.warn("getSchemaNameFromPropertiesObject caught Exception " + e);
      LOGGER.warn("Using default schemaName for Test");
    } catch (IOException e) {
      LOGGER.warn("createDataSourceFromProperties caught Exception " + e);
      LOGGER.warn("Using default Datasource for Test");
    }

    return schemaName;
  }

  private static DataSource createDataSource(JdbcDatabaseContainer<?> container) {
    String jdbcDriver = container.getDriverClassName();
    String jdbcUrl = container.getJdbcUrl();
    String dbUserName = container.getUsername();
    String dbPassword = container.getPassword();
    PooledDataSource ds =
        new PooledDataSource(
            Thread.currentThread().getContextClassLoader(),
            jdbcDriver,
            jdbcUrl,
            dbUserName,
            dbPassword);
    ds.setPoolTimeToWait(POOL_TIME_TO_WAIT);
    ds.forceCloseAll(); // otherwise the MyBatis pool is not initialized correctly
    // TODO: fix this :)
    //    if(container.getDatabaseName().equals("postgres")) {
    //      schemaName = "taskana";
    //    } else {
    //      schemaName = "TASKANA";
    //    }
    return ds;
  }

  /**
   * create Default Datasource for in-memory database.
   *
   * @return the default datasource.
   */
  private static DataSource createDefaultDataSource() {
    // JdbcDataSource ds = new JdbcDataSource();
    // ds.setURL("jdbc:h2:mem:taskana;IGNORECASE=TRUE;LOCK_MODE=0");
    // ds.setPassword("sa");
    // ds.setUser("sa");

    String jdbcDriver = "org.testcontainers.jdbc.ContainerDatabaseDriver";
    String jdbcUrl = "jdbc:tc:postgresql:10:///taskana";
    String dbUserName = "postgres";
    String dbPassword = "postgres";
    PooledDataSource ds =
        new PooledDataSource(
            Thread.currentThread().getContextClassLoader(),
            jdbcDriver,
            jdbcUrl,
            dbUserName,
            dbPassword);
    ds.setPoolTimeToWait(POOL_TIME_TO_WAIT);
    ds.forceCloseAll(); // otherwise the MyBatis pool is not initialized correctly

    return ds;
  }
}
