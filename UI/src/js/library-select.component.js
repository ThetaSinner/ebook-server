import React from 'react';

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

        return (
            <div className="container">
                {this.props.libraries.map((library) =>
                    <div key={library} className="row es-row-highlight">
                        <div className="col-sm-12" onClick={() => this.navigateToLibrary(library)}>
                            <span>{library}</span>
                        </div>
                    </div>
                )}
            </div>
        );
    }

    navigateToLibrary(library) {
        this.props.navigateToLibrary(library);
    }
}
