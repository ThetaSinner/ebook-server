'use strict';

$(function () {
    const SERVER_URL = 'http://localhost:8080';

    $('#loadLibrary').submit(function (event) {
        event.preventDefault();

        var target = $(event.target);
        var action = target.attr('action');
        var name = target.find('#libraryNameInput').val();

        console.log(SERVER_URL + action + '?' + encodeURIComponent(name));

        $.ajax(
            SERVER_URL + action + '?name=' + encodeURIComponent(name), {
                method: 'GET',
                success: function(data, textStatus, jqXHR) {
                    /* eslint-disable no-console */
                    console.log(textStatus, jqXHR);
                    /* eslint-enable no-console */
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    /* eslint-disable no-console */
                    console.log(textStatus, errorThrown);
                    /* eslint-enable no-console */
                }
            }
        );
    });

    $('#saveLibrary').submit(function (event) {
        event.preventDefault();

        var target = $(event.target);
        var action = target.attr('action');

        $.ajax(
            SERVER_URL + action + '?' + target.serialize(), {
                method: 'GET',
                success: function(data, textStatus, jqXHR) {
                    /* eslint-disable no-console */
                    console.log(textStatus, jqXHR);
                    /* eslint-enable no-console */
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    /* eslint-disable no-console */
                    console.log(textStatus, errorThrown);
                    /* eslint-enable no-console */
                }
            }
        );
    });
});

const BookModel = Backbone.Model.extend({
    defaults: {
        'title':  'Missing title',
        'authors': ['Missing author'],
    },

    url: '/books'
});
