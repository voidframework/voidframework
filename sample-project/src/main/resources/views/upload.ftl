<html lang="${lang!}">
<head>
    <title>Void Framework</title>
    <meta charset="utf-8">
    <link rel="stylesheet" href="/webjars/bootstrap/5.2.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="/webjars/font-awesome/6.1.2/css/all.min.css">
    <link rel="stylesheet" href="/static/css/application.css">
</head>
<body class="disable-select">

<div class="col-lg-8 mx-auto p-3 py-md-5">
    <header class="d-flex align-items-center pb-3 mb-5 border-bottom">
        <a href="/" class="d-flex align-items-center text-dark text-decoration-none">
            <img width="32" height="32" class="me-2" role="img" src="/static/favicon.ico" alt="VoidF Framework"/>
            <span class="fs-4">Void Framework</span>
        </a>
    </header>

    <#if (flash.error)??>
        <div class="alert alert-danger">
            <span type="button" class="close float-end" data-bs-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></span>
            ${i18n(flash.error)}
        </div>
    </#if>

    <#if (flash.warning)??>
        <div class="alert alert-warning alert-dismissible fade show" role="alert">
            <span type="button" class="close float-end" data-bs-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></span>
            ${i18n(flash.warning)}
        </div>
    </#if>

    <#if (flash.success)??>
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <span type="button" class="close float-end" data-bs-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></span>
            ${i18n(flash.success)}
        </div>
    </#if>

    <main>
        <h1>Upload</h1>
        <br/>
        <div>
            <form method="POST" action="${urlfor('uploadFile')}" enctype="multipart/form-data">
                <div class="mb-3">
                    <label for="formFile" class="form-label">Default file input example</label>
                    <input class="form-control" type="file" name="formFile" accept="image/*">
                </div>

                <button class="btn btn-outline-primary btn-sm">Upload</button>
            </form>
            <br/>
            <img src="${urlfor('getUploadedFile')}" alt="Uploaded picture" />
        </div>
    </main>

    <footer class="pt-2 my-5 text-muted border-top">
        <div class="float-start">
            Void Framework ${voidFrameworkVersion}
        </div>
        <div class="float-end">
            <div class="dropup">
                <a class="text-muted text-decoration-none text-decoration-none" data-bs-toggle="dropdown" aria-expanded="false">
                    <i class="fas fa-language"></i> &nbsp; ${i18n("lang." + lang)}
                </a>
                <ul class="dropdown-menu">
                    <#list config("voidframework.web.language.availableLanguages") as language>
                        <li>
                            <a class="dropdown-item" href="/lang/${language}">${i18n("lang." + language)}</a>
                        </li>
                    </#list>
                </ul>
            </div>
        </div>
    </footer>
</div>
<div class="visually-hidden" id="template-click-loading">
    <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
    ${i18n("commons.loader.please.wait")}
</div>
</body>
<script type="text/javascript" src="/webjars/jquery/3.6.1/jquery.js"></script>
<script type="text/javascript" src="/webjars/popper.js/2.9.2/umd/popper.min.js"></script>
<script type="text/javascript" src="/webjars/bootstrap/5.2.0/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript" src="/static/js/application.js"></script>
</html>
