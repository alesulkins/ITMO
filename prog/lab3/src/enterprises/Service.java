package enterprises;

import shorties.Shorty;

import java.util.Random;

public record Service(String name, int price, String action, String actionForEmployee) {

    public void doService(Shorty shorty){
        System.out.println(shorty.getName() + " " + action);
    }

    // наблюдать за короткышкин экшн
    public boolean makeService(Shorty shorty){
        Random random = new Random();
        if (random.nextDouble(0,1) > 0.5) {
            System.out.println(shorty.getName() + " " + actionForEmployee);
            return true;
        } else {
            System.out.println(shorty.getName() + " не " + actionForEmployee);
            shorty.addInjury(shorty.bodyParts.get(random.nextInt(0,shorty.bodyParts.size())), "синяк");
            return false;
        }
    }
}
