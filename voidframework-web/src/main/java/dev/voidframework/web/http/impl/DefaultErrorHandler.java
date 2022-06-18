package dev.voidframework.web.http.impl;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import dev.voidframework.core.bindable.BindClass;
import dev.voidframework.web.exception.HttpException;
import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.ErrorHandler;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.http.errorpage.DevMode404NotFound;
import dev.voidframework.web.http.errorpage.DevMode500InternalServerError;
import dev.voidframework.web.routing.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Default implementation of {@link ErrorHandler}.
 */
@BindClass
public class DefaultErrorHandler implements ErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);

    private final Config configuration;
    private final Router router;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     * @param router        The router
     */
    @Inject
    public DefaultErrorHandler(final Config configuration, final Router router) {
        this.configuration = configuration;
        this.router = router;
    }

    @Override
    public Result onBadRequest(final Context context, final HttpException.BadRequest badRequestException) {
        return Result.badRequest("400 Bad Request");
    }

    @Override
    public Result onNotFound(final Context context, final HttpException.NotFound notFoundException) {
        if (this.configuration.getBoolean("voidframework.core.runInDevMode")) {
            final Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("httpMethod", context.getRequest().getHttpMethod());
            dataModel.put("requestedUri", context.getRequest().getRequestURI());
            dataModel.put("availableRoutes", router.getRoutesAsList());

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

        if (this.configuration.getBoolean("voidframework.core.runInDevMode")) {
            final StackTraceElement stackTraceElement = throwable.getStackTrace()[0];
            final int lineNumberFromZero = stackTraceElement.getLineNumber() - 1;

            final String javaFileName = stackTraceElement.getClassName().replace(".", File.separator) + ".java";
            final Optional<Path> javaFilepathOptional = resolvePossibleJavaFileLocation(javaFileName);
            final List<FileLine> fileLineList = javaFilepathOptional.map(path -> retrievePartialFileContent(path, lineNumberFromZero))
                .orElseGet(ArrayList::new);

            return Result.internalServerError(
                DevMode500InternalServerError.render(
                    throwable.getMessage() != null ? throwable.getMessage() : "Oops, something goes wrong",
                    stackTraceElement.toString(),
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
     * Partially read a Java file.
     *
     * @param javaFilePath        The Java file to read
     * @param requestedLineNumber The requested line number
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
        }

        return fileLineList;
    }

    /**
     * Represents a single content line.
     *
     * @param number  The line number
     * @param content The line content
     */
    public record FileLine(int number, String content) {
    }
}
