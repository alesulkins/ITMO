package enterprises;

import shorties.Shorty;

import java.time.LocalTime;

public class EntertainmentEstablishment extends Institution{


    public EntertainmentEstablishment(String name, int budget, Service service, LocalTime openingTime, LocalTime closingTime) {
        super(name, budget, service, openingTime, closingTime);
    }

    @Override
    boolean checkWorker(Shorty shorty) {
        return shorty.getAge()>20;
    }
}