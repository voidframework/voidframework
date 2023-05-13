package dev.voidframework.web.http.errorhandler;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.template.exception.TemplateException;
import dev.voidframework.web.exception.HttpException;
import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.http.errorhandler.errorpage.DevMode400BadRequest;
import dev.voidframework.web.http.errorhandler.errorpage.DevMode404NotFound;
import dev.voidframework.web.http.errorhandler.errorpage.DevMode500InternalServerError;
import dev.voidframework.web.http.routing.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Default implementation of {@link ErrorHandler}.
 *
 * @since 1.0.0
 */
public class DefaultErrorHandler implements ErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultErrorHandler.class);

    private static final String CONFIGURATION_KEY_RUN_IN_DEVELOPMENT_MODE = "voidframework.core.runInDevMode";

    private final Config configuration;
    private final Router router;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     * @param router        The router
     * @since 1.0.0
     */
    @Inject
    public DefaultErrorHandler(final Config configuration, final Router router) {

        this.configuration = configuration;
        this.router = router;
    }

    @Override
    public Result onBadRequest(final Context context, final HttpException.BadRequest badRequestException) {

        if (this.configuration.getBoolean(CONFIGURATION_KEY_RUN_IN_DEVELOPMENT_MODE)) {
            final Throwable cause = (badRequestException == null || badRequestException.getCause() == null)
                ? badRequestException
                : badRequestException.getCause();

            final String errorMessage = cause == null
                ? null
                : cause.getMessage();

            return Result.badRequest(DevMode400BadRequest.render(errorMessage));
        }

        return Result.badRequest("400 Bad Request");
    }

    @Override
    public Result onNotFound(final Context context, final HttpException.NotFound notFoundException) {

        if (this.configuration.getBoolean(CONFIGURATION_KEY_RUN_IN_DEVELOPMENT_MODE)) {
            return Result.notFound(
                DevMode404NotFound.render(
                    context.getRequest().getHttpMethod(),
                    context.getRequest().getRequestURI(),
                    router.getRoutesAsList()));
        }

        return Result.notFound("404 Not Found");
    }

    @Override
    public Result onServerError(final Context context, final Throwable throwable) {

        LOGGER.error("Something goes wrong", throwable);

        if (this.configuration.getBoolean(CONFIGURATION_KEY_RUN_IN_DEVELOPMENT_MODE)) {
            Throwable cause = throwable.getCause() == null ? throwable : throwable.getCause();

            final String subHeaderError;
            final int lineNumberFromZero;
            final List<FileLine> fileLineList;
            if (throwable.getClass() == TemplateException.RenderingFailure.class) {
                // Template rendering error
                final TemplateException.RenderingFailure renderingFailure = (TemplateException.RenderingFailure) throwable;
                final Optional<Path> javaFilepathOptional = resolvePossibleTemplateFileLocation(renderingFailure.getTemplateName());

                lineNumberFromZero = renderingFailure.getLineNumber();
                subHeaderError = javaFilepathOptional.map(Path::toString).orElse(renderingFailure.getTemplateName())
                    + StringConstants.COLON
                    + lineNumberFromZero;
                fileLineList = javaFilepathOptional.map(path -> retrievePartialFileContent(path, lineNumberFromZero)).orElseGet(ArrayList::new);

                if (cause.getCause() != null) {
                    cause = cause.getCause();
                }
            } else {
                // Generic error
                final StackTraceElement stackTraceElement = cause.getStackTrace()[0];
                subHeaderError = stackTraceElement.toString();
                lineNumberFromZero = stackTraceElement.getLineNumber() - 1;

                final String javaFileName = stackTraceElement.getClassName().replace(StringConstants.DOT, File.separator).split("\\$", 2)[0] + ".java";
                final Optional<Path> javaFilepathOptional = resolvePossibleJavaFileLocation(javaFileName);
                fileLineList = javaFilepathOptional.map(path -> retrievePartialFileContent(path, lineNumberFromZero)).orElseGet(ArrayList::new);
            }

            return Result.internalServerError(
                DevMode500InternalServerError.render(
                    cause.getMessage() != null ? cause.getMessage() : "Oops, something goes wrong",
                    subHeaderError,
                    lineNumberFromZero,
                    fileLineList));
        }

        return Result.internalServerError("500 Internal Server Error");
    }

    /**
     * Resolve possible Java file location.
     *
     * @param javaFileName The Java file to find
     * @return The possible Java file location
     * @since 1.0.0
     */
    private Optional<Path> resolvePossibleJavaFileLocation(final String javaFileName) {

        final Path rootPath = Paths.get(System.getProperty("user.dir"));
        Path resolvePath = Path.of("src", "main", "java", javaFileName);

        final Path firstPossibleLocation = rootPath.resolve(resolvePath);
        if (firstPossibleLocation.toFile().exists()) {
            return Optional.of(firstPossibleLocation);
        }

        try (final Stream<Path> stream = Files.walk(rootPath, 1)) {
            return stream
                .filter(Files::isDirectory)
                .map(Path::getFileName)
                .map(path -> path.resolve(resolvePath))
                .filter(Files::exists)
                .findFirst();
        } catch (final IOException ignore) {
            return Optional.empty();
        }
    }

    /**
     * Resolve possible Template file location.
     *
     * @param templateFileName The Template file to find
     * @return The possible Template file location
     * @since 1.0.0
     */
    private Optional<Path> resolvePossibleTemplateFileLocation(final String templateFileName) {

        final Path rootPath = Paths.get(System.getProperty("user.dir"));
        Path resolvePath = Path.of("src", "main", "resources", "views", templateFileName);

        final Path firstPossibleLocation = rootPath.resolve(resolvePath);
        if (firstPossibleLocation.toFile().exists()) {
            return Optional.of(firstPossibleLocation);
        }

        try (final Stream<Path> stream = Files.walk(rootPath, 1)) {
            return stream
                .filter(Files::isDirectory)
                .map(Path::getFileName)
                .map(path -> path.resolve(resolvePath))
                .filter(Files::exists)
                .findFirst();
        } catch (final IOException ignore) {
            return Optional.empty();
        }
    }

    /**
     * Partially read a Java file.
     *
     * @param javaFilePath        The Java file to read
     * @param requestedLineNumber The requested line number
     * @since 1.0.0
     */
    private List<FileLine> retrievePartialFileContent(final Path javaFilePath,
                                                      final int requestedLineNumber) {

        final int cleanedLineNumberFrom = Math.max(requestedLineNumber - 6, 0);
        final int cleanedLineNumberTo = Math.max(cleanedLineNumberFrom, requestedLineNumber + 7);

        final List<FileLine> fileLineList = new ArrayList<>();

        try {
            final List<String> readlineList = Files.readAllLines(javaFilePath);
            for (int idx = cleanedLineNumberFrom; (idx < cleanedLineNumberTo) && (idx < readlineList.size()); idx += 1) {
                fileLineList.add(new FileLine(idx, readlineList.get(idx)));
            }
        } catch (final IOException ignore) {
            // This exception is not important
        }

        return fileLineList;
    }

    /**
     * Represents a single content line.
     *
     * @param number  The line number
     * @param content The line content
     * @since 1.0.0
     */
    public record FileLine(int number, String content) {
    }
}
