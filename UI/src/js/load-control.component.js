import React from 'react';
import $ from 'jquery';

/* eslint-disable no-unused-vars */
import ErrorText from './error-text.component';
/* eslint-enable no-unused-vars */

export default class LoadControl extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            libraryName: '',
            errorText: null
        };

        this.libraryNameChange = this.libraryNameChange.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
        this.handleLoadLibrary = this.handleLoadLibrary.bind(this);
    }

    render() {
        /* eslint-disable quotes */
        return (
            <>
                <button type="button" className="btn btn-primary" data-toggle="modal" data-target="#loadControlModal">Load</button>

                <div className="modal fade" id="loadControlModal" tabIndex="-1" role="dialog" aria-labelledby="loadControlModalLabel" aria-hidden="true">
                    <div className="modal-dialog" role="document">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title" id="loadControlModalLabel">Select a library to load</h5>
                                <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div className="modal-body">
                                <div className="mb-1">
                                    <ErrorText errorText={this.state.errorText} />
                                </div>
                                <form onSubmit={this.handleLoadLibrary}>
                                    <label htmlFor="loadLibraryName">Library name</label>
                                    <input type="text" name="loadLibraryName" id="loadLibraryNameId" placeholder="Library name" value={this.state.libraryName} onChange={this.libraryNameChange} className="form-control" />
                                </form>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" data-dismiss="modal" onClick={this.handleCancel}>Cancel</button>
                                <button type="button" className="btn btn-primary" onClick={this.handleLoadLibrary}>Ok</button>
                            </div>
                        </div>
                    </div>
                </div>
            </>
        );
        /* eslint-enable quotes */
    }

    libraryNameChange(e) {
        this.setState({
            libraryName: e.target.value,
            errorText: null
        });
    }

    handleCancel() {
        this.setState({
            errorText: null
        });
    }

    handleLoadLibrary() {
        this.props.loadLibrary(this.state.libraryName).then(function () {
            $('#loadControlModal').modal('hide');

            this.setState({
                libraryName: '',
                errorText: null
            });
        }).catch((err) => {
            this.setState({
                errorText: err
            });
        });
    }
}
