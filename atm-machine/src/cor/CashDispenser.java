package cor;

import repository.IAtmRespository;

public abstract class CashDispenser {
    protected CashDispenser next;
    protected final IAtmRespository respository;

    protected CashDispenser(IAtmRespository respository) {
        this.respository = respository;
    }

    public void setNext(CashDispenser next) {
        this.next = next;
    }

    public abstract boolean canDispenseCash(int amount);

    public abstract void dispenseCash(int amount);
}
