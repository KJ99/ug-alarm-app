package pl.aib.clockalarm.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "alarm", indices = {
        @Index(name = "al_key_unq", value = "key", unique = true)
})
public class Alarm {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;

    private String hour;

    private boolean active;

    private String key;

    @ColumnInfo(name = "broadcast_id")
    private int broadcastId = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Alarm && id == ((Alarm) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, hour, active);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getBroadcastId() {
        return broadcastId;
    }

    public void setBroadcastId(int broadcastId) {
        this.broadcastId = broadcastId;
    }
}
