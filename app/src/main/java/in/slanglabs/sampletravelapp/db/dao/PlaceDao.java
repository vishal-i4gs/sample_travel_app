package in.slanglabs.sampletravelapp.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import in.slanglabs.sampletravelapp.Model.Place;

@Dao
public interface PlaceDao {

    @Transaction
    @Query("SELECT * FROM place")
    LiveData<List<Place>> getAllPlaces();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Place> items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Place item);

    @Transaction
    @Query("SELECT * FROM place WHERE name LIKE '%' || :search || '%'")
    LiveData<List<Place>> getPlacesBasedOnSearch(String search);
}
