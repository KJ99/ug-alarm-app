package pl.aib.clockalarm.db;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import pl.aib.clockalarm.db.dao.AlarmDao;
import pl.aib.clockalarm.db.entity.Alarm;

@androidx.room.Database(entities = {Alarm.class}, version = 3)
public abstract class Database extends RoomDatabase {
    private static Database instance = null;
    private static final String DB_NAME = "alarm_db";

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("alter table alarm add column `key` varchar");
            database.execSQL("create unique index al_key_unq on alarm(`key`)");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("alter table alarm add column `broadcast_id` INTEGER not null default 0");
        }
    };

    public static Database getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context, Database.class, DB_NAME)
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build();
        }
        return instance;
    }

    public abstract AlarmDao alarmDao();
}
