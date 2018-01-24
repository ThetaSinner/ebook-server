import React from 'react';

import LibraryItem from './library-item.component';

export default class Library extends React.Component {
    constructor(props) {
        super(props);

        
    }

    render() {
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
                    <LibraryItem key={book.id} book={book} />
                )}
            </div>
        );
    }
}
