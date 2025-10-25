package cor;

import repository.IAtmRespository;

public class CashDispenserChainBuilder {
    public static CashDispenser buildChain(IAtmRespository respository) {
        CashDispenser c2000 = new Cash2000Dispenser(respository);
        CashDispenser c1000 = new Cash1000Dispenser(respository);
        CashDispenser c500 = new Cash500Dispenser(respository);
        CashDispenser c100 = new Cash100Dispenser(respository);
        c2000.setNext(c1000);
        c1000.setNext(c500);
        c500.setNext(c100);

        return c2000;
    }
}
