package dmx.springbootdb.router;

import lombok.Getter;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import javax.sql.DataSource;
import java.util.HashMap;

@Getter
public class DatabaseRouter extends AbstractRoutingDataSource {
    private static final ThreadLocal<DatabaseEnum> CURRENT_DATASOURCE = new ThreadLocal<>();
    private final DataSource masterDataSource;
    private final DataSource slaveDataSource;

    public DatabaseRouter(DataSource masterDataSource, DataSource slaveDataSource) {
        this.masterDataSource = masterDataSource;
        this.slaveDataSource = slaveDataSource;

        final var dataSourceMap = new HashMap<>();
        dataSourceMap.put(DatabaseEnum.MASTER, masterDataSource);
        dataSourceMap.put(DatabaseEnum.SLAVE, slaveDataSource);

        super.setTargetDataSources(dataSourceMap);
        super.setDefaultTargetDataSource(masterDataSource);
    }

    public static boolean isReadonlyDataSource() {
        return CURRENT_DATASOURCE.get() == DatabaseEnum.SLAVE;
    }

    public static void setReadonlyDataSource(boolean isReadonly) {
        CURRENT_DATASOURCE.set(isReadonly ? DatabaseEnum.SLAVE : DatabaseEnum.MASTER);
    }

    public DatabaseEnum getContext() {
        return CURRENT_DATASOURCE.get();
    }

    public void setContext(DatabaseEnum databaseEnum) {
        CURRENT_DATASOURCE.set(databaseEnum);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return CURRENT_DATASOURCE.get();
    }

    public enum DatabaseEnum {
        MASTER, SLAVE
    }
}
