import React from 'react';

export default class BookRow extends React.Component {
    constructor(props) {
        super(props);

        this.onExpandRow = this.onExpandRow.bind(this);
    }

    render() {
        const book = this.props.book;
        return (
            <div className="row es-book-row">
                <div className="col-sm-2">{book.title}</div>
                <div className="col-sm-2">{book.authors}</div>
                <div className="col-sm-2">{book.publisher}</div>
                <div className="col-sm-1">{book.datePublished}</div>
                <div className="col-sm-2">{book.isbn}</div>
                <div className="col-sm-1">{book.metadata.rating}</div>
                <div className="col-sm-1">{book.metadata.tags}</div>
                <div className="col-sm-1 es-icon-button" aria-label="Expand Book" onClick={this.onExpandRow}>
                    <i className="material-icons" aria-hidden="true">expand_more</i>
                </div>
            </div>
        );
    }

    onExpandRow() {
        this.props.onExpandRow(this.props.book.id);
    }
}
