package eventmonitor;

import java.time.LocalDateTime;

public class Reservation {
    private final String id;
    private final String spaceId;
    private final LocalDateTime start;
    private final LocalDateTime end;

    public Reservation(String id, String spaceId, LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("Intervalo de reserva no válido.");
        }
        this.id = id;
        this.spaceId = spaceId;
        this.start = start;
        this.end = end;
    }

    public String getId() { return id; }
    public String getSpaceId() { return spaceId; }
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }

    public boolean overlaps(LocalDateTime otherStart, LocalDateTime otherEnd) {
        return start.isBefore(otherEnd) && otherStart.isBefore(end);
    }

    public boolean activeAt(LocalDateTime moment) {
        return !moment.isBefore(start) && moment.isBefore(end);
    }

    @Override
    public String toString() {
        return "Reserva: " + id + " | Espacio: " + spaceId
                + " | Inicio: " + start + " | Fin: " + end;
    }
}
