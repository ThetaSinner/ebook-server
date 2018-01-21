import React from 'react';
import ReactDOM from 'react-dom';
import $ from 'jquery';
// import * as _ from 'lodash';

import Library from './library.component';

export default class App extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            books: [
                {
                    id: 'asd987asdf908-asdf98-asdfhjkl23j',
                    isbn: '2039482394820',
                    title: 'bubbles',
                    authors: ['Gabe Newell', 'Charlie Cavendish'],
                    publisher: 'Action Press',
                    metadata: {
                        tags: ['fiction', 'futuristic'],
                        rating: 4
                    }
                },
                {
                    id: 'dsfg098dsfg89-sadfkiu89o-sd9f898sa',
                    isbn: '9802340982349',
                    title: 'grapes',
                    authors: 'Andy Warhol',
                    publisher: 'Puffin',
                    metadata: {
                        tags: 'non-fiction',
                        rating: 2
                    }
                }
            ]
        };
    }

    render() {
        return <Library books={this.state.books} />;
    }
}

$(function() {
    ReactDOM.render(
        <App />,
        document.getElementById('app')
    );
});
