package com.example.sampletravelapp.db;

import android.content.Context;

import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.sampletravelapp.AppExecutors;
import com.example.sampletravelapp.Model.Bus;
import com.example.sampletravelapp.Model.BusAttributes;
import com.example.sampletravelapp.Model.Journey;
import com.example.sampletravelapp.Model.OrderItem;
import com.example.sampletravelapp.Model.Place;
import com.example.sampletravelapp.db.Convertors.DateConverter;
import com.example.sampletravelapp.db.Convertors.JourneyBusPlaceConvertor;
import com.example.sampletravelapp.db.dao.BusAttributeDao;
import com.example.sampletravelapp.db.dao.BusDao;
import com.example.sampletravelapp.db.dao.JournayDao;
import com.example.sampletravelapp.db.dao.OrderDao;
import com.example.sampletravelapp.db.dao.PlaceDao;

@Database(entities = {OrderItem.class, Journey.class, Bus.class, Place.class, BusAttributes.class}, version = 2)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;

    @VisibleForTesting
    public static final String DATABASE_NAME = "basic-sample-db";

    public abstract OrderDao orderDao();

    public abstract JournayDao journayDao();

    public abstract BusDao busDao();

    public abstract PlaceDao placeDao();

    public abstract BusAttributeDao busAttributeDao();

    public static AppDatabase getInstance(final Context context, final AppExecutors executors) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    /**
     * Build the database. {@link Builder#build()} only sets up the database configuration and
     * creates a new instance of the database.
     * The SQLite database is only created when it's accessed for the first time.
     */
    private static AppDatabase buildDatabase(final Context appContext) {
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
//                .addMigrations(MIGRATION_5_6)
                .build();
    }

//    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
//
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `itemsFts` USING FTS4("
//                    + "`id` TEXT, `name` TEXT, content=`items`)");
//            database.execSQL("INSERT INTO itemsFts (`id`, `name`) "
//                    + "SELECT `id`, `name`, `description` FROM items");
//
//        }
//    };

}