package eventmonitor;

import java.util.HashSet;
import java.util.Set;

public class Space {
    private final String id;
    private final SpaceType type;
    private final Set<String> resourceIds = new HashSet<>();
    private int reservationCount;

    public Space(String id, SpaceType type) {
        this.id = id;
        this.type = type;
        resourceIds.add(id);
    }

    public String getId() { return id; }
    public SpaceType getType() { return type; }
    public Set<String> getResourceIds() { return new HashSet<>(resourceIds); }
    public int getReservationCount() { return reservationCount; }
    public void incrementReservationCount() { reservationCount++; }
    public void addResources(Set<String> resources) { resourceIds.addAll(resources); }

    @Override
    public String toString() {
        return "ID: " + id + " | Tipo: " + type
                + " | Reservas: " + reservationCount
                + " | Recursos: " + resourceIds;
    }
}
