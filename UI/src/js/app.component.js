import React from 'react';
import ReactDOM from 'react-dom';
import $ from 'jquery';

/* eslint-disable no-unused-vars */
import Library from './library.component';
import LibraryControls from './library-controls.component';
import LibrarySelect from './library-select.component';
import applyFilterQuery from './filter-query.service';
/* eslint-enable no-unused-vars */

import EBookDataService from './ebook-data.service';

export default class App extends React.Component {
    constructor(props) {
        super(props);

        this.listLibraries = this.listLibraries.bind(this);
        this.createLibrary = this.createLibrary.bind(this);
        this.navigateToLibrary = this.navigateToLibrary.bind(this);
        this.saveLibrary = this.saveLibrary.bind(this);
        this.addBook = this.addBook.bind(this);
        this.uploadFiles = this.uploadFiles.bind(this);
        this.updateFilterQuery = this.updateFilterQuery.bind(this);
        this.getCover = this.getCover.bind(this);

        this.deleteBook = this.deleteBook.bind(this);
        this.updateBook = this.updateBook.bind(this);

        this.startSelectingLibrary = this.startSelectingLibrary.bind(this);
        this.getLoadedLibraries = this.getLoadedLibraries.bind(this);

        this.getReadLink = this.getReadLink.bind(this);
        this.getCoverLink = this.getCoverLink.bind(this);

        this.state = {
            selectingLibrary: true,
            libraries: [],
            librariesRequestOk: false,
            books: null,
            filterQuery: null,
            filteredBooks: null
        };
    }

    componentDidMount() {
        this.listLibraries().then((libraries, requestOk) => {
            this.setState({
                libraries: libraries,
                librariesRequestOk: requestOk
            });
        }).catch(err => {
            console.error('There was a problem getting the list of libraries', err)
            this.setState({
                libraries: [],
                librariesRequestOk: false
            });
        });
    }

    render() {
        const controlsService = {
            loadLibrary: this.loadLibrary,
            saveLibrary: this.saveLibrary,
            addBook: this.addBook,
            uploadFiles: this.uploadFiles,
            startSelectingLibrary: this.startSelectingLibrary,
            updateFilterQuery: this.updateFilterQuery
        };

        const libraryService = {
            updateBook: this.updateBook,
            deleteBook: this.deleteBook,
            getReadLink: this.getReadLink,
            getCover: this.getCover,
            getCoverLink: this.getCoverLink
        };

        const libraryData = {
            name: this.props.dataService.getLibraryDisplayName(),
            numberOfBooks: this.state.books ? this.state.books.length : 0
        };

        const selectingLibrary = this.state.selectingLibrary;

        const books = this.state.filteredBooks ? this.state.filteredBooks : this.state.books;

        /* eslint-disable quotes */
        return (
            <>
                {selectingLibrary &&                 
                    <LibrarySelect 
                        libraries={this.state.libraries}
                        librariesRequestOk={this.state.librariesRequestOk}
                        
                        navigateToLibrary={this.navigateToLibrary}
                        createLibrary={this.createLibrary}
                    />
                }
                {!selectingLibrary && 
                    <>
                        <LibraryControls service={controlsService} libraryData={libraryData} />
                        <Library books={books} service={libraryService} />
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
            return this.props.dataService.listLibraries();
        }).then((libraries, requestOk) => {
            this.setState({
                libraries: libraries,
                librariesRequestOk: requestOk
            });
        });
    }

    navigateToLibrary(name) {
        this.setState({
            selectingLibrary: false
        });

        this.props.dataService.setActiveLibraryName(name);
        return this.props.dataService.getBooks().then((books) => {
            if (!books) {
                return Promise.reject('Library not found');
            }

            this.setState({
                books: books
            });
        });
    }

    saveLibrary() {
        // TODO notify somehow when this suceeds
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
        return this.props.dataService.uploadFiles(files).then(() => {
            return this.props.dataService.getBooks();
        }).then((books) => {
            if (!books) {
                return Promise.reject('Library not found');
            }

            this.setState({
                books: books
            });
        });
    }

    updateBook(updatedBook) {
        return this.props.dataService.updateBook(updatedBook).then(() => {
            return this.props.dataService.getBooks();
        }).then((books) => {
            if (!books) {
                return Promise.reject('Library not found');
            }

            this.setState({
                books: books
            });
        });
    }

    deleteBook(id) {
        return this.props.dataService.deleteBook(id).then(() => {
            return this.props.dataService.getBooks();
        }).then((books) => {
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
            selectingLibrary: true,
            books: []
        });
    }

    getLoadedLibraries() {
        return this.props.dataService._getLoadedLibraries();
    }

    getReadLink(id, title) {
        if (!this.state.books) {
            return null;
        }

        const book = this.state.books.find(book => {
            return book.id === id;
        });

        if (!book) {
            return null;
        }

        if (book.url.type === 'LocalManaged' || book.url.type === 'LocalUnmanaged') {
            return this.props.dataService.getLocalReadLink(id, title);
        }
        else if (book.url.type === 'WebLink') {
            return book.url.value;
        }

        // TODO Need to just print the "other" type links somewhere on the page.

        return null;
    }

    updateFilterQuery(query) {
        if (!query) {
            this.setState({
                filterQuery: null,
                filteredBooks: null
            });

            return;
        }

        const resultBooks = applyFilterQuery(query, this.state.books);

        this.setState({
            filterQuery: query,
            filteredBooks: resultBooks
        });
    }

    getCover(id) {
        return this.props.dataService.getCover(id);
    }

    getCoverLink(id) {
        return this.props.dataService.getCoverLink(id);
    }
}

$(function() {
    const dataService = new EBookDataService('http://localhost:8121');

    ReactDOM.render(
        <App dataService={dataService} />,
        document.getElementById('app')
    );
});
