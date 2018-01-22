import React from 'react';

export default class Book extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const book = this.props.book;
        return (
            <p>{book.title}</p>
        );
    }
}