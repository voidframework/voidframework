package sample.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import dev.voidframework.web.bindable.WebController;
import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.HttpMethod;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.http.annotation.RequestPath;
import dev.voidframework.web.http.annotation.RequestRoute;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * This controller allows visitor to change I18N language.
 */
@Singleton
@WebController
public final class LanguageController {

    private final Config configuration;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     */
    @Inject
    public LanguageController(final Config configuration) {
        this.configuration = configuration;
    }

    /**
     * Change the language to use for I18N.
     *
     * @param context           The current context
     * @param requestedLanguage The requested language (ie: fr)
     * @return A redirection
     */
    @RequestRoute(method = HttpMethod.GET, route = "/lang/(?<lang>[a-zA-Z_]{2,5})")
    public Result changeLanguage(final Context context, @RequestPath("lang") final String requestedLanguage) {
        // Change language
        final boolean isAllowed = this.configuration.getStringList("voidframework.web.language.availableLanguages")
            .contains(requestedLanguage);

        if (isAllowed) {
            context.setLocal(Locale.forLanguageTag(requestedLanguage));
        }

        context.getFlashMessages().put("success", "flash.language.changed");

        // Redirect to the previous page, otherwise, "/"
        final String referer = context.getRequest().getHeader("referer");
        if (StringUtils.isNotBlank(referer)) {
            return Result.redirectTemporaryTo(referer);
        } else {
            return Result.redirectTemporaryTo("/");
        }
    }
}
