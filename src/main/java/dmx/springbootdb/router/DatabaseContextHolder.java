package dmx.springbootdb.router;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DatabaseContextHolder {
    private static final ThreadLocal<DatabaseEnum> CONTEXT = new ThreadLocal<>();

    public void setContext(DatabaseEnum databaseEnum) {
        CONTEXT.set(databaseEnum);
    }

    public DatabaseEnum getContext() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
