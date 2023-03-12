package dev.voidframework.sendmail.engine.constant;

import java.util.List;

/**
 * Mail Mime Property keys.
 *
 * @see <a href="https://javaee.github.io/javamail/docs/api/javax/mail/internet/package-summary.html">JavaEE Mail Mime Properties</a>
 * @since 1.7.0
 */
public final class MailMimePropertyKeys {

    /**
     * @since 1.7.0
     */
    public static final String ADDRESS_USECANONICALHOSTNAME = "mail.mime.address.usecanonicalhostname";

    /**
     * @since 1.7.0
     */
    public static final String ALLOWENCODEDMESSAGES = "mail.mime.allowencodedmessages";

    /**
     * @since 1.7.0
     */
    public static final String APPLEFILENAMES = "mail.mime.applefilenames";

    /**
     * @since 1.7.0
     */
    public static final String BASE64_IGNOREERRORS = "mail.mime.base64.ignoreerrors";

    /**
     * @since 1.7.0
     */
    public static final String CHARSET = "mail.mime.charset";

    /**
     * @since 1.7.0
     */
    public static final String CONTENTTYPEHANDLER = "mail.mime.contenttypehandler";

    /**
     * @since 1.7.0
     */
    public static final String DECODEFILENAME = "mail.mime.decodefilename";

    /**
     * @since 1.7.0
     */
    public static final String DECODEPARAMETERS = "mail.mime.decodeparameters";

    /**
     * @since 1.7.0
     */
    public static final String DECODETEXT_STRICT = "mail.mime.decodetext.strict";

    /**
     * @since 1.7.0
     */
    public static final String ENCODEEOL_STRICT = "mail.mime.encodeeol.strict";

    /**
     * @since 1.7.0
     */
    public static final String ENCODEFILENAME = "mail.mime.encodefilename";

    /**
     * @since 1.7.0
     */
    public static final String ENCODEPARAMETERS = "mail.mime.encodeparameters";

    /**
     * @since 1.7.0
     */
    public static final String FOLDTEXT = "mail.mime.foldtext";

    /**
     * @since 1.7.0
     */
    public static final String IGNOREMULTIPARTENCODING = "mail.mime.ignoremultipartencoding";

    /**
     * @since 1.7.0
     */
    public static final String IGNOREUNKNOWNENCODING = "mail.mime.ignoreunknownencoding";

    /**
     * @since 1.7.0
     */
    public static final String IGNOREWHITESPACELINES = "mail.mime.ignorewhitespacelines";

    /**
     * @since 1.7.0
     */
    public static final String MULTIPART_ALLOWEMPTY = "mail.mime.multipart.allowempty";

    /**
     * @since 1.7.0
     */
    public static final String MULTIPART_IGNOREEXISTINGBOUNDARYPARAMETER = "mail.mime.multipart.ignoreexistingboundaryparameter";

    /**
     * @since 1.7.0
     */
    public static final String MULTIPART_IGNOREMISSINGBOUNDARYPARAMETER = "mail.mime.multipart.ignoremissingboundaryparameter";

    /**
     * @since 1.7.0
     */
    public static final String MULTIPART_IGNOREMISSINGENDBOUNDARY = "mail.mime.multipart.ignoremissingendboundary";

    /**
     * @since 1.7.0
     */
    public static final String PARAMETERS_STRICT = "mail.mime.parameters.strict";

    /**
     * @since 1.7.0
     */
    public static final String SETCONTENTTYPEFILENAME = "mail.mime.setcontenttypefilename";

    /**
     * @since 1.7.0
     */
    public static final String SETDEFAULTTEXTCHARSET = "mail.mime.setdefaulttextcharset";

    /**
     * @since 1.7.0
     */
    public static final String UUDECODE_IGNOREERRORS = "mail.mime.uudecode.ignoreerrors";

    /**
     * @since 1.7.0
     */
    public static final String UUDECODE_IGNOREMISSINGBEGINEND = "mail.mime.uudecode.ignoremissingbeginend";

    /**
     * @since 1.7.0
     */
    public static final String WINDOWSFILENAMES = "mail.mime.windowsfilenames";

    /**
     * Default constructor.
     *
     * @since 1.7.0
     */
    private MailMimePropertyKeys() {

        throw new UnsupportedOperationException("This is a class containing constants and cannot be instantiated");
    }

    /**
     * Returns all constant keys.
     *
     * @return An immutable list containing all keys
     * @since 1.7.0
     */
    public static List<String> keys() {

        return List.of(
            ADDRESS_USECANONICALHOSTNAME,
            ALLOWENCODEDMESSAGES,
            APPLEFILENAMES,
            BASE64_IGNOREERRORS,
            CHARSET,
            CONTENTTYPEHANDLER,
            DECODEFILENAME,
            DECODEPARAMETERS,
            DECODETEXT_STRICT,
            ENCODEEOL_STRICT,
            ENCODEFILENAME,
            ENCODEPARAMETERS,
            FOLDTEXT,
            IGNOREMULTIPARTENCODING,
            IGNOREUNKNOWNENCODING,
            IGNOREWHITESPACELINES,
            MULTIPART_ALLOWEMPTY,
            MULTIPART_IGNOREEXISTINGBOUNDARYPARAMETER,
            MULTIPART_IGNOREMISSINGBOUNDARYPARAMETER,
            MULTIPART_IGNOREMISSINGENDBOUNDARY,
            PARAMETERS_STRICT,
            SETCONTENTTYPEFILENAME,
            SETDEFAULTTEXTCHARSET,
            UUDECODE_IGNOREERRORS,
            UUDECODE_IGNOREMISSINGBEGINEND,
            WINDOWSFILENAMES);
    }
}
