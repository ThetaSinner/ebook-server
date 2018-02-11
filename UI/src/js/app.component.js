import React from 'react';
import ReactDOM from 'react-dom';
import $ from 'jquery';

/* eslint-disable no-unused-vars */
import Library from './library.component';
import LibraryControls from './library-controls.component';
import LibrarySelect from './library-select.component';
/* eslint-enable no-unused-vars */

import EBookDataService from './ebook-data.service';

export default class App extends React.Component {
    constructor(props) {
        super(props);

        this.listLibraries = this.listLibraries.bind(this);
        this.createLibrary = this.createLibrary.bind(this);
        this.loadLibrary = this.loadLibrary.bind(this);
        this.saveLibrary = this.saveLibrary.bind(this);
        this.addBook = this.addBook.bind(this);
        this.uploadFiles = this.uploadFiles.bind(this);

        this.deleteBook = this.deleteBook.bind(this);
        this.updateBook = this.updateBook.bind(this);

        this.startSelectingLibrary = this.startSelectingLibrary.bind(this);
        this.getLoadedLibraries = this.getLoadedLibraries.bind(this);

        this.state = {
            selectingLibrary: true,
            libraries: [],
            loadedLibraries: [],
            books: null
        };
    }

    componentDidMount() {
        this.listLibraries().then((libraries) => {
            this.setState({
                libraries: libraries
            });
        });

        this.getLoadedLibraries().then((loadedLibraries) => {
            this.setState({
                loadedLibraries: loadedLibraries
            });
        });
    }

    render() {
        const controlsService = {
            createLibrary: this.createLibrary,
            loadLibrary: this.loadLibrary,
            saveLibrary: this.saveLibrary,
            addBook: this.addBook,
            uploadFiles: this.uploadFiles,
            startSelectingLibrary: this.startSelectingLibrary
        };

        const libraryService = {
            updateBook: this.updateBook,
            deleteBook: this.deleteBook
        };

        const selectingLibrary = this.state.selectingLibrary;

        /* eslint-disable quotes */
        return (
            <>
                {selectingLibrary &&                 
                    <LibrarySelect 
                        libraries={this.state.libraries} 
                        loadedLibraries={this.state.loadedLibraries} 
                        
                        loadLibrary={this.loadLibrary} 
                    />
                }
                {!selectingLibrary && 
                    <>
                        <LibraryControls service={controlsService} />
                        <Library books={this.state.books} service={libraryService} />
                    </>
                }
            </>
        );
        /* eslint-enable quotes */
    }

    listLibraries() {
        return this.props.dataService.listLibraries();
    }

    createLibrary(name) {
        return this.props.dataService.createLibrary(name).then(() => {
            this.setState({
                books: []
            });
        });
    }

    loadLibrary(name) {
        return this.props.dataService.loadLibrary(name).then((loadedLibraries) => {
            this.setState({
                loadedLibraries: loadedLibraries,
                selectingLibrary: false
            });

            return this.props.dataService._getBooks();
        }).then((books) => {
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

    addBook(url, type) {
        return this.props.dataService.addBook(url, type).then((newBook) => {
            let newBooks = this.state.books;
            newBooks.push(newBook);

            this.setState({
                books: newBooks
            });
        });
    }

    uploadFiles(files) {
        return this.props.dataService.uploadFiles(files).then((books) => {
            if (!books) {
                return Promise.reject('Library not found');
            }

            this.setState({
                books: books
            });
        });
    }

    updateBook(updatedBook) {
        return this.props.dataService.updateBook(updatedBook).then((books) => {
            if (!books) {
                return Promise.reject('Library not found');
            }

            this.setState({
                books: books
            });
        });
    }

    deleteBook(id) {
        return this.props.dataService.deleteBook(id).then((books) => {
            if (!books) {
                return Promise.reject('Library not found');
            }

            this.setState({
                books: books
            });
        });
    }

    startSelectingLibrary() {
        this.setState({
            selectingLibrary: true
        });
    }

    getLoadedLibraries() {
        return this.props.dataService._getLoadedLibraries();
    }
}

$(function() {
    const dataService = new EBookDataService();

    ReactDOM.render(
        <App dataService={dataService} />,
        document.getElementById('app')
    );
});
