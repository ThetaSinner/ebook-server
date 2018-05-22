import React from 'react';

/* eslint-disable no-unused-vars */
import UploadControl from './upload-control.component';
import AddBookControl from './add-book-control.component';
import LibraryInfo from './library-info.component';
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

        const libraryData = this.props.libraryData;

        /* eslint-disable quotes */
        return (
            <>
                <div className="d-inline mr-5" onClick={startSelectingLibrary}>
                    <i className="material-icons es-icon-button-large" aria-hidden="true">navigate_before</i>
                </div>
                <div className="d-inline mr-5">
                    <div className="d-inline mr-1">
                        <i className="material-icons es-icon-button-large" aria-hidden="true" onClick={saveLibrary} data-toggle="tooltip" data-placement="bottom" title="Save library">save</i>
                    </div>
                </div>
                
                <div className="d-inline">
                    <div className="d-inline mr-1">
                        <AddBookControl addBook={addBook} />
                    </div>
                    <div className="d-inline mr-1">
                        <UploadControl uploadFiles={uploadFiles} />
                    </div>
                </div>

                <div className="d-inline float-right mr-3">
                    <LibraryInfo libraryData={libraryData} />
                </div>
            </>
        );
        /* eslint-enable quotes */
    }
}
