package eventmonitor;

import java.time.LocalDateTime;
import java.util.*;

public class EventMonitor {
    private final Map<String, Space> spaces = new HashMap<>();
    private final TreeMap<LocalDateTime, List<Reservation>> reservations = new TreeMap<>();

    public boolean addSpace(String id, SpaceType type) {
        if (id == null || id.isBlank() || type == null || spaces.containsKey(id)) return false;
        spaces.put(id, new Space(id, type));
        return true;
    }

    public Space getSpace(String id) { return spaces.get(id); }
    public List<Space> listSpaces() { return new ArrayList<>(spaces.values()); }

    public List<Space> listSpacesByReservationCount() {
        List<Space> result = listSpaces();
        result.sort(Comparator.comparingInt(Space::getReservationCount).reversed()
                .thenComparing(Space::getId));
        return result;
    }

    public boolean reserve(String spaceId, LocalDateTime start, LocalDateTime end) {
        Space space = spaces.get(spaceId);
        if (space == null || start == null || end == null
                || !start.isAfter(LocalDateTime.now()) || !start.isBefore(end)) return false;

        for (List<Reservation> group : reservations.values()) {
            for (Reservation existing : group) {
                Space existingSpace = spaces.get(existing.getSpaceId());
                if (existingSpace != null
                        && sharesResource(space.getResourceIds(), existingSpace.getResourceIds())
                        && existing.overlaps(start, end)) return false;
            }
        }

        Reservation reservation = new Reservation(UUID.randomUUID().toString(), spaceId, start, end);
        reservations.computeIfAbsent(start, key -> new ArrayList<>()).add(reservation);
        space.incrementReservationCount();
        return true;
    }

    private boolean sharesResource(Set<String> first, Set<String> second) {
        for (String resource : first) if (second.contains(resource)) return true;
        return false;
    }

    public boolean isAvailableNow(String spaceId) {
        Space space = spaces.get(spaceId);
        if (space == null) return false;
        LocalDateTime now = LocalDateTime.now();

        for (List<Reservation> group : reservations.values()) {
            for (Reservation reservation : group) {
                Space reservedSpace = spaces.get(reservation.getSpaceId());
                if (reservedSpace != null
                        && sharesResource(space.getResourceIds(), reservedSpace.getResourceIds())
                        && reservation.activeAt(now)) return false;
            }
        }
        return true;
    }

    public List<Reservation> getChronologicalHistory() {
        List<Reservation> result = new ArrayList<>();
        for (List<Reservation> group : reservations.values()) result.addAll(group);
        result.sort(Comparator.comparing(Reservation::getStart).thenComparing(Reservation::getId));
        return result;
    }

    public boolean mergeSpaces(String firstId, String secondId, String mergedId) {
        Space first = spaces.get(firstId);
        Space second = spaces.get(secondId);
        if (first == null || second == null || firstId.equals(secondId)
                || mergedId == null || mergedId.isBlank() || spaces.containsKey(mergedId)) return false;
        if (first.getType() != second.getType()
                || !isAvailableNow(firstId) || !isAvailableNow(secondId)) return false;

        Space merged = new Space(mergedId, first.getType());
        Set<String> resources = new HashSet<>(first.getResourceIds());
        resources.addAll(second.getResourceIds());
        merged.addResources(resources);
        spaces.put(mergedId, merged);
        return true;
    }

    public int countReservations() {
        int total = 0;
        for (List<Reservation> group : reservations.values()) total += group.size();
        return total;
    }
}
