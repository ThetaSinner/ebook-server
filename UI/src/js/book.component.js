import React from 'react';
import * as _ from 'lodash';

import { formatDate } from './formatter';

export default class Book extends React.Component {
    constructor(props) {
        super(props);

        this.editBook = this.editBook.bind(this);
        this.deleteBook = this.deleteBook.bind(this);
    }

    render() {
        const book = this.props.book;
        /* eslint-disable quotes */
        return (
            <>
                <div className="container-fluid card">
                    <div className="row">
                        <div className="col-sm-12">
                            <div className="es-title-line">
                                <h3>{book.title}</h3>
                                <span className="es-faded-text">{formatDate(book.datePublished)}</span>

                                <div className="float-right">
                                    <i className="mr-2 mt-1 material-icons es-icon-button-large" aria-hidden="true" onClick={this.editBook}>mode_edit</i>
                                    <i className="mt-1 material-icons es-icon-button-large" aria-hidden="true" data-toggle="modal" data-target="#confirmDeleteModal">delete</i>
                                </div>
                            </div>
                            <p><span className="es-faded-text">by  </span>
                                {this.toAuthorList(book.authors).map((author, index) => (
                                    <span key={book.authors[index]} className="es-author">{author}</span>
                                ))}
                            </p>
                            <p><span className="es-faded-text">Publisher </span><span>{book.publisher}</span></p>
                        </div>
                    </div>
                    <div className="row">
                        <div className="col-sm-3">
                            <img alt="No cover" src="http://via.placeholder.com/250x375" />
                        </div>
                        <div className="col-sm-9">
                            <p>{book.description}</p>
                        </div>
                    </div>
                </div>
                
                <div className="modal fade" id="confirmDeleteModal" tabIndex="-1" role="dialog" aria-labelledby="confirmDeleteModalLabel" aria-hidden="true">
                    <div className="modal-dialog" role="document">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title" id="confirmDeleteModalLabel">Are you sure?</h5>
                                <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" data-dismiss="modal">Cancel</button>
                                <button type="button" className="btn btn-primary" data-dismiss="modal" onClick={this.deleteBook}>Delete</button>
                            </div>
                        </div>
                    </div>
                </div>
            </>
        );
        /* eslint-enable quotes */
    }

    toAuthorList(authors) {
        if (!authors) {
            return [];
        }

        var result = _.uniq(_.castArray(authors)).slice();

        if (result.length <= 1) {
            return result;
        }

        var back = result.pop();
        result.forEach((author, index, arr) => arr[index] = author + ',');
        result.push(back);

        return result;
    }

    editBook(e) {
        e.preventDefault();

        this.props.editBook();
    }

    deleteBook() {
        this.props.deleteBook(this.props.book.id);
    }
}