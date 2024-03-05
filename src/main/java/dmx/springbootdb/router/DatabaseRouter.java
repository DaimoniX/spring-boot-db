package dmx.springbootdb.router;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Component
public class DatabaseRouter extends AbstractRoutingDataSource {
    private final DatabaseAConfig dataSourceOneConfig;
    private final DatabaseBConfig dataSourceTwoConfig;
    private final DatabaseContextHolder dataSourceContextHolder;

    public DatabaseRouter(DatabaseContextHolder dataSourceContextHolder, DatabaseAConfig dataSourceOneConfig,
                          DatabaseBConfig dataSourceTwoConfig) {
        this.dataSourceOneConfig = dataSourceOneConfig;
        this.dataSourceTwoConfig = dataSourceTwoConfig;
        this.dataSourceContextHolder = dataSourceContextHolder;

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(DatabaseEnum.DATABASE_A, databaseASource());
        dataSourceMap.put(DatabaseEnum.DATABASE_B, databaseBSource());
        this.setTargetDataSources(dataSourceMap);
        this.setDefaultTargetDataSource(databaseASource());
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return dataSourceContextHolder.getContext();
    }

    public DataSource databaseASource() {
        var dataSource = new DriverManagerDataSource();
        dataSource.setUrl(dataSourceOneConfig.getUrl());
        dataSource.setUsername(dataSourceOneConfig.getUsername());
        dataSource.setPassword(dataSourceOneConfig.getPassword());
        return dataSource;
    }

    public DataSource databaseBSource() {
        var dataSource = new DriverManagerDataSource();
        dataSource.setUrl(dataSourceTwoConfig.getUrl());
        dataSource.setUsername(dataSourceTwoConfig.getUsername());
        dataSource.setPassword(dataSourceTwoConfig.getPassword());
        return dataSource;
    }
}
