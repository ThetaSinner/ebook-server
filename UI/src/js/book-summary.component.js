import React from 'react';
import * as _ from 'lodash';
import $ from 'jquery';

import { formatDate, formatDateShort } from './formatter';
import DisplayHelper from './display-helper.service';

export default class BookSummary extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const displayHelper = new DisplayHelper();

        const book = this.props.book;
        const authors = displayHelper.toCommaList(book.authors);
        const tags = displayHelper.toCommaList(book.metadata && book.metadata.tags);
        return (
            <div className="row es-book-row">
                <div className="col-sm-2" data-toggle="tooltip" data-placement="bottom" title={book.title}>{book.title}</div>
                <div className="col-sm-2" data-toggle="tooltip" data-placement="bottom" title={authors}>{authors}</div>
                <div className="col-sm-2" data-toggle="tooltip" data-placement="bottom" title={book.publisher}>{book.publisher}</div>
                <div className="col-sm-1">{formatDateShort(book.datePublished)}</div>
                <div className="col-sm-2">{book.isbn}</div>
                <div className="col-sm-1">{book.metadata && book.metadata.rating}</div>
                <div className="col-sm-1" data-toggle="tooltip" data-placement="bottom" title={tags}>{tags}</div>
                <div className="col-sm-1 text-center es-icon-button" aria-label="Expand Book" onClick={this.props.onExpandRow}>
                    <i className="material-icons" aria-hidden="true">{this.props.expanded ? 'expand_less' : 'expand_more'}</i>
                </div>
            </div>
        );
    }

    componentDidMount() {
        $(document).ready(function(){
            $('[data-toggle="tooltip"]').tooltip();
        });
    }
}
