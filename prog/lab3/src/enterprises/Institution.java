package enterprises;

import exception.NotEnoughMoneyException;
import interfaces.PaySalary;
import shorties.Emotions;
import shorties.Shorty;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Institution implements PaySalary {
    private String name;
    private int budget;
    private List<Service> services;
    private Shorty employee;
    private LocalTime openingTime;
    private LocalTime closingTime;

    public Institution(String name, int budget, List<Service> services, LocalTime openingTime, LocalTime closingTime) {
        this.name = name;
        this.budget = budget;
        this.services = new ArrayList<>();
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    abstract boolean checkWorker(Shorty shorty);

    public void doService(Shorty shorty, Service service) {
        System.out.println(shorty.getName() + " " + service.action());
    }

    public boolean makeService(Shorty shorty, Service service) {
        Random random = new Random();
        boolean resultOfService = random.nextDouble(0, 1) > 0.5;

        if (resultOfService) {
            this.paySalary(shorty, service);
            shorty.changeEmotion(Emotions.HAPPY);
            System.out.println(shorty.getName() + " " + service.actionForEmployee());
        } else {
            System.out.println(shorty.getName() + " не " + service.actionForEmployee());
            shorty.addInjury(shorty.bodyParts.get(random.nextInt(0, shorty.bodyParts.size())), "синяк");
        }

        shorty.checkHealth(resultOfService);
        return resultOfService;
    }

    public void paySalary(Shorty shorty, Service service) {
        if (budget >= service.price()) {
            budget -= service.price();
            shorty.addMoney(service.price());
            System.out.println(name + " выплатил " + service.price() + " рублей " + shorty.getName());
        } else {
            System.out.println(name + " не может выплатить зарплату " + shorty.getName());
        }
    }


    public boolean provisionOfService(Shorty shorty, Service requestedService) throws NotEnoughMoneyException {
        if (!isWorking()) {
            System.out.println(getName() + " закрыт");
            return false;
        }

        Service service = null;
        for (Service s : getServices()) {
            if (s.name().equalsIgnoreCase(requestedService.name())) {
                service = s;
                break;
            }
        }

        if (service == null) {
            System.out.println("Услуга " + requestedService.name() + " не найдена в " + getName());
            return false;
        }

        if (shorty.getMoney() < service.price()) {
            throw new NotEnoughMoneyException(shorty.getName(), service.price() - shorty.getMoney(), service);
        }

        doService(shorty, service);
        return makeService(employee, service);
    }

    public boolean isWorking() {
        return LocalTime.now().isAfter(openingTime) && LocalTime.now().isBefore(closingTime);
    }

    public void employeeLeavePlace() {
        if (employee.getEmotion().equals(Emotions.HURT)) {
            System.out.println(employee.getName() + " ушел из " + name + " со слезами на глазах");
        } else {
            System.out.println(employee.getName() + " ушел из " + name);
        }
        employee = null;
    }

    public boolean toHire(Shorty shorty) {
        if (shorty.isIll()) {
            System.out.println(name + " не может нанять " + shorty.getName() + " из-за состояния его здоровья");
            return false;
        }

        if (checkWorker(shorty)) {
            System.out.println(name + " принимает на работу " + shorty.getName());
            employee = shorty;
            return true;
        } else {
            System.out.println(name + " не принимает на работу " + shorty.getName());
            return false;
        }
    }

    public void addService(Service service) {
        services.add(service);
    }

    public void removeService(Service service) {
        services.remove(service);
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

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
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