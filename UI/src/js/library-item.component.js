import React from 'react';

import BookSummary from './book-summary.component';
import Book from './book.component';

export default class LibraryItem extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            showBook: false
        };

        this.onExpandRow = this.onExpandRow.bind(this);
    }

    render() {
        const showBook = this.state.showBook;
        const book = this.props.book;
        return (
            <div>
                <BookSummary book={book} expanded={showBook} onExpandRow={this.onExpandRow} />
                {showBook &&
                    <Book book={book} />
                }
            </div>
        );
    }

    onExpandRow() {
        this.setState((prevState, props) => ({
            showBook: !prevState.showBook
        }));
    }
}