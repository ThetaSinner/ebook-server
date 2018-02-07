var TestData = {
    testLibraryA: {
        books: [
            {
                id: 'asd987asdf908-asdf98-asdfhjkl23j',
                isbn: '2039482394820',
                title: 'bubbles',
                authors: ['Gabe Newell', 'Charlie Cavendish'],
                publisher: 'Action Press',
                datePublished: new Date(),
                description: 'This is a book about bubbles by some random people who I don\'t think write books',
                metadata: {
                    tags: ['fiction', 'futuristic'],
                    rating: 4
                }
            },
            {
                id: 'dsfg098dsfg89-sadfkiu89o-sd9f898sa',
                isbn: '9802340982349',
                title: 'grapes',
                authors: 'Andy Warhol',
                publisher: 'Puffin',
                datePublished: new Date(),
                description: 'An abstract novel on the topic of grapes by a strange and wonderful artist',
                metadata: {
                    tags: 'non-fiction',
                    rating: 2
                }
            }
        ]
    },
    testLibraryB: {
        books: [
            {
                id: 'tgd987asdf568-asdf28-asdfhtkl23j',
                isbn: '2039482355820',
                title: 'The taming of the boo',
                authors: ['Boo Meister'],
                publisher: 'Orion',
                datePublished: new Date(),
                description: 'This is a great modern retelling.',
                metadata: {
                    tags: ['fiction', 'romance'],
                    rating: 3
                }
            },
            {
                id: 'tgd987modf568-asdf28-as3phtkl23j',
                isbn: '2038882355820',
                title: 'The wheening of the winkle',
                authors: ['Aaron Simon'],
                publisher: 'Addison-Wesley',
                datePublished: new Date(),
                description: 'I wonder what on earth this could be about.',
                metadata: {
                    tags: ['fiction', 'sci-fi'],
                    rating: 4
                }
            },
            {
                id: 'dsfg498dsfg89-sadfkiu89o-lt9f898sa',
                isbn: '9802369982349',
                title: 'Who killed Tyrion',
                authors: 'Meister Martin',
                publisher: 'Bloomsbury',
                datePublished: new Date(),
                description: 'An even worse story than you might imagine',
                metadata: {
                    tags: 'fantasy',
                    rating: 1
                }
            }
        ]
    }
};

import $ from 'jquery';
import { formatDate } from './formatter';

export default class EBookDataService {
    constructor(serverUrl) {
        this.token = null;
        // The server supports multiple libraries being open, just need to support switching on the UI.
        this.activeLibraryName = null;
        this.serverUrl = serverUrl;

        this._getBooks = this._getBooks.bind(this);
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
                }
            }).done((response) => {
                that.token = response.token;
                that.activeLibraryName = libraryName;
                resolve();
            }).fail((jqXHR, textStatus) => {
                reject(textStatus);
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
                }
            }).done((response) => {
                that.token = response.token;
                that.activeLibraryName = libraryName;
                resolve();
            }).fail((jqXHR, textStatus) => {
                reject(textStatus);
            });
        }).then(this._getBooks);
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
                }
            }).done(() => {
                resolve();
            }).fail((jqXHR, textStatus) => {
                reject(textStatus);
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
                dataType: "json",
                data: JSON.stringify({
                    token: that.token,
                    name: that.activeLibraryName,
                    request: {
                        url: url,
                        type: type
                    }
                })
            }).done((response) => {
                resolve(response);
            }).fail((jqXHR, textStatus) => {
                reject(textStatus);
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
                contentType: false
            }).done(function (data) {
                console.log(data);
                resolve();
            }).fail(function (err) {
                alert(err);
                reject();
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
                contentType: 'application/json',
                dataType: "json",
                data: JSON.stringify({
                    token: that.token,
                    name: that.activeLibraryName,
                    request: updateRequest
                })
            }).done(function (data) {
                console.log(data);
                resolve();
            }).fail(function (err) {
                alert(err);
                reject();
            });
        }).then(this._getBooks);
    }

    deleteBook(id) {
        console.log('Will delete that book right away', id);

        return Promise.resolve();
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
                }
            }).done((result) => {
                resolve(result);
            }).fail((jqXHR, textStatus) => {
                reject(textStatus);
            });
        });
    }
}
