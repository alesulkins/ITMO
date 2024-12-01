package enterprises;

import interfaces.ToPay;
import interfaces.ToWork;
import shorties.Emotions;
import shorties.Shorty;

import java.time.LocalTime;
import java.util.ArrayList;

public abstract class Institution implements ToWork, ToPay {
    private String name;
    private Service service;
    private Shorty employee;
    private LocalTime openingTime;
    private LocalTime closingTime;


    public Institution(String name, LocalTime openingTime, LocalTime closingTime) {
        this.name = name;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public boolean provisionOfService(Shorty shorty) throws NotEnoughMoneyException {
        LocalTime now = LocalTime.now();
        if (now.isAfter(openingTime) && now.isBefore(closingTime)) {
            if (shorty.getMoney() < service.price()) {
                throw new NotEnoughMoneyException(shorty.getName(), service.price() - shorty.getMoney(), service);
            } else {
                service.doService(shorty);
                return service.makeService(employee);
            }
        } else {
            System.out.println("Sorry, " + name + " is closed.");
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
        if (checkWorker(shorty)){
            System.out.println(name + " принимает на работу " + shorty.getName());
            employee = shorty;
            return true;
        } else {
            System.out.println(name + " не принимает на работу " + shorty.getName());
            return false;
        }
    }

    abstract boolean checkWorker(Shorty shorty);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
