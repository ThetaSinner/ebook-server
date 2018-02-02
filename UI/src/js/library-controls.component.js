import React from 'react';

import CreateLibraryControl from './create-library-control.component';
import LoadControl from './load-control.component';
import SaveControl from './save-control.component';
import UploadControl from './upload-control.component';
import AddBookControl from './add-book-control.component';

export default class LibraryControls extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const createLibrary = this.props.service.createLibrary;
        const loadLibrary = this.props.service.loadLibrary;
        const saveLibrary = this.props.service.saveLibrary;
        const addBook = this.props.service.addBook;
        const uploadFiles = this.props.service.uploadFiles;
        return (
            <>
                <div className="d-inline mr-5">
                    <span>Library </span>

                    <div className="d-inline mr-1">
                        <CreateLibraryControl createLibrary={createLibrary} />
                    </div>
                    <div className="d-inline mr-1">
                        <LoadControl loadLibrary={loadLibrary} />
                    </div>
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
    }
}
