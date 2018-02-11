import React from 'react';

export default class LibrarySelect extends React.Component {
    constructor(props) {
        super(props);

        this.loadLibrary = this.loadLibrary.bind(this);
    }

    render() {
        // TODO figure out how to tell if the server is running.
        if (this.props.libraries.length === 0) {
            return <p>No libraries found, is the server up?</p>;
        }

        return (
            <div className="container">
                {this.props.libraries.map((library) =>
                    <div key={library} className="row es-row-highlight">
                        <div className="col-sm-11" onClick={() => this.loadLibrary(library)}>
                            <span>{library}</span>
                        </div>
                        <div className="col-sm-1">
                            <i className="material-icons" aria-hidden="true">
                                {this.props.loadedLibraries.indexOf(library) != -1 ? 'lock_open' : 'lock'}
                            </i>
                        </div>
                    </div>
                )}
            </div>
        );
    }

    loadLibrary(library) {
        this.props.loadLibrary(library);
    }
}
