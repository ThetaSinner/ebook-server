import React from 'react';
import $ from 'jquery';

/* eslint-disable no-unused-vars */
import ErrorText from './error-text.component';
/* eslint-enable no-unused-vars */

export default class CreateLibraryControl extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            libraryName: '',
            errorText: null
        };

        this.libraryNameChange = this.libraryNameChange.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
        this.handleCreateLibrary = this.handleCreateLibrary.bind(this);
    }

    render() {
        /* eslint-disable quotes */
        return (
            <>
                <button type="button" className="btn btn-primary" data-toggle="modal" data-target="#createLibraryControlModal">Create</button>

                <div className="modal fade" id="createLibraryControlModal" tabIndex="-1" role="dialog" aria-labelledby="createLibraryControlModalLabel" aria-hidden="true">
                    <div className="modal-dialog" role="document">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title" id="createLibraryControlModalLabel">Create a library</h5>
                                <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div className="modal-body">
                                <div className="mb-1">
                                    <ErrorText errorText={this.state.errorText} />
                                </div>
                                <form onSubmit={this.handleCreateLibrary}>
                                    <label htmlFor="createLibraryName">Library name</label>
                                    <input type="text" name="createLibraryName" id="createLibraryNameId" placeholder="Library name" value={this.state.libraryName} onChange={this.libraryNameChange} className="form-control" />
                                </form>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" data-dismiss="modal" onClick={this.handleCancel}>Cancel</button>
                                <button type="button" className="btn btn-primary" onClick={this.handleCreateLibrary}>Ok</button>
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

    handleCreateLibrary() {
        this.props.createLibrary(this.state.libraryName).then(() => {
            $('#createLibraryControlModal').modal('hide');

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
