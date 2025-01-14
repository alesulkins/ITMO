import enterprises.*;
import shorties.Emotions;
import shorties.Shorty;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        Service service1 = new Service("Стендап Сергея Орлова", 5, "Новая программа", "получает удовольствие и смеется от шуток комика");
        Service service2 = new Service("попади в коротышку", 10, "кидает мяч ", "уворачивается от мяча ");
        List<Service> services = new ArrayList<>();
        services.add(service1);
        services.add(service2);


        EntertainmentEstablishment e1 = new EntertainmentEstablishment("Веселый Балаганчик", 50, services, LocalTime.of(11, 30), LocalTime.of(13, 30));
        DepressingEstablishment e3 = new DepressingEstablishment("ITMO", 50, services, LocalTime.of(12, 30), LocalTime.of(14, 30));


        Street street = new Street("Кронвекский проспект");
        street.getInstitutions().add(e1);
        street.getInstitutions().add(e3);

        Shorty shorty1 = new Shorty("Незнайка", 15, 10, Emotions.NEUTRAL);//незнайка
        Shorty shorty2 = new Shorty("Козлик", 14, 20, Emotions.NEUTRAL);//козлик
        Shorty shorty3 = new Shorty("первак", 52, 10, Emotions.NEUTRAL);//коротышка
        Shorty shorty4 = new Shorty("Сергей Клименков", 66, 100, Emotions.NEUTRAL);

        e3.toHire(shorty3);

        if (!e1.isWorking() || !e3.isWorking()) {
            shorty2.sayPhrase("ну ничего, скоро откроется");
        }

        while (shorty4.getMoney() > 0) {
            Service selectedService = services.get(0); // Сергей выбирает первую услугу
            if (!shorty4.tryToVisitInstitution(e3, selectedService)) {
                System.out.println("Посещение завершилось неудачей.");
                break;
            }
        }

        e3.employeeLeavePlace();

        shorty1.findWork(street);
        System.out.println("-----------------------------------");

        shorty1.changeEmotion(Emotions.HAPPY);
        shorty2.changeEmotion(Emotions.HURT);
        shorty1.tryToVisitInstitution(e1, service2);

        shorty2.tryToVisitInstitution(e3, service1);

        shorty3.tryToVisitInstitution(e3, service2);

        System.out.println("\n--- Конец симуляции ---");
    }
}