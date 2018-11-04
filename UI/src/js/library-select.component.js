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
        const createLibrary = this.props.createLibrary;

        if (this.props.libraries.length === 0) {
            if (this.props.librariesRequestOk) {
                return (
                    <div className="container">
                        <h5>It looks like you don't have any libraries yet, you can create one now!</h5>
                        <div className="row">
                            <div className="col-sm-12">
                                <CreateLibraryControl createLibrary={createLibrary} />
                            </div>
                        </div>
                    </div>
                );
            }
            
            return <p>Could not get libraries from the server. Please check that it is running then reload this page!</p>;
        }

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
