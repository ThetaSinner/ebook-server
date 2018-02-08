import React from 'react';

/* eslint-disable no-unused-vars */
import LibraryItem from './library-item.component';
/* eslint-enable no-unused-vars */

export default class Library extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        if (!this.props.books) {
            return <p>No books here, open a library or add some books!</p>;
        }

        const service = this.props.service;
        return (
            <div>
                <div className="row es-book-header-row">
                    <div className="col-sm-2">Title</div>
                    <div className="col-sm-2">Authors</div>
                    <div className="col-sm-2">Publisher</div>
                    <div className="col-sm-1">Date</div>
                    <div className="col-sm-2">ISBN</div>
                    <div className="col-sm-1">Rating</div>
                    <div className="col-sm-1">Tags</div>
                    {/* one left over for expand button */}
                </div>
                {this.props.books.map((book) =>
                    <LibraryItem key={book.id} book={book} service={service} />
                )}
            </div>
        );
    }
}
