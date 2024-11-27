import enterprises.*;
import shorties.Emotions;
import shorties.Shorty;

import java.time.LocalTime;

public class Main {
    public static void main(String[] args) {
        EntertainmentEstablishment e1 = new EntertainmentEstablishment("Веселый Балаганчик");
        DepressingEstablishment e2 = new DepressingEstablishment("Депрессивный Балаганчик");
        EntertainmentEstablishment e3 = new EntertainmentEstablishment("новое заведение");

        e1.setOpeningTime(LocalTime.of(16,30));
        e1.setClosingTime(LocalTime.of(22,30));

        Service service = new Service("Стендап Сергея Орлова", 5,"Новая программа","получает удовольствие и смеется от шуток комика");
        e1.setService(service);

        Service service3 = new Service("попади в коротышку", 10,"кидает мяч ", "уворачивается от мяча ");
        e3.setService(service3);

        Street street = new Street("Невский проспект");
        street.getInstitutions().add(e1);
        street.getInstitutions().add(e2);

        Shorty shorty1 = new Shorty("Незнайка", 15, 10, Emotions.NEUTRAL);
        Shorty shorty2 = new Shorty("Козлик", 14, 20, Emotions.NEUTRAL);
        Shorty shorty3 = new Shorty("коротышка", 52, 10, Emotions.NEUTRAL);
        Shorty shorty4 = new Shorty("Rich", 66, 100, Emotions.NEUTRAL);
        e3.toHire(shorty3);

        if (!e1.isWorking()){
            shorty2.sayPhrase("ну ничего, скоро откроется");
        }
        while(shorty4.getMoney()>0){
            if (!shorty4.goToInstitution(e3)){
                break;
            }
        }

        e3.employeeLeavePlace();


        shorty1.findWork(street);
        System.out.println("-----------------------------------");
        shorty1.goToInstitution(e1);
        shorty2.goToInstitution(e3);



    }
}