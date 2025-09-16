import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

enum PackageSize {
    SMALL, MEDIUM, LARGE
}

enum LockerStatus {
    AVAILABLE, OCCUPIED, OUT_OF_SERVICE
}

interface Pack {
    String getPackageId();
    PackageSize getPackageSize();
}

class SmallPackage implements Pack {
    private final String packageId;

    public SmallPackage() {
        packageId = UUID.randomUUID().toString();
    }

    @Override
    public String getPackageId() { return packageId; }

    @Override
    public PackageSize getPackageSize() { return PackageSize.SMALL; }
}

class Locker {
    private final String lockerId;
    private final PackageSize lockerSize;
    private LockerStatus lockerStatus;
    private Pack pack;
    private final ReentrantLock lock = new ReentrantLock();

    public Locker(PackageSize size) {
        lockerId = UUID.randomUUID().toString();
        lockerSize = size;
        lockerStatus = LockerStatus.AVAILABLE;
    }

    public String getLockerId() { return lockerId; }
    public PackageSize getLockerSize() { return lockerSize; }
    public LockerStatus getLockerStatus() { return lockerStatus; }
    public Pack getPack() { return pack; }

    public boolean occupyLocker(Pack pack) {
        lock.lock();
        try {
            if (lockerStatus == LockerStatus.AVAILABLE) {
                this.pack = pack;
                lockerStatus = LockerStatus.OCCUPIED;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean releaseLocker() {
        lock.lock();
        try {
            if (lockerStatus == LockerStatus.OCCUPIED) {
                this.pack = null;
                lockerStatus = LockerStatus.AVAILABLE;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}

class LockerManager {
    private final List<Locker> lockerList = new ArrayList<>();
    private final Map<String, Locker> pinToLockerMap = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public LockerManager(List<Locker> lockers) {
        lockerList.addAll(lockers);
    }

    private boolean fit(PackageSize lockerSize, PackageSize packSize) {
        if (lockerSize == PackageSize.LARGE) return true;
        if (lockerSize == PackageSize.MEDIUM && (packSize == PackageSize.MEDIUM || packSize == PackageSize.SMALL)) return true;
        return lockerSize == PackageSize.SMALL && packSize == PackageSize.SMALL;
    }

    private String generateUniquePin() {
        String pin;
        do {
            pin = String.format("%06d", random.nextInt(1000000));
        } while (pinToLockerMap.containsKey(pin));
        return pin;
    }

    public String allocate(Pack pack) {
        for (Locker locker : lockerList) {
            if (locker.getLockerStatus() == LockerStatus.AVAILABLE && fit(locker.getLockerSize(), pack.getPackageSize())) {
                if (locker.occupyLocker(pack)) {
                    String pin = generateUniquePin();
                    pinToLockerMap.put(pin, locker);
                    return pin;
                }
            }
        }
        return null; // no available locker
    }

    public Pack fetch(String pin) {
        Locker locker = pinToLockerMap.remove(pin);
        if (locker != null) {
            Pack pack = locker.getPack();
            locker.releaseLocker();
            return pack;
        }
        return null;
    }
}

public class AmazonLocker {
    public static void main(String[] args) {
        List<Locker> lockers = new ArrayList<>();
        lockers.add(new Locker(PackageSize.SMALL));
        lockers.add(new Locker(PackageSize.MEDIUM));
        lockers.add(new Locker(PackageSize.LARGE));

        LockerManager manager = new LockerManager(lockers);

        Pack smallPack = new SmallPackage();
        Pack mediumPack = new Pack() {  // 匿名类，模拟中号包
            private final String id = UUID.randomUUID().toString();
            @Override
            public String getPackageId() { return id; }
            @Override
            public PackageSize getPackageSize() { return PackageSize.MEDIUM; }
        };
        Pack largePack = new Pack() {  // 匿名类，模拟大号包
            private final String id = UUID.randomUUID().toString();
            @Override
            public String getPackageId() { return id; }
            @Override
            public PackageSize getPackageSize() { return PackageSize.LARGE; }
        };

        String pinSmall = manager.allocate(smallPack);
        String pinMedium = manager.allocate(mediumPack);
        String pinLarge = manager.allocate(largePack);

        System.out.println("Allocated PINs:");
        System.out.println("Small pack PIN: " + pinSmall);
        System.out.println("Medium pack PIN: " + pinMedium);
        System.out.println("Large pack PIN: " + pinLarge);

        Pack fetchedSmall = manager.fetch(pinSmall);
        Pack fetchedMedium = manager.fetch(pinMedium);
        Pack fetchedLarge = manager.fetch(pinLarge);

        System.out.println("\nFetched packages:");
        System.out.println("Small pack ID: " + (fetchedSmall != null ? fetchedSmall.getPackageId() : "null"));
        System.out.println("Medium pack ID: " + (fetchedMedium != null ? fetchedMedium.getPackageId() : "null"));
        System.out.println("Large pack ID: " + (fetchedLarge != null ? fetchedLarge.getPackageId() : "null"));

        String pinSmall2 = manager.allocate(smallPack);
        System.out.println("\nRe-allocate small pack, new PIN: " + pinSmall2);
    }
}