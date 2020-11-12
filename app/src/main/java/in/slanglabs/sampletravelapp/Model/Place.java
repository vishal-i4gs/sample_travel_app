package in.slanglabs.sampletravelapp.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "place")
public class Place implements Serializable {

    @PrimaryKey
    @NonNull
    public String id;
    public String name;
}