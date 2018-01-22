import React from 'react';
import * as _ from 'lodash';

export default class BookSummary extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const book = this.props.book;
        return (
            <div className="row es-book-row">
                <div className="col-sm-2">{book.title}</div>
                <div className="col-sm-2">{_.isArray(book.authors) ? _.join(book.authors, ', ') : book.authors}</div>
                <div className="col-sm-2">{book.publisher}</div>
                <div className="col-sm-1">{book.datePublished}</div>
                <div className="col-sm-2">{book.isbn}</div>
                <div className="col-sm-1">{book.metadata.rating}</div>
                <div className="col-sm-1">{_.isArray(book.metadata.tags) ? _.join(book.metadata.tags, ', ') : book.metadata.tags}</div>
                <div className="col-sm-1 es-icon-button" aria-label="Expand Book" onClick={this.props.onExpandRow}>
                    <i className="material-icons" aria-hidden="true">{this.props.expanded ? 'expand_less' : 'expand_more'}</i>
                </div>
            </div>
        );
    }
}
