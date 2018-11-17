import React from 'react';

/* eslint-disable no-unused-vars */
import BookSummary from './book-summary.component';
import BookEditor from './book-editor.component';
import Book from './book.component';
/* eslint-enable no-unused-vars */

export default class LibraryItem extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            showBook: false,
            isEditing: false
        };

        this.onExpandRow = this.onExpandRow.bind(this);
        this.editBook = this.editBook.bind(this);
        this.deleteBook = this.deleteBook.bind(this);
        this.saveBook = this.saveBook.bind(this);
        this.cancelEdit = this.cancelEdit.bind(this);
    }

    render() {
        const showBook = this.state.showBook;
        const isEditing = this.state.isEditing;
        const getReadLink = this.props.service.getReadLink;
        const getCover = this.props.service.getCover;
        const getCoverLink = this.props.service.getCoverLink;
        const book = this.props.book;
        return (
            <div className="row">
                <div className="col-sm-12">
                    <BookSummary book={book} expanded={showBook} onExpandRow={this.onExpandRow} />
                    <div className="row">
                        <div className="col-sm-12">
                            {(showBook && !isEditing) &&
                                <Book book={book} editBook={this.editBook} deleteBook={this.deleteBook} getReadLink={getReadLink} getCover={getCover} getCoverLink={getCoverLink} />
                            }
                            {isEditing &&
                                <BookEditor book={book} saveBook={this.saveBook} cancelEdit={this.cancelEdit} />
                            }
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    onExpandRow() {
        this.setState((prevState) => ({
            showBook: !prevState.showBook
        }));
    }

    editBook() {
        this.setState({
            isEditing: true
        });
    }

    deleteBook(id) {
        this.props.service.deleteBook(id);
    }

    saveBook(updatedBook) {
        // TODO check that is has actually been changed?

        this.props.service.updateBook(updatedBook).then(() => {
            this.setState({
                isEditing: false
            });
        });
    }

    cancelEdit() {
        this.setState({
            isEditing: false
        });
    }
}