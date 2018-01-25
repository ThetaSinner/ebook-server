import React from 'react';
import * as _ from 'lodash';

import { formatDate } from './formatter';

export default class Book extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const book = this.props.book;
        return (
            <div className="container-fluid card">
                <div className="row">
                    <div className="col-sm-12">
                        <div className="es-title-line">
                            <h3>{book.title}</h3>
                            <span className="es-faded-text">{formatDate(book.datePublished)}</span>
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
        );
    }

    toAuthorList(authors) {
        var result = _.uniq(_.castArray(authors)).slice();

        if (result.length <= 1) {
            return result;
        }

        var back = result.pop();
        result.forEach((author, index, arr) => arr[index] = author + ',');
        result.push(back);

        return result;
    }
}