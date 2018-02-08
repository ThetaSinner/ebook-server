import React from 'react';

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
            metadataTags: displayHelper.toCommaList(book.metadata && book.metadata.tags),
            metadataRating: book.metadata ? book.metadata.rating : ''
        };

        this.saveBook = this.saveBook.bind(this);
        this.cancelEdit = this.cancelEdit.bind(this);

        this.handleChange = this.handleChange.bind(this);
    }

    render() {
        return (
            <div className="container-fluid card">
                <div className="row">
                    <div className="col-sm-12">
                        <form>
                            <div className="form-group">
                                <label htmlFor="editIsbn">ISBN</label>
                                <input type="text" id="editIsbn" name="isbn" className="form-control" value={this.state.isbn} onChange={this.handleChange} />
                            </div>
                            <div className="form-group">
                                <label htmlFor="editTitle">Title</label>
                                <input type="text" id="editTitle" name="title" className="form-control" value={this.state.title} onChange={this.handleChange} />
                            </div>
                            <div className="form-group">
                                <label htmlFor="editAuthors">Authors</label>
                                <input type="text" id="editAuthors" name="authors" className="form-control" value={this.state.authors} onChange={this.handleChange} aria-describedby="editAuthorsHelp" />
                                <small id="editAuthorsHelp" className="form-text text-muted">Comma seperated list of authors</small>
                            </div>
                            <div className="form-group">
                                <label htmlFor="editPublisher">Publisher</label>
                                <input type="text" id="editPublisher" name="publisher" className="form-control" value={this.state.publisher} onChange={this.handleChange} />
                            </div>
                            <div className="form-group">
                                <label htmlFor="editDatePublished">Date Published</label>
                                <input type="text" id="editDatePublished" name="datePublished" className="form-control" value={this.state.datePublished} onChange={this.handleChange} />
                            </div>
                            <div className="form-group">
                                <label htmlFor="editDescription">Description</label>
                                <textarea id="editDescription" name="description" className="form-control" value={this.state.description} onChange={this.handleChange} />
                            </div>
                            <div className="form-group">
                                <label htmlFor="editTags">Tags</label>
                                <input type="text" id="editTags" name="metadataTags" className="form-control" value={this.state.metadataTags} onChange={this.handleChange} aria-describedby="editTagsHelp" />
                                <small id="editTagsHelp" className="form-text text-muted">Comma seperated list of tags</small>
                            </div>
                            <div className="form-group">
                                <label htmlFor="editRating">Rating</label>
                                <input type="number" id="editRating" name="metadataRating" className="form-control" value={this.state.metadataRating} onChange={this.handleChange} />
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

        try {
            let book = this.props.book;
            book.isbn = this.state.isbn;
            book.title = this.state.title;
            book.authors = displayHelper.fromCommaList(this.state.authors);
            book.publisher = this.state.publisher;
            book.datePublished = new Date(this.state.datePublished);
            book.description = this.state.description;
            book.metadata = {};
            book.metadata.tags = this.state.metadataTags;
            book.metadata.rating = this.state.metadataRating;

            this.props.saveBook(book);
        }
        catch (e) {
            alert(e);
        }
    }

    cancelEdit(e) {
        e.preventDefault();

        this.props.cancelEdit();
    }

    handleChange(e) {
        const name = e.target.name;
        this.setState({
            [name]: e.target.value
        });
    }
}