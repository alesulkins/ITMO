import enterprises.*;
import shorties.Emotions;
import shorties.Shorty;

import java.time.LocalTime;

public class Main {
    public static void main(String[] args) {

        Service service = new Service("Стендап Сергея Орлова", 5,"Новая программа","получает удовольствие и смеется от шуток комика");
        Service service3 = new Service("попади в коротышку", 10,"кидает мяч ", "уворачивается от мяча ");

        EntertainmentEstablishment e1 = new EntertainmentEstablishment("Веселый Балаганчик", 50, service, LocalTime.of(12,30),LocalTime.of(19,30) );
        DepressingEstablishment e3 = new DepressingEstablishment("ITMO", 50, service3, LocalTime.of(12,30),LocalTime.of(19,30));


        Street street = new Street("Кронвекский проспект");
        street.getInstitutions().add(e1);
        street.getInstitutions().add(e3);

        Shorty shorty1 = new Shorty("Незнайка", 15, 10, Emotions.NEUTRAL);//незнайка
        Shorty shorty2 = new Shorty("Козлик", 14, 20, Emotions.NEUTRAL);//козлик
        Shorty shorty3 = new Shorty("первак", 52, 10, Emotions.NEUTRAL);//коротышка
        Shorty shorty4 = new Shorty("Сергей Клименков", 66, 100, Emotions.NEUTRAL);

        e3.toHire(shorty3);

        if (!e1.isWorking() || !e3.isWorking()){
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
        e1.checkWorkingTime(shorty1);

        shorty2.goToInstitution(e3);
        e3.checkWorkingTime(shorty2);

    }
}