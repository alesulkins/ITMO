package enterprises;

import shorties.Shorty;

import java.time.LocalTime;
import java.util.List;

public class DepressingEstablishment extends Institution {

    public DepressingEstablishment(String name, int budget, List<Service> services, LocalTime openingTime, LocalTime closingTime) {
        super(name, budget, services, openingTime, closingTime);
    }

    @Override
    boolean checkWorker(Shorty shorty) {
        return shorty.getAge() > 30;
    }
}
