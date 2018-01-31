import React from 'react';
import ReactDOM from 'react-dom';
import $ from 'jquery';

import Library from './library.component';
import LibraryControls from './library-controls.component';

import EBookDataService from './ebook-data.service';

export default class App extends React.Component {
    constructor(props) {
        super(props);

        this.loadLibrary = this.loadLibrary.bind(this);
        this.saveLibrary = this.saveLibrary.bind(this);
        this.uploadFiles = this.uploadFiles.bind(this);

        this.updateBook = this.updateBook.bind(this);

        this.state = {
            controlsService: {
                loadLibrary: this.loadLibrary,
                saveLibrary: this.saveLibrary,
                uploadFiles: this.uploadFiles
            },
            libraryService: {
                updateBook: this.updateBook
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

    loadLibrary(name) {
        return this.props.dataService.loadLibrary(name).then((library) => {
            if (!library) {
                return Promise.reject('Library not found');
            }

            this.setState({
                books: library.books
            });
        });
    }

    saveLibrary() {
        return this.props.dataService.saveLibrary();
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
}

$(function() {
    const dataService = new EBookDataService();

    ReactDOM.render(
        <App dataService={dataService} />,
        document.getElementById('app')
    );
});
