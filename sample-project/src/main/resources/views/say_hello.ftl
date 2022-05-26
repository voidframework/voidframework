<html lang="${lang!}">
<head>
    <title>Void Framework</title>
    <meta charset="utf-8">
    <link rel="stylesheet" href="/webjars/bootstrap/5.1.3/css/bootstrap.min.css">
</head>
<body>
<div class="container">
    <h1>Welcome!</h1>

    <div class="card" style="width: 18rem;">
        <img src="/static/raccoon.jpg" class="card-img-top" alt="raccoon"/>
        <div class="card-body">
            <h5 class="card-title">${name}</h5>
            <p class="card-text">Your IP is ${remoteHostName}.</p>
        </div>
    </div>
</div>
</body>
</html>
