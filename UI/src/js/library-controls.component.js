import React from 'react';

/* eslint-disable no-unused-vars */
import SaveControl from './save-control.component';
import UploadControl from './upload-control.component';
import AddBookControl from './add-book-control.component';
/* eslint-enable no-unused-vars */

export default class LibraryControls extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const saveLibrary = this.props.service.saveLibrary;

        const addBook = this.props.service.addBook;
        const uploadFiles = this.props.service.uploadFiles;

        const startSelectingLibrary = this.props.service.startSelectingLibrary;

        /* eslint-disable quotes */
        return (
            <>
                <div className="d-inline mr-5" onClick={startSelectingLibrary}>
                    <i className="material-icons es-icon-button" aria-hidden="true">navigate_before</i>
                </div>
                <div className="d-inline mr-5">
                    <span>Library </span>

                    <div className="d-inline mr-1">
                        <SaveControl saveLibrary={saveLibrary} />
                    </div>
                </div>
                
                <div className="d-inline">
                    <span>Books </span>

                    <div className="d-inline mr-1">
                        <AddBookControl addBook={addBook} />
                    </div>
                    <div className="d-inline mr-1">
                        <UploadControl uploadFiles={uploadFiles} />
                    </div>
                </div>
            </>
        );
        /* eslint-enable quotes */
    }
}
