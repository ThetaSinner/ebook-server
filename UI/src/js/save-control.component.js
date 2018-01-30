import React from 'react';

export default class SaveControl extends React.Component {
    constructor(props) {
        super(props);

        this.saveLibrary = this.saveLibrary.bind(this);
    }

    render() {
        return (
            <button type="button" className="btn btn-primary" onClick={this.saveLibrary}>Save</button>
        );
    }

    saveLibrary(e) {
        e.preventDefault();

        this.props.saveLibrary();
    }
}