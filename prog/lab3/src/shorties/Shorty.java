package shorties;

import enterprises.Institution;
import exception.NotEnoughMoneyException;
import enterprises.Service;
import enterprises.Street;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Shorty {
    private String name;
    private int age;
    private int money;
    private Emotions emotion = Emotions.NEUTRAL;
    public ArrayList<BodyPart> bodyParts = new ArrayList<>();
    private boolean isIll = false;
    private final ArrayList<String> injuries = new ArrayList<>();//not static

    public Shorty(String name, int age, int money, Emotions emotion) {
        this.name = name;
        this.age = age;
        this.money = money;
        bodyParts.addAll(List.of(new BodyPart[]{BodyPart.ARM, BodyPart.LEG, BodyPart.BACK, BodyPart.CHEST, BodyPart.HEAD}));
    }


    public void checkHealth(boolean resultOfService) {
        if (!resultOfService) {
            Random random = new Random();
            if (random.nextDouble() < 0.3) {
                isIll = true;
                //System.out.println(name + " заболел и не может работать");
            }
        }
    }

    public void addMoney(int amount) {
        this.money += amount;
        //System.out.println(amount + " рублей заработано. Теперь " + this.name + " имеет " + this.money + " рублей");
    }

    public void findWork(Street street) {
        //System.out.println(name + " отправился искать работу на улицу " + street.getName());
        for (Institution institution : street.getInstitutions()) {
            if (institution.toHire(this) && this.isIll) {
                //System.out.println(name + " смог устроиться на работы и был счастлив");
                return;
            }
        }
        System.out.println(name + " не смог устроиться на работу и был опечален");
    }

    public boolean checkInjury(String injuryToCheck) {
        for (String injury : injuries) {
            if (injury.equals(injuryToCheck)) {
                //System.out.println(name + " действительно имеет травму: " + injuryToCheck);
                return true;
            }
        }
        //System.out.println(name + " не имеет травмы: " + injuryToCheck);
        return false;
    }
    public void sayPhrase(String phrase) {//оставить + в зависимости от эмоции цвет фразы
        String colour;
        switch (emotion) {
            case HURT -> colour = "\u001B[31m";
            case HAPPY -> colour = "\u001B[35m";
            default -> colour = "\u001B[32m";
        }
        System.out.println(colour + this.name + " говорит: " + phrase + "\u001B[0m");
    }

    public void tryToVisitInstitution(Institution institution, Service service) {
        if (!institution.isWorking()) {
            sayPhrase("Ну ничего, скоро откроется!");
        } else {
            goToInstitution(institution, service);
        }
    }

    public boolean goToInstitution(Institution institution, Service service) {
        try {
            //System.out.println(name + " отправляется в " + institution.getName());
            return institution.provisionOfService(this, service);
        } catch (NotEnoughMoneyException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void checkWorkingTime(Institution institution) {
        if (!institution.isWorking()) {
            this.sayPhrase("ну ничего, скоро откроется");
        }
    }

    public void changeEmotion(Emotions newEmotion) {
        this.emotion = newEmotion;
        //System.out.println(name + " теперь " + emotion);
    }


    public void addInjury(BodyPart bodyPart, String injuryDescription) {
        injuries.add(injuryDescription + bodyPart);
        //System.out.println(name + " получил рану: " + injuryDescription + " на " + bodyPart);
        changeEmotion(Emotions.HURT);
    }


    public ArrayList<String> getInjuries() {
        return injuries;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public Emotions getEmotion() {
        return emotion;
    }

    public void setEmotion(Emotions emotion) {
        this.emotion = emotion;
    }

    public boolean isIll() {
        return isIll;
    }
}
