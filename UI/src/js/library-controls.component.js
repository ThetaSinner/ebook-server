import React from 'react';

import LoadControl from './load-control.component';
import UploadControl from './upload-control.component';

export default class LibraryControls extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const loadLibrary = this.props.service.loadLibrary;
        const uploadFiles = this.props.service.uploadFiles;
        return (
            <div>
                <LoadControl loadLibrary={loadLibrary} />
                <UploadControl uploadFiles={uploadFiles} />
                <button type="button" className="btn btn-primary mr-1">Save</button>
            </div>
        );
    }
}
