package enterprises;

import interfaces.PaySalary;
import shorties.Emotions;
import shorties.Shorty;

import java.time.LocalTime;
import java.util.Random;

public abstract class Institution implements PaySalary {
    private String name;
    private int budget;
    private Service service;
    private Shorty employee;
    private LocalTime openingTime;
    private LocalTime closingTime;


    public Institution(String name, int budget, Service service, LocalTime openingTime, LocalTime closingTime) {
        this.name = name;
        this.budget = budget;
        this.service = service;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }


    abstract boolean checkWorker(Shorty shorty);


    public void doService(Shorty shorty){
        System.out.println(shorty.getName() + " " + service.action());
    }


    public void witnessShorty(Shorty shorty, Shorty employee){
        System.out.println(employee.getName() + " наблюдает за таем, как " + shorty.getName() + service.action());
    }

    public boolean makeService(Shorty shorty, Institution institution){
        Random random = new Random();
        boolean resultOfService = random.nextDouble(0,1) > 0.5;
        if (resultOfService) {
            int earnedMoney = random.nextInt(10,50);
            institution.paySalary(shorty, earnedMoney);
            shorty.changeEmotion(Emotions.HAPPY);
            System.out.println(shorty.getName() + " " + service.actionForEmployee());
        } else {
            System.out.println(shorty.getName() + " не " + service.actionForEmployee());
            shorty.addInjury(shorty.bodyParts.get(random.nextInt(0,shorty.bodyParts.size())), "синяк");
        }
        shorty.checkHealth(resultOfService);
        return  resultOfService;
    }



    public void checkWorkingTime(Shorty shorty) {
        if (!this.isWorking()){
            shorty.sayPhrase("ну ничего, скоро откроется");
        }
    }


    public void paySalary(Shorty shorty, int amount) {
        if (budget >= amount) {
            budget -= amount;
            shorty.addMoney(amount);
            System.out.println(name + " выплатил " + amount + " рублей " + shorty.getName());
        } else {
            System.out.println(name + " не может выплатить зарплату " + shorty.getName());
        }
    }



    public boolean provisionOfService(Shorty shorty) throws NotEnoughMoneyException {
        LocalTime now = LocalTime.now();
        if (now.isAfter(openingTime) && now.isBefore(closingTime)&&employee!=null) {
            if (shorty.getMoney() < service.price()) {
                throw new NotEnoughMoneyException(shorty.getName(), service.price() - shorty.getMoney(), service);
            } else {
                doService(shorty);
                return makeService(employee, this);
            }
        } else {
            System.out.println(name + " закрыт");
            return false;
        }
    }

    public boolean isWorking(){
        return LocalTime.now().isAfter(openingTime) && LocalTime.now().isBefore(closingTime);
    }

    public void employeeLeavePlace(){
        if (employee.getEmotion().equals(Emotions.HURT)){
            System.out.println(employee.getName() + " ушел из " + name + " со слезами на глазах");
        } else {
            System.out.println(employee.getName() + " ушел из " + name);
        }
        employee = null;
    }
    public boolean toHire(Shorty shorty){
        if (shorty.isIll()) {
            System.out.println(name + " не может нанять " + shorty.getName() + " из-за состояния его здоровья");
            return false;
        }
        if (checkWorker(shorty)){
            System.out.println(name + " принимает на работу " + shorty.getName());
            employee = shorty;
            return true;
        } else {
            System.out.println(name + " не принимает на работу " + shorty.getName());
            return false;
        }
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }
}
