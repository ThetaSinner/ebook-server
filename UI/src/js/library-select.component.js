import React from 'react';

/* eslint-disable no-unused-vars */
import CreateLibraryControl from './create-library-control.component';
/* eslint-enable no-unused-vars */

export default class LibrarySelect extends React.Component {
    constructor(props) {
        super(props);

        this.navigateToLibrary = this.navigateToLibrary.bind(this);
    }

    render() {
        // TODO figure out how to tell if the server is running.
        if (this.props.libraries.length === 0) {
            return <p>No libraries found, is the server up?</p>;
        }

        const createLibrary = this.props.createLibrary;

        return (
            <div className="container">
                <h3>Which library would you like to open?</h3>
                {this.props.libraries.map((library) =>
                    <div key={library} className="row es-row-highlight">
                        <div className="col-sm-12" onClick={() => this.navigateToLibrary(library)}>
                            <span>{library}</span>
                        </div>
                    </div>
                )}
                <div className="row">
                    <div className="col-sm-12">
                        <CreateLibraryControl createLibrary={createLibrary} />
                    </div>
                </div>
            </div>
        );
    }

    navigateToLibrary(library) {
        this.props.navigateToLibrary(library);
    }
}
