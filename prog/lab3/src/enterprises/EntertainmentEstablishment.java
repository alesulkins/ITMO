package enterprises;

import shorties.Shorty;

import java.time.LocalTime;

public class EntertainmentEstablishment extends Institution{
    public EntertainmentEstablishment(String name) {
        super(name, LocalTime.of(12,30), LocalTime.of(19,30));
    }

    @Override
    boolean checkWorker(Shorty shorty) {
        return shorty.getAge()>20;
    }
}