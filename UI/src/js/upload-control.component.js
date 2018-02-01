import React from 'react';
import $ from 'jquery';

export default class UploadControl extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            files: []
        };

        this.handleSelectedFilesChanged = this.handleSelectedFilesChanged.bind(this);
        this.handleUploadFiles = this.handleUploadFiles.bind(this);
    }

    render() {
        return (
            <>
                <button type="button" className="btn btn-primary" data-toggle="modal" data-target="#uploadControlModal">Upload</button>

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
                                <form onSubmit={this.handleUploadFiles}>
                                    <label htmlFor="selectFiles">Choose books to upload</label>
                                    <input type="file" id="selectFiles" accept=".pdf" multiple className="form-control-file" onChange={this.handleSelectedFilesChanged} />
                                </form>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" data-dismiss="modal">Cancel</button>
                                <button type="button" className="btn btn-primary" onClick={this.handleUploadFiles}>Ok</button>
                            </div>
                        </div>
                    </div>
                </div>
            </>
        );
    }

    handleSelectedFilesChanged(e) {
        this.setState({
            files: e.target.files
        });
    }

    handleUploadFiles(e) {
        e.preventDefault();

        this.props.uploadFiles(this.state.files).then(() => {
            $('#uploadControlModal').modal('hide');
        }).catch((err) => {
            // TODO
            console.error(err);
        });
    }
}