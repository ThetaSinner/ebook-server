import $ from 'jquery';
import jwt_decode from 'jwt-decode';

function processAjaxError(jqXHR) {
    if (jqXHR.statusText === 'timeout') {
        return 'Server is not alive.';
    }
    else if (jqXHR.responseJSON && jqXHR.responseJSON.errorMessage) {
        return jqXHR.responseJSON.errorMessage;
    }
    else {
        return 'Unknown error';
    }
}

export default class EBookDataService {
    constructor(serverUrl) {
        this.token = null;
        // The server supports multiple libraries being open, just need to support switching on the UI.
        this.activeLibraryName = null;
        this.serverUrl = serverUrl;

        this._getBooks = this._getBooks.bind(this);
    }

    listLibraries() {
        return new Promise((resolve, reject) => {
            $.ajax({
                url: 'http://localhost:8080/libraries',
                type: 'GET',
                timeout: 1500
            }).done((response) => {
                resolve(response);
            }).fail((jqXHR) => {
                reject(processAjaxError(jqXHR));
            });
        });
    }

    createLibrary(libraryName) {
        const that = this;
        return new Promise((resolve, reject) => {
            $.ajax({
                url: 'http://localhost:8080/create',
                type: 'POST',
                data: {
                    token: that.token,
                    name: libraryName
                },
                timeout: 3000
            }).done((response) => {
                that.token = response.token;
                that.activeLibraryName = libraryName;
                resolve();
            }).fail((jqXHR) => {
                reject(processAjaxError(jqXHR));
            });
        });
    }

    loadLibrary(libraryName) {
        const that = this;
        return new Promise((resolve, reject) => {
            $.ajax({
                url: 'http://localhost:8080/load',
                type: 'GET',
                data: {
                    token: that.token,
                    name: libraryName
                },
                timeout: 3000
            }).done((response) => {
                that.token = response.token;
                that.activeLibraryName = libraryName;
                var decoded = jwt_decode(that.token);
                resolve(decoded && decoded.libraries ? decoded.libraries : []);
            }).fail((jqXHR) => {
                reject(processAjaxError(jqXHR));
            });
        });
    }

    saveLibrary() {
        const that = this;
        return new Promise((resolve, reject) => {
            $.ajax({
                url: 'http://localhost:8080/save',
                type: 'GET',
                data: {
                    token: that.token,
                    name: that.activeLibraryName
                },
                timeout: 3000
            }).done(() => {
                resolve();
            }).fail((jqXHR) => {
                reject(processAjaxError(jqXHR));
            });
        });
    }

    addBook(url, type) {
        const that = this;
        return new Promise((resolve, reject) => {
            $.ajax({
                url: 'http://localhost:8080/books',
                type: 'POST',
                contentType: 'application/json',
                dataType: 'json',
                data: JSON.stringify({
                    token: that.token,
                    name: that.activeLibraryName,
                    request: {
                        url: url,
                        type: type
                    }
                }),
                timeout: 3000
            }).done((response) => {
                resolve(response);
            }).fail((jqXHR) => {
                reject(processAjaxError(jqXHR));
            });
        });
    }

    uploadFiles(files) {
        const formData = new FormData();
        for (var i = 0; i < files.length; i++) {
            formData.append('files', files.item(i));
        }

        formData.append('token', this.token);
        formData.append('name', this.activeLibraryName);
        
        return new Promise((resolve, reject) => {
            $.ajax({
                url: 'http://localhost:8080/upload',
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                timeout: 3000
            }).done(function (/*data*/) {
                // TODO retry if any files failed? or notify
                resolve();
            }).fail(function (jqXHR) {
                reject(processAjaxError(jqXHR));
            });
        }).then(this._getBooks);
    }

    updateBook(book) {
        var updateRequest = {};
        if (book.title) {
            updateRequest.title = book.title;
        }
        if (book.authors) {
            updateRequest.authors = book.authors;
        }
        if (book.publisher) {
            updateRequest.publisher = book.publisher;
        }
        if (book.datePublished) {
            updateRequest.datePublished = book.datePublished;
        }

        var metadata = book.metadata;
        if (metadata) {
            updateRequest.bookMetadataUpdateRequest = {};
            
            if (metadata.tags) {
                updateRequest.bookMetadataUpdateRequest.tags = metadata.tags;
            }
            if (metadata.rating) {
                updateRequest.bookMetadataUpdateRequest.rating = metadata.rating;
            }
        }

        const that = this;
        return new Promise((resolve, reject) => {
            $.ajax({
                url: 'http://localhost:8080/books/' + book.id,
                type: 'PATCH',
                contentType: 'application/json', // TODO wrong content type for patch
                dataType: 'json',
                data: JSON.stringify({
                    token: that.token,
                    name: that.activeLibraryName,
                    request: updateRequest
                }),
                timeout: 3000
            }).done(function (/*updatedBook*/) {
                // TODO what to do with the updated book?
                resolve();
            }).fail(function (jqXHR) {
                reject(processAjaxError(jqXHR));
            });
        }).then(this._getBooks);
    }

    deleteBook(id) {
        const that = this;
        const params = $.param({
            token: that.token,
            name: that.activeLibraryName
        });
        return new Promise((resolve, reject) => {
            $.ajax({
                url: 'http://localhost:8080/books/' + id + '?' + params,
                type: 'DELETE',
                contentType: 'application/json',
                dataType: 'json',
                timeout: 3000
            }).done(function () {
                resolve();
            }).fail(function (jqXHR) {
                reject(processAjaxError(jqXHR));
            });
        }).then(this._getBooks);
    }

    _getBooks() {
        const that = this;
        return new Promise((resolve, reject) => {
            $.ajax({
                url: 'http://localhost:8080/books',
                type: 'GET',
                data: {
                    token: that.token,
                    name: that.activeLibraryName
                },
                timeout: 3000
            }).done((result) => {
                resolve(result);
            }).fail((jqXHR) => {
                reject(processAjaxError(jqXHR));
            });
        });
    }
}
