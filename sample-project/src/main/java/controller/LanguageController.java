package controller;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import com.voidframework.core.bindable.Controller;
import com.voidframework.web.http.Context;
import com.voidframework.web.http.Result;
import com.voidframework.web.http.param.RequestPath;
import com.voidframework.web.http.param.RequestRoute;
import com.voidframework.web.routing.HttpMethod;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * This controller allows visitor to change I18N language.
 */
@Controller
public final class LanguageController {

    private final Config configuration;

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
        final boolean isAllowed = this.configuration.getStringList("voidframework.web.i18n.languages")
            .contains(requestedLanguage);

        if (isAllowed) {
            context.setLocal(Locale.forLanguageTag(requestedLanguage));
        }

        // Redirect to the previous page, otherwise, "/"
        final String referer = context.getRequest().getHeader("referer");
        if (StringUtils.isNotBlank(referer)) {
            return Result.redirectTemporaryTo(referer);
        } else {
            return Result.redirectTemporaryTo("/");
        }
    }
}
