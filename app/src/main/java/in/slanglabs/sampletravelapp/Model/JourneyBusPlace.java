package in.slanglabs.sampletravelapp.Model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class JourneyBusPlace {
    @Embedded
    public Journey journey;
    @Relation(
            parentColumn = "busId",
            entityColumn = "id",
            entity = Bus.class
    )
    public Bus bus;
    @Relation(
            parentColumn = "busId",
            entityColumn = "busId"
    )
    public List<BusAttributes> busAttributesList;
    @Relation(
            parentColumn = "startLocation",
            entityColumn = "id",
            entity = Place.class
    )
    public Place sourcePlace;
    @Relation(
            parentColumn = "endLocation",
            entityColumn = "id",
            entity = Place.class
    )
    public Place destinationPlace;
}
