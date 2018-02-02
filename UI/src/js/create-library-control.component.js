import React from 'react';
import $ from 'jquery';

export default class CreateLibraryControl extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            libraryName: ''
        };

        this.libraryNameChange = this.libraryNameChange.bind(this);
        this.handleCreateLibrary = this.handleCreateLibrary.bind(this);
    }

    render() {
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
                                <form onSubmit={this.handleCreateLibrary}>
                                    <label htmlFor="createLibraryName">Library name</label>
                                    <input type="text" name="createLibraryName" id="createLibraryNameId" placeholder="Library name" value={this.state.libraryName} onChange={this.libraryNameChange} className="form-control" />
                                </form>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" data-dismiss="modal">Cancel</button>
                                <button type="button" className="btn btn-primary" onClick={this.handleCreateLibrary}>Ok</button>
                            </div>
                        </div>
                    </div>
                </div>
            </>
        );
    }

    libraryNameChange(e) {
        this.setState({
            libraryName: e.target.value
        });
    }

    handleCreateLibrary(e) {
        e.preventDefault();

        this.props.createLibrary(this.state.libraryName).then(() => {
            $('#createLibraryControlModal').modal('hide');
        }).catch(function (err) {
            // TODO output the error into the modal.
            alert(err);
        });
    }
}
