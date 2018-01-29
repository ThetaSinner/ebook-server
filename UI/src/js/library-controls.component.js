import React from 'react';

import LoadControl from './load-control.component';

export default class LibraryControls extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const loadLibrary = this.props.service.loadLibrary;
        return (
            <div>
                <LoadControl loadLibrary={loadLibrary} />
                <button type="button" className="btn btn-primary mr-1">Save</button>
            </div>
        );
    }
}
