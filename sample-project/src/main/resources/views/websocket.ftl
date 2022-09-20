<html lang="${lang!}">
<head>
    <title>Void Framework</title>
    <meta charset="utf-8">
    <link rel="stylesheet" href="/webjars/bootstrap/5.2.0/css/bootstrap.min.css">
    <script>
        let socket;
        if (window.WebSocket) {
            socket = new WebSocket('ws://127.0.0.1:9000/ws');
            socket.onmessage = function (event) {
                let chat = document.getElementById('chat');
                chat.innerHTML = chat.innerHTML + event.data + '<br />';
            };
        }

        function send(message) {
            if (socket.readyState === WebSocket.OPEN) {
                socket.send(message);
            } else {
                alert('The socket is not open.');
            }

            return false;
        }
    </script>
</head>
<body>
<div class="container">
    <h1>Web Socket</h1>

    <div id="chat" style="height:80%;width: 100%; overflow: scroll;"></div>
    <form onsubmit="return false;" class="chatform" action="">
        <label for="msg">Message</label>
        <input type="text" name="message" id="msg" onkeypress="if(event.keyCode==13) { send(this.form.message.value); this.value='' } "/>
    </form>
</div>
</body>
</html>
