import React from 'react';

import BookRow from './book-row.component';

export default class Library extends React.Component {
    constructor(props) {
        super(props);

        this.onExpandRow = this.onExpandRow.bind(this);
    }

    render() {
        return (
            <div className="container">
                <div className="row">
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
                        <div key={book.id}>
                            <BookRow book={book} onExpandRow={this.onExpandRow} />
                        </div>
                    )}
            </div>
        );
    }

    onExpandRow(id) {
        alert('expand ' + id);
    }
}
