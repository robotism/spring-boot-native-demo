package com.gankcode.springboot.db.migration;

import com.gankcode.springboot.db.migration.database.MysqlDatabase;
import com.gankcode.springboot.db.migration.database.TaosDatabase;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class DatabaseFactory {

    private static final List<AbstractDatabase> LIST = new ArrayList<>();

    static {
        register(new MysqlDatabase());
        register(new TaosDatabase());
    }


    public static synchronized void register(final AbstractDatabase database) {
        LIST.add(database);
    }


    public static synchronized AbstractDatabase findByUrl(final String type, final String url) {
        for (AbstractDatabase database : LIST) {
            final String driver = database.getDriver(type, url);
            if (!StringUtils.hasText(driver)) {
                continue;
            }
            try {
                Class.forName(driver);
                return database;
            } catch (Exception ignored) {

            }
        }
        return null;
    }
}
