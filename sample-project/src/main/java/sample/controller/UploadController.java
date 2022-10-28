package sample.controller;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import dev.voidframework.vfs.engine.VirtualFileStorage;
import dev.voidframework.web.bindable.WebController;
import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.FormItem;
import dev.voidframework.web.http.HttpContentTypes;
import dev.voidframework.web.http.HttpMethod;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.http.TemplateResult;
import dev.voidframework.web.http.annotation.RequestRoute;
import dev.voidframework.web.http.routing.Router;

import java.io.InputStream;

/**
 * A Simple upload web controller.
 */
@Singleton
@WebController(prefixRoute = "upload")
public final class UploadController {

    private final Router router;
    private final Provider<VirtualFileStorage> vfs;

    /**
     * Build a new instance.
     *
     * @param router Router instance
     * @param vfs    Virtual File Storage engine instance
     */
    @Inject
    public UploadController(final Router router,
                            final Provider<VirtualFileStorage> vfs) {

        this.router = router;
        this.vfs = vfs;
    }

    /**
     * Display the upload page.
     *
     * @return A Result
     */
    @RequestRoute(method = HttpMethod.GET, name = "showUploadPage")
    public Result showUploadPage() {

        return Result.ok(TemplateResult.of("upload.ftl"));
    }

    /**
     * Display the upload page.
     *
     * @return A Result
     */
    @RequestRoute(method = HttpMethod.POST, name = "uploadFile")
    public Result uploadFile(final Context context) {

        final FormItem formItem = context.getRequest().getBodyContent().asFormData().getFirst("formFile");
        if (formItem != null && formItem.fileSize() > 0) {
            this.vfs.get().storeFile("upload", HttpContentTypes.APPLICATION_OCTET_STREAM, formItem.inputStream());
        }

        return Result.redirectSeeOther(this.router.reverseRoute("showUploadPage"));
    }

    /**
     * Get upload file.
     *
     * @return A Result
     */
    @RequestRoute(method = HttpMethod.GET, route = "get", name = "getUploadedFile")
    public Result getUploadedFile() {

        final InputStream is = this.vfs.get().retrieveFile("upload");
        if (is == null) {
            return Result.notFound();
        }

        return Result.ok(is, HttpContentTypes.APPLICATION_OCTET_STREAM);
    }
}
