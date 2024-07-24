package org.example.refactored;

import org.example.entities.User;
import org.example.entities.UserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class UserVerificationTest {
    private User verifiedUser;
    private User notVerifiedUser;
    private User notVerifiedUserOnlyPassport;
    private User notVerifiedUserOnlyAddress;

    @BeforeEach
    public void setUp() throws Exception {
        notVerifiedUser = getPrebuildedUser().build();
        notVerifiedUserOnlyPassport = getPrebuildedUser().withPassportId(123).build();
        notVerifiedUserOnlyAddress = getPrebuildedUser().withAddress("Green Street").build();
        verifiedUser = getPrebuildedUser().withAddress("Green Street").withPassportId(124).build();
    }

    UserBuilder getPrebuildedUser() throws Exception {
        return new UserBuilder("Sasha", "Ivanov", 100000);
    }

    @Test
    public void test_UserIsVerified() {
        assertTrue(verifiedUser.verificationPersonalData());
    }

    // region Если при создании счета у клиента не указаны адрес или номер паспорта, мы объявляем такой счет (любого типа) сомнительным
    @Test
    public void test_UserIsNotVerified_AllValidationPropsNotSpecified() {
        assertFalse(notVerifiedUser.verificationPersonalData());
    }

    @Test
    public void test_UserIsNotVerified_AddressIsNotSpecified() {
        assertFalse(notVerifiedUserOnlyPassport.verificationPersonalData());
    }

    @Test
    public void test_UserIsNotVerified_AddressIsEmpty() {
        notVerifiedUserOnlyPassport.setAddress("");
        assertFalse(notVerifiedUser.verificationPersonalData());
    }

    @Test
    public void test_UserIsNotVerified_PassportIsNotSpecified() {
        assertFalse(notVerifiedUserOnlyAddress.verificationPersonalData());
    }
    // endregion

    // region Если в дальнейшем клиент указывает всю необходимую информацию о себе - счет перестает быть сомнительным и может использоваться без ограничений
    @Test
    public void test_UserPassVerification() {
        notVerifiedUser.setAddress("ActualAddress");
        notVerifiedUser.setPassportId(123);
        assertTrue(notVerifiedUser.verificationPersonalData());
    }
    // endregion
}
