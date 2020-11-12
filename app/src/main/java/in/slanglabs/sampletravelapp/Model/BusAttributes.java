package in.slanglabs.sampletravelapp.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "busAttributes",
        foreignKeys = @ForeignKey(entity = Bus.class,
        parentColumns = "id",
        childColumns = "busId",
        onDelete = ForeignKey.CASCADE),
        primaryKeys = {"busId","travelClass"})
public class BusAttributes {
    @NonNull
    public String busId;
    @NonNull
    public String travelClass;
}
