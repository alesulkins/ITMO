package exception;

import enterprises.Service;

public class NotEnoughMoneyException extends Exception {
    public NotEnoughMoneyException(String name, int count, Service service) {
        super(name + " не хватает " + count + " чтобы заплатить за " + service.name());
    }
}
