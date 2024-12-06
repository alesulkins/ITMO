package enterprises;

import shorties.Shorty;

import java.time.LocalTime;

public class DepressingEstablishment extends Institution{

    public DepressingEstablishment(String name, int budget, Service service, LocalTime openingTime, LocalTime closingTime) {
        super(name, budget, service, openingTime, closingTime);
    }

    @Override
    boolean checkWorker(Shorty shorty) {
        return shorty.getAge()>30;
    }
}
