package org.example.refactored;

import org.example.entities.Bank;
import org.example.exception.CentralBankException;
import org.example.service.CentralBank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BankValidationTest {
    CentralBank centralBank;
    Bank nullBank;

    @BeforeEach
    public void setUp(){
        centralBank = new CentralBank();
        nullBank = null;
    }

    @Test
    public void testNullBankExceptionThrow() {
        CentralBankException exception = assertThrows(CentralBankException.class, () -> {
            centralBank.addBank(nullBank);
        });
        assertEquals("Unable to add bank due to null object", exception.getMessage());
    }
}
