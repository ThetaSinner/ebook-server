$(function () {
    const SERVER_URL = 'http://localhost:8080';

    $('#loadLibrary').submit(function (event) {
        event.preventDefault();

        var target = $(event.target);
        var action = target.attr('action');

        $.ajax(
            SERVER_URL + action + '?' + target.serialize(), {
                method: 'GET',
                success: function(data, textStatus, jqXHR) {
                    console.log(textStatus);
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log(textStatus);
                }
            }
        );
    });
});
