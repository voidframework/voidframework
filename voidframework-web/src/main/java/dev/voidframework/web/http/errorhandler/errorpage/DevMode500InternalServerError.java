package dev.voidframework.web.http.errorhandler.errorpage;

import dev.voidframework.web.http.errorhandler.DefaultErrorHandler;

import java.util.List;

/**
 * Generates a "500 Internal Server Error" page when development mode is enabled. The
 * template engine is not used, as it is not guaranteed to be available or compatible.
 */
public final class DevMode500InternalServerError {

    private static final String TITLE = "500 Internal Server Error";

    private static final String CSS_STYLE = """
        body {
            margin: 0;
            padding: 0;
            background-color: #ececec;
        }

        header {
            padding: 5px 5px 5px 20px;
            display: block;
            background-color: #e71c2f;
            color: #ffffff;
            font-weight: bold;
        }

        .subheader {
            border: solid 3px #e71c2f;
            padding: 20px 20px 20px 20px;
            display: block;
            background-color: #f8d7da;
            color: #5d040a;
            font-size: 1.2rem;
            max-height: 250px;
        }

        .description {
            padding: 20px 20px 5px 20px;
            display: block;
            font-size: 1rem;
        }

        .description table {
            font-size: 1.0rem;
            width: 100%%;
        }

        .description table tr td:first-child {
            width: 80px;
        }

        .description table tr:nth-child(2n+1) {
            background: #CCC
        }

        .error {
            color: #c50101;
        }
        """;

    private static final String CONTENT = """
        <html lang="en">
        <head>
            <title>""" + TITLE + """
            </title>
            <style>
        """ + CSS_STYLE + """
            </style>
        </head>
        <header>
            <h1>%s</h1>
        </header>
        <div class="subheader">
            <code>At %s</code>
        </div>
        <div class="description">
            <h2></h2>
            <table>
                %s
            </table>
            <br/>
        </div>
        </html>
        """;

    private static final String CLASS_ERROR = "class=\"error\"";

    private static final String FILE_PATTERN_ENTRY = """
        <tr %s>
            <td><code>%s</code></td>
            <td><pre>%s</pre></td>
        </tr>
        """;

    /**
     * Default constructor.
     */
    private DevMode500InternalServerError() {

        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Render template.
     *
     * @param errorMessage    The error message
     * @param errorLocation   Where the error occur
     * @param errorLineNumber Line number (from zero) where is located the error
     * @param fileLineList    Partial file line content
     * @return The rendered template
     */
    public static String render(final String errorMessage,
                                final String errorLocation,
                                final int errorLineNumber,
                                final List<DefaultErrorHandler.FileLine> fileLineList) {

        final StringBuilder fileSnippedBuilder = new StringBuilder();
        for (final DefaultErrorHandler.FileLine fileLine : fileLineList) {
            fileSnippedBuilder.append(FILE_PATTERN_ENTRY.formatted(
                errorLineNumber == fileLine.number() ? CLASS_ERROR : "",
                fileLine.number(),
                fileLine.content().replace("<", "&lt;")));
        }

        return CONTENT.formatted(errorMessage, errorLocation, fileSnippedBuilder.toString());
    }
}
