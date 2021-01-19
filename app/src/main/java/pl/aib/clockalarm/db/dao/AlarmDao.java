package pl.aib.clockalarm.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.aib.clockalarm.db.entity.Alarm;

@Dao
public interface AlarmDao {

    @Query("select * from alarm")
    List<Alarm> findAll();

    @Query("select * from alarm where id = :id")
    Alarm findOne(int id);

    @Query("select * from alarm where `key` = :key")
    Alarm findOneByKey(String key);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Alarm ...alarms);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Alarm ...alarms);

    @Delete
    void delete(Alarm ...alarms);

}
