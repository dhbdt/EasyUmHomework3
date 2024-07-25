package org.example.refactored;

import org.example.entities.*;
import org.example.service.CentralBank;
import org.example.service.TimeManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static junit.framework.Assert.assertEquals;

public class TimeMachineTest {
    private User verified;
    private TimeManager timeManager;
    private Bank sber;
    private LocalDateTime dateFirst;

    @BeforeEach
    public void setUp() throws Exception {
        dateFirst = LocalDateTime.of(2022, 9, 1, 0, 0, 0);
        timeManager = new TimeManager(LocalDateTime.of(2022, 9, 1, 0, 0, 0));

        verified = new UserBuilder("Sasha", "Ivanov", 100000).withAddress("Green Street").withPassportId(124).build();

        // инициализация банка и "машины времени"
        sber = new Bank("SberBank", 1, 2, 5, 5000, 10000, 2, -1000000, 1000, 999999999);
        CentralBank centralBank = new CentralBank();
        centralBank.addBank(sber);
        sber.addUser(verified);

        timeManager.addObserver(sber);
    }

    @Test
    public void test_DebitCardBalanceAfterOneMonth() throws Exception {
        DebitCard card = sber.addDebitCard(dateFirst, 50000, verified.getUserId());
        timeManager.addMonth();

        assertEquals(80000, card.getBalance(), 0.001);
    }

    @Test
    public void test_CreditCardBalanceAfterOneMonth_NoOperations() throws Exception {
        CreditCard card = sber.addCreditCard(dateFirst, 100, verified.getUserId());
        timeManager.addMonth();
        assertEquals(999999999, card.getUntrustedUserLimit(), 0.001);
    }

    @Test
    public void test_CreditCardBalanceAfterOneMonth_Withdraw() throws Exception {
        CreditCard card = sber.addCreditCard(dateFirst, 100, verified.getUserId());
        card.withdrawMoney(2000);
        timeManager.addMonth();
        assertEquals(-31900, card.getBalance(), 0.001);
    }

    @Test
    public void test_DepositCardBalanceAfterOneMonth() throws Exception {
        DepositCard card = sber.addDepositCard(dateFirst, LocalDateTime.of(2022, 9, 2, 0, 0, 0), 15000, verified.getUserId());
        timeManager.addMonth();
        assertEquals(37500, card.getBalance(), 0.001);
    }
}
