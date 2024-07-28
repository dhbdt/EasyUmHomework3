package org.example.refactored;

import org.example.entities.Bank;
import org.example.entities.DebitCard;
import org.example.entities.User;
import org.example.entities.UserBuilder;
import org.example.service.CentralBank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;

public class TransactionTest {
    private CentralBank centralBank;
    private DebitCard sashaCard;
    private DebitCard ivanCard;


    @BeforeEach
    public void setUp() throws Exception {
        Bank sber = new Bank("SberBank", 1, 2, 5, 5000, 10000, 2, -1000000, 1000, 999999999);
        LocalDateTime dateFirst = LocalDateTime.of(2022, 9, 1, 0, 0, 0);

        User sasha = new UserBuilder("Sasha", "Ivanov", 100000).withAddress("Green Street").withPassportId(124).build();
        User ivan = new UserBuilder("Ivan", "Petrov", 10000).withAddress("Green Street").withPassportId(123).build();

        centralBank = new CentralBank();
        centralBank.addBank(sber);
        sber.addUser(sasha);
        sber.addUser(ivan);
        
        sashaCard = sber.addDebitCard(dateFirst, 50000, sasha.getUserId());
        ivanCard = sber.addDebitCard(dateFirst, 50000, ivan.getUserId());
    }

    @Test
    public void test_AddMoney() throws Exception {
        sashaCard.topUpCard(10000);
        assertEquals(60000, sashaCard.getBalance(), 0.001);
    }

    @Test
    public void test_WithdrawMoney() throws Exception {
        sashaCard.withdrawMoney(5000);
        assertEquals(45000, sashaCard.getBalance(), 0.001);
    }

    @Test
    @Timeout(value = 1000000, unit = TimeUnit.NANOSECONDS)
    public void test_SimpleTransaction() throws Exception {
        centralBank.transferMoney(25000, sashaCard.getCardId(), ivanCard.getCardId());

        assertEquals(75000, ivanCard.getBalance(), 0.001);
        assertEquals(25000, sashaCard.getBalance(), 0.001);
    }

    @Test
    @Timeout(value = 1000000, unit = TimeUnit.NANOSECONDS)
    public void test_TransactionCancellation() throws Exception {
        centralBank.transferMoney(25000, sashaCard.getCardId(), ivanCard.getCardId());

        centralBank.transactionCancellation(sashaCard.getCardId(), 0);
        assertEquals(50000, ivanCard.getBalance(), 0.001);
        assertEquals(50000, sashaCard.getBalance(), 0.001);
    }


    @Test
    public void test_TransactionCancellation_CancelOneOfMany() throws Exception {
        sashaCard.topUpCard(10000);
        centralBank.transferMoney(20000, sashaCard.getCardId(), ivanCard.getCardId());

        centralBank.transactionCancellation(sashaCard.getCardId(), 0);
        assertEquals(30000, sashaCard.getBalance(), 0.001);
    }
}
