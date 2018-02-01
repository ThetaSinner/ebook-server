import React from 'react';

import LoadControl from './load-control.component';
import SaveControl from './save-control.component';
import UploadControl from './upload-control.component';
import AddBookControl from './add-book-control.component';

export default class LibraryControls extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const loadLibrary = this.props.service.loadLibrary;
        const saveLibrary = this.props.service.saveLibrary;
        const addBook = this.props.service.addBook;
        const uploadFiles = this.props.service.uploadFiles;
        return (
            <>
                <div className="d-inline mr-1">
                    <LoadControl loadLibrary={loadLibrary} />
                </div>
                <div className="d-inline mr-1">
                    <SaveControl saveLibrary={saveLibrary} />
                </div>
                <div className="d-inline mr-1">
                    <AddBookControl addBook={addBook} />
                </div>
                <div className="d-inline mr-1">
                    <UploadControl uploadFiles={uploadFiles} />
                </div>
            </>
        );
    }
}
