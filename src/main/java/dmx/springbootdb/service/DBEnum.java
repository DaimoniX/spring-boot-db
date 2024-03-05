package dmx.springbootdb.service;

import dmx.springbootdb.router.DatabaseEnum;

public enum DBEnum {
    MASTER, SLAVE;

    public static DatabaseEnum getDatabaseEnum(DBEnum mode) {
        return switch (mode) {
            case MASTER -> DatabaseEnum.DATABASE_A;
            case SLAVE -> DatabaseEnum.DATABASE_B;
        };
    }
}
