package dev.voidframework.web.http.errorhandler.errorpage;

import org.apache.commons.lang3.StringUtils;

/**
 * Generates a "400 Bad Request" page when development mode is enabled. The template
 * engine is not used, as it is not guaranteed to be available or compatible.
 *
 * @since 1.0.0
 */
public final class DevMode400BadRequest {

    private static final String TITLE = "400 Bad Request";

    private static final String CSS_STYLE = """
        body {
            margin: 0;
            padding: 0;
            background-color: #ececec;
        }

        header {
            padding: 5px 5px 5px 20px;
            display: block;
            background-color: #7d1ce7;
            color: #ffffff;
            font-weight: bold;
        }

        .subheader {
            border: solid 3px #7d1ce7;
            padding: 20px 20px 20px 20px;
            display: block;
            background-color: #e3dded;
            color: #1b045d;
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
            <h1>Client provides a bad request</h1>
        </header>
        <div class="subheader">
            <code>%s</code>
        </div>
        </html>
        """;

    /**
     * Default constructor.
     *
     * @since 1.0.0
     */
    private DevMode400BadRequest() {

        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Render template.
     *
     * @param errorMessage The error message
     * @return The rendered template
     * @since 1.0.0
     */
    public static String render(final String errorMessage) {

        return CONTENT.formatted(errorMessage == null ? StringUtils.EMPTY : errorMessage);
    }
}
