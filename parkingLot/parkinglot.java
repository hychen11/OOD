import java.util.*;
import java.math.BigDecimal;
import java.time.*;

// ===== Vehicle =====
public interface Vehicle {
    String getLicensePlate();

    VehicleSize getSize();
}

public class Motorcycle implements Vehicle {
    private String licensePlate;

    public Motorcycle(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    @Override
    public String getLicensePlate() {
        return licensePlate;
    }

    @Override
    public VehicleSize getSize() {
        return VehicleSize.SMALL;
    }
}

public class Car implements Vehicle {
    private String licensePlate;

    public Car(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    @Override
    public String getLicensePlate() {
        return licensePlate;
    }

    @Override
    public VehicleSize getSize() {
        return VehicleSize.MEDIUM;
    }
}

public class Truck implements Vehicle {
    private String licensePlate;

    public Truck(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    @Override
    public String getLicensePlate() {
        return licensePlate;
    }

    @Override
    public VehicleSize getSize() {
        return VehicleSize.LARGE;
    }
}

public enum VehicleSize {SMALL, MEDIUM, LARGE}

// ===== ParkingSpot =====
public interface ParkingSpot {
    boolean isAvailable();

    void park(Vehicle vehicle);

    void vacate();

    int getSpotNumber();

    VehicleSize getSize();
}

class CompactSpot implements ParkingSpot {
    private int spotNumber;
    private Vehicle vehicle;

    public CompactSpot(int spotNumber) {
        this.spotNumber = spotNumber;
        this.vehicle = null;
    }

    @Override
    public boolean isAvailable() {
        return vehicle == null;
    }

    @Override
    public void park(Vehicle vehicle) {
        if (isAvailable()) this.vehicle = vehicle;
        else throw new RuntimeException("CompactSpot already occupied");
    }

    @Override
    public void vacate() {
        vehicle = null;
    }

    @Override
    public int getSpotNumber() {
        return spotNumber;
    }

    @Override
    public VehicleSize getSize() {
        return VehicleSize.SMALL;
    }
}

class RegularSpot implements ParkingSpot {
    private int spotNumber;
    private Vehicle vehicle;

    public RegularSpot(int spotNumber) {
        this.spotNumber = spotNumber;
        this.vehicle = null;
    }

    @Override
    public boolean isAvailable() {
        return vehicle == null;
    }

    @Override
    public void park(Vehicle vehicle) {
        if (isAvailable()) this.vehicle = vehicle;
        else throw new RuntimeException("RegularSpot already occupied");
    }

    @Override
    public void vacate() {
        vehicle = null;
    }

    @Override
    public int getSpotNumber() {
        return spotNumber;
    }

    @Override
    public VehicleSize getSize() {
        return VehicleSize.MEDIUM;
    }
}

class LargeSpot implements ParkingSpot {
    private int spotNumber;
    private Vehicle vehicle;

    public LargeSpot(int spotNumber) {
        this.spotNumber = spotNumber;
        this.vehicle = null;
    }

    @Override
    public boolean isAvailable() {
        return vehicle == null;
    }

    @Override
    public void park(Vehicle vehicle) {
        if (isAvailable()) this.vehicle = vehicle;
        else throw new RuntimeException("LargeSpot already occupied");
    }

    @Override
    public void vacate() {
        vehicle = null;
    }

    @Override
    public int getSpotNumber() {
        return spotNumber;
    }

    @Override
    public VehicleSize getSize() {
        return VehicleSize.LARGE;
    }
}

// ===== ParkingManager =====
public class ParkingManager {
    private final Map<VehicleSize, List<ParkingSpot>> availableSpots;
    private final Map<Vehicle, ParkingSpot> vehicleToSpotMap;

    public ParkingManager(Map<VehicleSize, List<ParkingSpot>> availableSpots) {
        this.availableSpots = availableSpots;
        this.vehicleToSpotMap = new HashMap<>();
    }

    public ParkingSpot findSpotForVehicle(Vehicle vehicle) {
        VehicleSize vehicleSize = vehicle.getSize();
        for (VehicleSize size : VehicleSize.values()) {
            if (size.ordinal() >= vehicleSize.ordinal()) {
                List<ParkingSpot> spots = availableSpots.get(size);
                for (ParkingSpot spot : spots) {
                    if (spot.isAvailable()) return spot;
                }
            }
        }
        return null;
    }

    public ParkingSpot parkVehicle(Vehicle vehicle) {
        ParkingSpot spot = findSpotForVehicle(vehicle);
        if (spot != null) {
            spot.park(vehicle);
            vehicleToSpotMap.put(vehicle, spot);
            availableSpots.get(spot.getSize()).remove(spot);
            return spot;
        }
        return null;
    }

    public void unparkVehicle(Vehicle vehicle) {
        ParkingSpot spot = vehicleToSpotMap.remove(vehicle);
        if (spot != null) {
            spot.vacate();
            availableSpots.get(spot.getSize()).add(spot);
        }
    }
}

// ===== Ticket =====
public class Ticket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot parkingSpot;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public Ticket(String ticketId, Vehicle vehicle, ParkingSpot parkingSpot, LocalDateTime entryTime) {
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.parkingSpot = parkingSpot;
        this.entryTime = entryTime;
        this.exitTime = null;
    }

    public BigDecimal calculateParkingDuration() {
        return new BigDecimal(
                Duration.between(entryTime, exitTime != null ? exitTime : LocalDateTime.now()).toMinutes()
        );
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }
}

// ===== Fare Strategies =====
public interface FareStrategy {
    BigDecimal calculateFare(Ticket ticket, BigDecimal inputFare);
}

public class BaseFareStrategy implements FareStrategy {
    private static final BigDecimal SMALL_RATE = new BigDecimal("1.0");
    private static final BigDecimal MEDIUM_RATE = new BigDecimal("2.0");
    private static final BigDecimal LARGE_RATE = new BigDecimal("3.0");

    @Override
    public BigDecimal calculateFare(Ticket ticket, BigDecimal inputFare) {
        BigDecimal rate;
        switch (ticket.getVehicle().getSize()) {
            case MEDIUM:
                rate = MEDIUM_RATE;
                break;
            case LARGE:
                rate = LARGE_RATE;
                break;
            default:
                rate = SMALL_RATE;
        }
        return inputFare.add(rate.multiply(ticket.calculateParkingDuration()));
    }
}

public class PeakHoursFareStrategy implements FareStrategy {
    private static final BigDecimal MULTIPLIER = new BigDecimal("1.5");

    @Override
    public BigDecimal calculateFare(Ticket ticket, BigDecimal inputFare) {
        if (isPeakHours(ticket.getEntryTime())) {
            return inputFare.multiply(MULTIPLIER);
        }
        return inputFare;
    }

    private boolean isPeakHours(LocalDateTime time) {
        int hour = time.getHour();
        return (hour >= 7 && hour <= 10) || (hour >= 16 && hour <= 19);
    }
}

public class FareCalculator {
    private final List<FareStrategy> strategies;

    public FareCalculator(List<FareStrategy> strategies) {
        this.strategies = strategies;
    }

    public BigDecimal calculateFare(Ticket ticket) {
        BigDecimal fare = BigDecimal.ZERO;
        for (FareStrategy strategy : strategies) {
            fare = strategy.calculateFare(ticket, fare);
        }
        return fare;
    }
}

// ===== ParkingLot =====
public class ParkingLot {
    private final ParkingManager parkingManager;
    private final FareCalculator fareCalculator;

    public ParkingLot(ParkingManager parkingManager, FareCalculator fareCalculator) {
        this.parkingManager = parkingManager;
        this.fareCalculator = fareCalculator;
    }

    public Ticket enterVehicle(Vehicle vehicle) {
        ParkingSpot spot = parkingManager.parkVehicle(vehicle);
        if (spot != null) {
            return new Ticket(UUID.randomUUID().toString(), vehicle, spot, LocalDateTime.now());
        }
        return null;
    }

    public BigDecimal leaveVehicle(Ticket ticket) {
        if (ticket != null && ticket.getExitTime() == null) {
            ticket.setExitTime(LocalDateTime.now());
            parkingManager.unparkVehicle(ticket.getVehicle());
            return fareCalculator.calculateFare(ticket);
        }
        return BigDecimal.ZERO;
    }
}

// ====== Demo Main ======
class Main {
    public static void main(String[] args) throws InterruptedException {
        Map<VehicleSize, List<ParkingSpot>> spots = new HashMap<>();
        spots.put(VehicleSize.SMALL, new ArrayList<>(Arrays.asList(new CompactSpot(1), new CompactSpot(2))));
        spots.put(VehicleSize.MEDIUM, new ArrayList<>(Arrays.asList(new RegularSpot(3))));
        spots.put(VehicleSize.LARGE, new ArrayList<>(Arrays.asList(new LargeSpot(4))));

        ParkingManager manager = new ParkingManager(spots);
        FareCalculator calculator = new FareCalculator(Arrays.asList(new BaseFareStrategy(), new PeakHoursFareStrategy()));
        ParkingLot lot = new ParkingLot(manager, calculator);

        Vehicle car = new Car("ABC123");
        Ticket ticket = lot.enterVehicle(car);
        System.out.println("Car entered: " + ticket.getVehicle().getLicensePlate());

        Thread.sleep(2000); // 模拟停车 2 秒

        BigDecimal fare = lot.leaveVehicle(ticket);
        System.out.println("Fare: $" + fare);
    }
}
