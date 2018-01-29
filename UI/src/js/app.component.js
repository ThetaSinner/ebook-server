import React from 'react';
import ReactDOM from 'react-dom';
import $ from 'jquery';

import Library from './library.component';
import LibraryControls from './library-controls.component';

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
                    datePublished: new Date(),
                    description: 'This is a book about bubbles by some random people who I don\'t think write books',
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
                    datePublished: new Date(),
                    description: 'An abstract novel on the topic of grapes by a strange and wonderful artist',
                    metadata: {
                        tags: 'non-fiction',
                        rating: 2
                    }
                }
            ]
        };

        this.state.controlsService = {
            loadLibrary: this.loadLibrary
        };
    }

    render() {
        return (
            <div>
                <LibraryControls service={this.state.controlsService} />
                <Library books={this.state.books} /> 
            </div>
        );
    }

    loadLibrary(name) {
        return Promise.resolve('Sucess');
    }
}

$(function() {
    ReactDOM.render(
        <App />,
        document.getElementById('app')
    );
});
