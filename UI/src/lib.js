'use strict';

const SERVER_URL = 'http://localhost:8080';

$(function () {


    $('#loadLibrary').submit(function (event) {
        event.preventDefault();

        var target = $(event.target);
        var action = target.attr('action');
        var name = target.find('#libraryNameInput').val();

        /* eslint-disable no-console */
        console.log(SERVER_URL + action + '?' + encodeURIComponent(name));
        /* eslint-enable no-console */

        $.ajax(
            SERVER_URL + action + '?name=' + encodeURIComponent(name), {
                method: 'GET',
                success: function (data, textStatus, jqXHR) {
                    /* eslint-disable no-console */
                    console.log(textStatus, jqXHR);
                    /* eslint-enable no-console */

                    var library = new BooksCollection();
                    library.fetch();
                    var view = new BooksView({collection: library});
                    view.render();
                },
                error: function (jqXHR, textStatus, errorThrown) {
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
                success: function (data, textStatus, jqXHR) {
                    /* eslint-disable no-console */
                    console.log(textStatus, jqXHR);
                    /* eslint-enable no-console */
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    /* eslint-disable no-console */
                    console.log(textStatus, errorThrown);
                    /* eslint-enable no-console */
                }
            }
        );
    });
});

function BookViewModel() {
    this.expanded = false;
}

const BookModel = Backbone.Model.extend({
    defaults: {
        'title': null,
        'authors': [],
    }
});

const BookView = Backbone.View.extend({
    tagName: 'div',

    events: {
        'click .expand-book': 'expandBook'
    },

    initialize: function() {
        // Ensure that 'this' for render points to its view.
        _.bindAll(this, 'render');

        this.model.bind('change', this.render);

        this.viewModel = new BookViewModel();
    },

    template: _.template($('#bookTableTemplate').html()),

    render: function() {
        var element = $(this.el);

        // Clear existing row data if needed
        element.empty();

        element.html(this.template(this.model.toJSON()));

        return this;
    },

    expandBook: function() {
        if (this.viewModel.expanded) {
            $('#expand-' + this.model.get('id')).empty();
        }
        else {
            var t = new BookExpandView({
                el: '#expand-' + this.model.get('id'),
                model: this.model
            });
    
            t.render();
        }

        this.viewModel.expanded = !this.viewModel.expanded;
    }
});

const BooksCollection = Backbone.Collection.extend({
    model: BookModel,

    url: SERVER_URL + '/books'
});

const BooksView = Backbone.View.extend({
    el: '#libraryBind',

    initialize: function (options) {
        // TODO checked.
        this.collection = options.collection;

        _.bindAll(this, 'render');

        this.collection.bind('reset', this.render);
        this.collection.bind('add', this.render);
        this.collection.bind('remove', this.render);
    },

    render: function () {
        var element = this.$el;
        
        element.empty();

        this.collection.forEach(function (item) {
            var bookViewItem = new BookView({
                model: item
            });

            element.append(bookViewItem.render().el);
        });

        return this;
    },

    // Store the collection this view manages.
    collection: null,
});

const BookExpandView = Backbone.View.extend({
    initialize: function () {
        _.bindAll(this, 'render');

        this.model.bind('change', this.render);
    },

    template: _.template($('#bookTemplate').html()),

    render: function() {
        this.$el.empty();
        this.$el.html(this.template(this.model.toJSON()));

        return this;
    }
});
