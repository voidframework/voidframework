package dev.voidframework.i18n;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Locale;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
final class InternationalizationTest {

    @BeforeAll
    public static void beforeAll() {

        Locale.setDefault(Locale.ENGLISH);
    }

    @Test
    void getMessageEnglish() {

        // Arrange
        final ResourceBundleInternationalization internationalization = new ResourceBundleInternationalization();

        // Act
        final String msgHelloEnglish = internationalization.getMessage(Locale.ENGLISH, "hello.world");

        // Assert
        Assertions.assertNotNull(msgHelloEnglish);
        Assertions.assertEquals("Hello World!", msgHelloEnglish);
    }

    @Test
    void getMessageFrench() {

        // Arrange
        final ResourceBundleInternationalization internationalization = new ResourceBundleInternationalization();

        // Act
        final String msgHelloFrench = internationalization.getMessage(Locale.FRENCH, "hello.world");

        // Assert
        Assertions.assertNotNull(msgHelloFrench);
        Assertions.assertEquals("Bonjour !", msgHelloFrench);
    }

    @Test
    void getMessageNotHandledLanguage() {

        // Arrange
        final ResourceBundleInternationalization internationalization = new ResourceBundleInternationalization();

        // Act
        final String msgNotFound = internationalization.getMessage(Locale.FRENCH, "unknown.key");


        // Assert
        Assertions.assertNotNull(msgNotFound);
        Assertions.assertEquals("%unknown.key%", msgNotFound);
    }

    @Test
    void getMessageArgumentEnglish() {

        // Arrange
        final ResourceBundleInternationalization internationalization = new ResourceBundleInternationalization();

        // Act
        final String msgHelloEnglish = internationalization.getMessage(Locale.ENGLISH, "hello.name", "Aurelia");

        // Assert
        Assertions.assertNotNull(msgHelloEnglish);
        Assertions.assertEquals("Hello Aurelia!", msgHelloEnglish);
    }

    @Test
    void getMessageArgumentFrench() {

        // Arrange
        final ResourceBundleInternationalization internationalization = new ResourceBundleInternationalization();

        // Act
        final String msgHelloFrench = internationalization.getMessage(Locale.FRENCH, "hello.name", "Byron");

        // Assert
        Assertions.assertNotNull(msgHelloFrench);
        Assertions.assertEquals("Bonjour Byron !", msgHelloFrench);
    }

    @Test
    void getMessageComplexFormatZero() {

        // Arrange
        final ResourceBundleInternationalization internationalization = new ResourceBundleInternationalization();

        // Act
        final String msgZeroComment = internationalization.getMessage(Locale.ENGLISH, "complex.format", 0);

        // Assert
        Assertions.assertNotNull(msgZeroComment);
        Assertions.assertEquals("This element contains no comments", msgZeroComment);

        final String msgTwoComment = internationalization.getMessage(Locale.ENGLISH, "complex.format", 1);
        Assertions.assertNotNull(msgTwoComment);
        Assertions.assertEquals("This element contains one comment", msgTwoComment);

        final String msgALotOfComment = internationalization.getMessage(Locale.ENGLISH, "complex.format", 1337);
        Assertions.assertNotNull(msgALotOfComment);
        Assertions.assertEquals("This element contains 1337 comments", msgALotOfComment);
    }

    @Test
    void getMessageComplexFormatSingular() {

        // Arrange
        final ResourceBundleInternationalization internationalization = new ResourceBundleInternationalization();

        // Act
        final String msgOneComment = internationalization.getMessage(Locale.ENGLISH, "complex.format", 1);

        // Assert
        Assertions.assertNotNull(msgOneComment);
        Assertions.assertEquals("This element contains one comment", msgOneComment);

        final String msgALotOfComment = internationalization.getMessage(Locale.ENGLISH, "complex.format", 1337);
        Assertions.assertNotNull(msgALotOfComment);
        Assertions.assertEquals("This element contains 1337 comments", msgALotOfComment);
    }

    @Test
    void getMessageComplexFormatPlural() {

        // Arrange
        final ResourceBundleInternationalization internationalization = new ResourceBundleInternationalization();

        // Act
        final String msgALotOfComment = internationalization.getMessage(Locale.ENGLISH, "complex.format", 1337);

        // Assert
        Assertions.assertNotNull(msgALotOfComment);
        Assertions.assertEquals("This element contains 1337 comments", msgALotOfComment);
    }

    @Test
    void getMessageLocalNull() {

        // Arrange
        final ResourceBundleInternationalization internationalization = new ResourceBundleInternationalization();

        // Act
        final String msg = internationalization.getMessage(null, "hello.world");

        // Assert
        Assertions.assertNotNull(msg);
        Assertions.assertEquals("%hello.world%", msg);
    }

    @Test
    void getMessagePluraleFormZero() {

        // Arrange
        final ResourceBundleInternationalization internationalization = new ResourceBundleInternationalization();

        // Act
        final String msgPlural0English = internationalization.getMessage(Locale.ENGLISH, 0, "inbox", "BoxName", 0);

        // Assert
        Assertions.assertNotNull(msgPlural0English);
        Assertions.assertEquals("Inbox \"BoxName\" contains no messages", msgPlural0English);
    }

    @Test
    void getMessagePluraleFormSingular() {

        // Arrange
        final ResourceBundleInternationalization internationalization = new ResourceBundleInternationalization();

        // Act
        final String msgSingularEnglish = internationalization.getMessage(Locale.ENGLISH, 1, "inbox", "BoxName", 1);

        // Assert
        Assertions.assertNotNull(msgSingularEnglish);
        Assertions.assertEquals("Inbox \"BoxName\" contains one message", msgSingularEnglish);
    }

    @Test
    void getMessagePluraleFormPlural() {

        // Arrange
        final ResourceBundleInternationalization internationalization = new ResourceBundleInternationalization();

        // Act
        final String msgPluralMoreEnglish = internationalization.getMessage(Locale.ENGLISH, 456, "inbox", "BoxName", 456);

        // Assert
        Assertions.assertNotNull(msgPluralMoreEnglish);
        Assertions.assertEquals("Inbox \"BoxName\" contains 456 messages", msgPluralMoreEnglish);
    }
}
