import React from 'react';
import ReactDOM from 'react-dom';
import $ from 'jquery';

import Library from './library.component';
import LibraryControls from './library-controls.component';

import EBookDataService from './ebook-data.service';

export default class App extends React.Component {
    constructor(props) {
        super(props);

        this.createLibrary = this.createLibrary.bind(this);
        this.loadLibrary = this.loadLibrary.bind(this);
        this.saveLibrary = this.saveLibrary.bind(this);
        this.addBook = this.addBook.bind(this);
        this.uploadFiles = this.uploadFiles.bind(this);

        this.deleteBook = this.deleteBook.bind(this);
        this.updateBook = this.updateBook.bind(this);

        this.state = {
            controlsService: {
                createLibrary: this.createLibrary,
                loadLibrary: this.loadLibrary,
                saveLibrary: this.saveLibrary,
                addBook: this.addBook,
                uploadFiles: this.uploadFiles
            },
            libraryService: {
                updateBook: this.updateBook,
                deleteBook: this.deleteBook
            },
            books: null
        };
    }

    render() {
        return (
            <div>
                <LibraryControls service={this.state.controlsService} />
                <Library books={this.state.books} service={this.state.libraryService} />
            </div>
        );
    }

    createLibrary(name) {
        return this.props.dataService.createLibrary(name).then(() => {
            return this.loadLibrary(name);
        });
    }

    loadLibrary(name) {
        return this.props.dataService.loadLibrary(name).then((books) => {
            if (!books) {
                return Promise.reject('Library not found');
            }

            this.setState({
                books: books
            });
        });
    }

    saveLibrary() {
        return this.props.dataService.saveLibrary();
    }

    addBook(url) {
        return this.props.dataService.addBook(url).then(() => {
            let newBooks = this.state.books;
            newBooks.push({
                id: 'asdflk2j34098sdf-sfdlkj4353-sdf98' + Math.floor(Math.random() * 1000),
                url: url,
                metadata: {}
            });

            this.setState({
                books: newBooks
            });
        });
    }

    uploadFiles(files) {
        return this.props.dataService.uploadFiles(files);
    }

    updateBook(updatedBook) {
        return this.props.dataService.updateBook(updatedBook).then(() => {
            console.log('Overwriting book, dev only');

            this.state.books.forEach((book, index, books) => {
                if (book.id === updatedBook.id) {
                    books[index] = book;
                    
                    this.setState({
                        books: books
                    });
                }
            });
        });
    }

    deleteBook(id) {
        return this.props.dataService.deleteBook(id).then(() => {
            var newBooks = this.state.books.filter((book) => {
                return book.id !== id;
            });

            this.setState({
                books: newBooks
            });
        });
    }
}

$(function() {
    const dataService = new EBookDataService();

    ReactDOM.render(
        <App dataService={dataService} />,
        document.getElementById('app')
    );
});
