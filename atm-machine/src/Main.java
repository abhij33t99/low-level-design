import context.ATMContext;
import enums.State;
import factory.AtmStateFactory;
import model.Account;
import model.Card;
import repository.AtmRepository;
import repository.IAtmRespository;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        IAtmRespository atmRepository = new AtmRepository();
        atmRepository.addCash(Arrays.asList("2000:5", "1000:5", "500:10", "100:20"));

        Scanner scanner = new Scanner(System.in);
        ATMContext atmContext = new ATMContext(atmRepository, scanner);

        // Hardcoded card and account for demonstration
        Account account = new Account("John Doe", 12345L, 10000);
        Card card = new Card(account, 123);

        atmContext.setState(AtmStateFactory.getState(State.NEW));

        while (true) {
            System.out.println("\nATM Menu:");
            System.out.println("1. Insert Card");
            System.out.println("2. Enter PIN");
            System.out.println("3. Execute Transaction (Withdraw/Check Balance)");
            System.out.println("4. Eject Card");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = -1;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    atmContext.insertCard(card);
                    break;
                case 2:
                    atmContext.enterPin();
                    break;
                case 3:
                    atmContext.executeTransaction();
                    break;
                case 4:
                    atmContext.ejectCard();
                    break;
                case 5:
                    System.out.println("Exiting. Thank you for using the ATM.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
