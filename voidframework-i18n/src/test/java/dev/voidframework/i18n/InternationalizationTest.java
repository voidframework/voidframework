package dev.voidframework.i18n;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Locale;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public final class InternationalizationTest {

    @Test
    public void getMessage() {
        final dev.voidframework.i18n.ResourceBundleInternationalization internationalization = new dev.voidframework.i18n.ResourceBundleInternationalization();

        final String msgHelloEnglish = internationalization.getMessage(Locale.ENGLISH, "hello.world");
        Assertions.assertNotNull(msgHelloEnglish);
        Assertions.assertEquals("Hello World!", msgHelloEnglish);

        final String msgHelloFrench = internationalization.getMessage(Locale.FRENCH, "hello.world");
        Assertions.assertNotNull(msgHelloFrench);
        Assertions.assertEquals("Bonjour !", msgHelloFrench);

        final String msgNotHandledLocale = internationalization.getMessage(Locale.GERMAN, "hello.world");
        Assertions.assertNotNull(msgNotHandledLocale);
        Assertions.assertEquals("Hello World!", msgNotHandledLocale);

        final String msgNotFound = internationalization.getMessage(Locale.FRENCH, "unknown.key");
        Assertions.assertNotNull(msgNotFound);
        Assertions.assertEquals("%unknown.key%", msgNotFound);
    }

    @Test
    public void getMessageArgument() {
        final dev.voidframework.i18n.ResourceBundleInternationalization internationalization = new dev.voidframework.i18n.ResourceBundleInternationalization();

        final String msgHelloEnglish = internationalization.getMessage(Locale.ENGLISH, "hello.name", "Aurelia");
        Assertions.assertNotNull(msgHelloEnglish);
        Assertions.assertEquals("Hello Aurelia!", msgHelloEnglish);

        final String msgHelloFrench = internationalization.getMessage(Locale.FRENCH, "hello.name", "Byron");
        Assertions.assertNotNull(msgHelloFrench);
        Assertions.assertEquals("Bonjour Byron !", msgHelloFrench);
    }

    @Test
    public void getMessageComplexFormat() {
        final dev.voidframework.i18n.ResourceBundleInternationalization internationalization = new dev.voidframework.i18n.ResourceBundleInternationalization();

        final String msgZeroComment = internationalization.getMessage(Locale.ENGLISH, "complex.format", 0);
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
    public void getMessageLocalNull() {
        final dev.voidframework.i18n.ResourceBundleInternationalization internationalization = new dev.voidframework.i18n.ResourceBundleInternationalization();

        final String msg = internationalization.getMessage(null, "hello.world");
        Assertions.assertNotNull(msg);
        Assertions.assertEquals("%hello.world%", msg);
    }

    @Test
    public void getMessagePluraleForm() {
        final dev.voidframework.i18n.ResourceBundleInternationalization internationalization = new dev.voidframework.i18n.ResourceBundleInternationalization();

        final String msgPlural0English = internationalization.getMessage(Locale.ENGLISH, 0, "inbox", "BoxName", 0);
        Assertions.assertNotNull(msgPlural0English);
        Assertions.assertEquals("Inbox \"BoxName\" contains no messages", msgPlural0English);

        final String msgPlural1English = internationalization.getMessage(Locale.ENGLISH, 1, "inbox", "BoxName", 1);
        Assertions.assertNotNull(msgPlural1English);
        Assertions.assertEquals("Inbox \"BoxName\" contains one message", msgPlural1English);

        final String msgPluralMoreEnglish = internationalization.getMessage(Locale.ENGLISH, 456, "inbox", "BoxName", 456);
        Assertions.assertNotNull(msgPluralMoreEnglish);
        Assertions.assertEquals("Inbox \"BoxName\" contains 456 messages", msgPluralMoreEnglish);

        final String msgPlural0French = internationalization.getMessage(Locale.FRENCH, 0, "inbox", "BoxName", 0);
        Assertions.assertNotNull(msgPlural0French);
        Assertions.assertEquals("La boite \"BoxName\" contient aucun message", msgPlural0French);

        final String msgPlural1French = internationalization.getMessage(Locale.FRENCH, 1, "inbox", "BoxName", 1);
        Assertions.assertNotNull(msgPlural1French);
        Assertions.assertEquals("La boite \"BoxName\" contient un seul message", msgPlural1French);

        final String msgPluralMoreFrench = internationalization.getMessage(Locale.FRENCH, 456, "inbox", "BoxName", 456);
        Assertions.assertNotNull(msgPluralMoreFrench);
        Assertions.assertEquals("La boite \"BoxName\" contient 456 messages", msgPluralMoreFrench);
    }
}
