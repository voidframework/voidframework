package dev.voidframework.web.http.errorpage;

import dev.voidframework.web.routing.HttpMethod;
import dev.voidframework.web.routing.Route;

import java.util.List;

/**
 * Generates a "404 Not Found" page when development mode is enabled. The template
 * engine is not used, as it is not guaranteed to be available or compatible.
 */
public final class DevMode404NotFound {

    private static final String TITLE = "404 Not Found";

    private static final String CSS_STYLE = """
        body {
            margin: 0;
            padding: 0;
            background-color: #ececec;
        }

        header {
            padding: 5px 5px 5px 20px;
            display: block;
            background-color: #e7881c;
            color: #ffffff;
            font-weight: bold;
        }

        .subheader {
            border: solid 3px #e7961c;
            padding: 20px 20px 20px 20px;
            display: block;
            background-color: #edeadd;
            color: #5d3604;
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
            <h1>No route has been found</h1>
        </header>
        <div class="subheader">
            <code>For request %s %s</code>
        </div>
        <div class="description">
            <h2>Available routes</h2>
            <table>
                %s
            </table>
            <br/>
        </div>
        </html>
        """;

    private static final String FILE_PATTERN_ENTRY = """
        <tr>
            <td><code>%s</code></td>
            <td><pre>%s</pre></td>
        </tr>
        """;

    /**
     * Default constructor.
     */
    private DevMode404NotFound() {

        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Render template.
     *
     * @param requestMethod The request method (ie: GET)
     * @param requestUri    The request URI
     * @param routeList     The available routes
     * @return The rendered template
     */
    public static String render(final HttpMethod requestMethod,
                                final String requestUri,
                                final List<Route> routeList) {

        final StringBuilder availableRoutesBuilder = new StringBuilder();
        for (final Route route : routeList) {
            availableRoutesBuilder.append(FILE_PATTERN_ENTRY.formatted(
                route.httpMethod(),
                route.routePattern().toString()));
        }

        return CONTENT.formatted(requestMethod, requestUri, availableRoutesBuilder.toString());
    }
}
