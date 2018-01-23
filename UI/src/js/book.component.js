import React from 'react';
import * as _ from 'lodash';

export default class Book extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const book = this.props.book;
        return (
            <div className="container-fluid">
                <div className="container-fluid">
                    <h3>{book.title}</h3>
                    <p>by<span>  </span>
                        {_.uniq(_.castArray(book.authors)).map((author, index) => (
                            <span key={author} className="es-author">{author}</span>
                        ))}
                    </p>
                    <p>Publisher <span className="es-publisher">{book.publisher}</span></p>
                </div>
                <div className="row">
                    <div className="col-sm-3">
                        <img alt="No cover"/>
                    </div>
                    <div className="col-sm-9">
                        <p>{book.description}</p>
                    </div>
                </div>
            </div>
        );
    }
}