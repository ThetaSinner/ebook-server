import React from 'react';

export default class LibrarySelect extends React.Component {
    constructor(props) {
        super(props);

        this.loadLibrary = this.loadLibrary.bind(this);
    }

    render() {
        return (
            <div className="container">
                {this.props.libraries.map((library) =>
                    <div key={library} className="row es-row-highlight" onClick={() => this.loadLibrary(library)}>
                        <div className="col-sm-11">
                            <span>{library}</span>
                        </div>
                        <div className="col-sm-1">
                            <i className="material-icons es-icon-button" aria-hidden="true">
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
