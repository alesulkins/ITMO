package shorties;

import enterprises.Institution;
import enterprises.NotEnoughMoneyException;
import enterprises.Street;
import interfaces.ToPay;
import interfaces.ToWork;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Shorty implements ToWork, ToPay {
    String name;
    int age;
    int money;
    Emotions emotion;
    public ArrayList<BodyPart> bodyParts = new ArrayList<>();
    boolean isIll;//нельзя нанять нельзя работать

    private static ArrayList<String> injuries = new ArrayList<>();

    public Shorty(String name, int age, int money, Emotions emotion) {
        this.name = name;
        this.age = age;
        this.money = money;
        this.emotion = emotion;
        bodyParts.addAll(List.of(new BodyPart[]{BodyPart.ARM_LEFT, BodyPart.LEG_LEFT, BodyPart.ARM_LEFT, BodyPart.BACK, BodyPart.CHEST, BodyPart.HEAD, BodyPart.LEG_RIGHT}));
    }

    public void sayPhrase(String phrase){
        System.out.println(this.name + " говорит: " + phrase);
    }
    public void findWork(Street street){
        System.out.println(name + " отправился искать работу на улицу " + street.getName());
        for (Institution institution: street.getInstitutions()){
            if (institution.toHire(this)){
                System.out.println(name + " смог устроиться на работы и был счастлив");
                return;
            }
        }
        System.out.println(name + " не смог устроиться на работы и был опечален");
    }

    public boolean goToInstitution(Institution institution){
        try {
            System.out.println(name + " отправляется в " + institution.getName());
            return institution.provisionOfService(this);

        } catch (NotEnoughMoneyException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void changeEmotion(Emotions newEmotion) {
        this.emotion = newEmotion;
        System.out.println(name + " теперь " + emotion);
    }

    public void addInjury(BodyPart bodyPart, String injuryDescription) {
        injuries.add(injuryDescription + bodyPart);
        System.out.println(name + " получил рану: " + injuryDescription + " на " + bodyPart);

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
}
