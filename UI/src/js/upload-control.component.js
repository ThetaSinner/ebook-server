import React from 'react';
import $ from 'jquery';

/* eslint-disable no-unused-vars */
import ErrorText from './error-text.component';
/* eslint-enable no-unused-vars */

export default class UploadControl extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            files: [],
            errorText: null
        };

        this.handleSelectedFilesChanged = this.handleSelectedFilesChanged.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
        this.handleUploadFiles = this.handleUploadFiles.bind(this);
    }

    render() {
        /* eslint-disable quotes */
        return (
            <>
                <div className="d-inline" data-toggle="modal" data-target="#uploadControlModal">
                    <i className="material-icons es-icon-button-large" aria-hidden="true" data-toggle="tooltip" data-placement="bottom" title="Upload books">file_upload</i>
                </div>

                <div className="modal fade" id="uploadControlModal" tabIndex="-1" role="dialog" aria-labelledby="uploadControlModalLabel" aria-hidden="true">
                    <div className="modal-dialog" role="document">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title" id="uploadControlModalLabel">Choose books to upload</h5>
                                <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div className="modal-body">
                                <div className="mb-1">
                                    <ErrorText errorText={this.state.errorText} />
                                </div>
                                <form onSubmit={this.handleUploadFiles}>
                                    <label htmlFor="selectFiles">Choose books to upload</label>
                                    <input type="file" id="selectFiles" accept=".pdf" multiple className="form-control-file" onChange={this.handleSelectedFilesChanged} />
                                </form>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" data-dismiss="modal" onClick={this.handleCancel}>Cancel</button>
                                <button type="button" className="btn btn-primary" onClick={this.handleUploadFiles}>Ok</button>
                            </div>
                        </div>
                    </div>
                </div>
            </>
        );
        /* eslint-enable quotes */
    }

    handleSelectedFilesChanged(e) {
        this.setState({
            files: e.target.files,
            errorText: null
        });
    }

    handleCancel() {
        this.setState({
            errorText: null
        });
    }

    handleUploadFiles(e) {
        e.preventDefault();

        this.props.uploadFiles(this.state.files).then(() => {
            $('#uploadControlModal').modal('hide');

            this.setState({
                files: [],
                errorText: null
            });
        }).catch((err) => {
            this.setState({
                errorText: err
            });
        });
    }
}