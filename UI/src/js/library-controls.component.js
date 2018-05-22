import React from 'react';

/* eslint-disable no-unused-vars */
import UploadControl from './upload-control.component';
import AddBookControl from './add-book-control.component';
import LibraryInfo from './library-info.component';
import LibraryFilter from './library-filter.component';
/* eslint-enable no-unused-vars */

export default class LibraryControls extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            isFiltering: false
        };

        this.toggleFilter = this.toggleFilter.bind(this);
    }

    render() {
        const saveLibrary = this.props.service.saveLibrary;

        const addBook = this.props.service.addBook;
        const uploadFiles = this.props.service.uploadFiles;

        const startSelectingLibrary = this.props.service.startSelectingLibrary;

        const libraryData = this.props.libraryData;

        const toggleFilter = this.toggleFilter;
        const isFiltering = this.state.isFiltering;

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
                    <div className="d-inline">
                        <div className="d-inline" onClick={toggleFilter}>
                            <i className={"material-icons es-icon-button-large" + (isFiltering ? " es-icon-button-toggled" : "")} aria-hidden="true" data-toggle="tooltip" data-placement="bottom" title="Toggle filter">filter_list</i>
                        </div>
                    </div>
                </div>

                <div className="d-inline float-right mr-3">
                    <LibraryInfo libraryData={libraryData} />
                </div>

                {isFiltering &&
                    <div>
                        <LibraryFilter />
                    </div>
                }
            </>
        );
        /* eslint-enable quotes */
    }

    toggleFilter() {
        this.setState(prevState => ({
            isFiltering: !prevState.isFiltering
        }));
    }
}
