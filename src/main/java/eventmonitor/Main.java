package eventmonitor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final EventMonitor MONITOR = new EventMonitor();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            showMenu();
            String option = SCANNER.nextLine().trim();
            try {
                switch (option) {
                    case "1" -> registerSpace();
                    case "2" -> listSpaces();
                    case "3" -> createReservation();
                    case "4" -> checkAvailability();
                    case "5" -> showHistory();
                    case "6" -> sortSpaces();
                    case "7" -> mergeSpaces();
                    case "8" -> System.out.println("Total de reservas: " + MONITOR.countReservations());
                    case "0" -> running = false;
                    default -> System.out.println("Opción no válida.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println();
        }
        SCANNER.close();
        System.out.println("Programa finalizado.");
    }

    private static void showMenu() {
        System.out.println("=== EventMonitor UNAL ===");
        System.out.println("1. Registrar espacio");
        System.out.println("2. Listar espacios");
        System.out.println("3. Crear reserva");
        System.out.println("4. Consultar disponibilidad actual");
        System.out.println("5. Historial cronológico");
        System.out.println("6. Ordenar espacios por reservas");
        System.out.println("7. Combinar espacios");
        System.out.println("8. Contar reservas");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void registerSpace() {
        System.out.print("ID del espacio: ");
        String id = SCANNER.nextLine().trim();
        System.out.println("Tipos: SALON, AUDITORIO, CANCHA, LABORATORIO, OTRO");
        System.out.print("Tipo: ");
        SpaceType type = SpaceType.valueOf(SCANNER.nextLine().trim().toUpperCase());
        System.out.println(MONITOR.addSpace(id, type)
                ? "Espacio registrado correctamente." : "No se pudo registrar el espacio.");
    }

    private static void listSpaces() {
        List<Space> spaces = MONITOR.listSpaces();
        if (spaces.isEmpty()) {
            System.out.println("No hay espacios registrados.");
            return;
        }
        for (Space space : spaces) System.out.println(space);
    }

    private static void createReservation() {
        System.out.print("ID del espacio: ");
        String spaceId = SCANNER.nextLine().trim();
        LocalDateTime start = readDateTime("Inicio (yyyy-MM-dd HH:mm): ");
        LocalDateTime end = readDateTime("Fin (yyyy-MM-dd HH:mm): ");
        System.out.println(MONITOR.reserve(spaceId, start, end)
                ? "Reserva creada correctamente."
                : "Reserva rechazada. Verifique espacio, fechas y conflictos.");
    }

    private static LocalDateTime readDateTime(String prompt) {
        System.out.print(prompt);
        try {
            return LocalDateTime.parse(SCANNER.nextLine().trim(), FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Use el formato yyyy-MM-dd HH:mm.");
        }
    }

    private static void checkAvailability() {
        System.out.print("ID del espacio: ");
        String id = SCANNER.nextLine().trim();
        if (MONITOR.getSpace(id) == null) System.out.println("El espacio no existe.");
        else System.out.println(MONITOR.isAvailableNow(id)
                ? "El espacio está disponible." : "El espacio está ocupado.");
    }

    private static void showHistory() {
        List<Reservation> history = MONITOR.getChronologicalHistory();
        if (history.isEmpty()) {
            System.out.println("No hay reservas registradas.");
            return;
        }
        for (Reservation reservation : history) System.out.println(reservation);
    }

    private static void sortSpaces() {
        for (Space space : MONITOR.listSpacesByReservationCount()) System.out.println(space);
    }

    private static void mergeSpaces() {
        System.out.print("ID del primer espacio: ");
        String first = SCANNER.nextLine().trim();
        System.out.print("ID del segundo espacio: ");
        String second = SCANNER.nextLine().trim();
        System.out.print("ID del espacio combinado: ");
        String merged = SCANNER.nextLine().trim();
        System.out.println(MONITOR.mergeSpaces(first, second, merged)
                ? "Espacios combinados correctamente."
                : "No se pudieron combinar los espacios.");
    }
}
