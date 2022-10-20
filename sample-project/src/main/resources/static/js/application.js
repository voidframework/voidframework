function hide_button_on_submit(elem) {
    'use strict';

    setTimeout(function () {
        $(elem).html($('#template-click-loading').html());
        $(elem).attr("disabled", true);
    }, 1);

    return true;
}

$(document).ready(function () {
    'use strict';

    let tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl)
    });

    $('#btnGenerateCUID').click(function () {
        $.get("/cuid", function (data) {
            $('#resultGenerateCUID').html(data);
        });
    })
});
