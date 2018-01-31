import React from 'react';
import * as _ from 'lodash';

import DisplayHelper from './display-helper.service';

export default class BookEditor extends React.Component {
    constructor(props) {
        super(props);

        let displayHelper = this.displayHelper = new DisplayHelper();

        const book = props.book;
        this.state = {
            isbn: book.isbn,
            title: book.title,
            authors: displayHelper.toCommaList(book.authors),
            publisher: book.publisher,
            datePublished: book.datePublished,
            description: book.description,
            metadataTags: displayHelper.toCommaList(book.metadata.tags),
            metadataRating: book.metadata.rating
        };

        this.saveBook = this.saveBook.bind(this);
        this.cancelEdit = this.cancelEdit.bind(this);

        this.handleTitleChange = this.handleTitleChange.bind(this);
        this.handleAuthorsChange = this.handleAuthorsChange.bind(this);
    }

    render() {
        return (
            <div className="container-fluid card">
                <div className="row">
                    <div className="col-sm-12">
                        <form>
                            <div className="form-group">
                                <label htmlFor="editTitle">Title</label>
                                <input type="text" id="editTitle" className="form-control" value={this.state.title} onChange={this.handleTitleChange} />
                            </div>
                            <div className="form-group">
                                <label htmlFor="editAuthors">Authors</label>
                                <input type="text" id="editAuthors" className="form-control" value={this.state.authors} onChange={this.handleAuthorsChange} aria-describedby="editAuthorsHelp" />
                                <small id="editAuthorsHelp" className="form-text text-muted">Comma seperated list of authors</small>
                            </div>
                        </form>

                        <div>
                            <i className="material-icons es-icon-button" aria-hidden="true" onClick={this.saveBook}>save</i>
                            <i className="material-icons es-icon-button" aria-hidden="true" onClick={this.cancelEdit}>cancel</i>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    saveBook(e) {
        e.preventDefault();

        let displayHelper = this.displayHelper;

        let book = this.props.book;
        book.isbn = this.state.isbn;
        book.title = this.state.title;
        book.authors = displayHelper.fromCommaList(this.state.authors);
        book.publisher = this.state.publisher;
        book.datePublished = this.state.datePublished;
        book.description = this.state.description;
        book.metadata.tags = this.state.metadataTags;
        book.metadata.rating = this.state.metadataRating;

        this.props.saveBook(book);
    }

    cancelEdit(e) {
        e.preventDefault();

        this.props.cancelEdit();
    }

    handleTitleChange(e) {
        this.setState({
            title: e.target.value
        });
    }

    handleAuthorsChange(e) {
        this.setState({
            authors: e.target.value
        });
    }
}