package enterprises;

import shorties.Shorty;

import java.time.LocalTime;
import java.util.List;

public class EntertainmentEstablishment extends Institution {


    public EntertainmentEstablishment(String name, int budget, List<Service> services, LocalTime openingTime, LocalTime closingTime) {
        super(name, budget, services, openingTime, closingTime);
    }

    @Override
    boolean checkWorker(Shorty shorty) {
        return shorty.getAge() > 20;
    }
}