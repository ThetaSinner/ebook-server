import React from 'react';
import $ from 'jquery';

export default class LoadControl extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            libraryName: ''
        };

        this.libraryNameChange = this.libraryNameChange.bind(this);
        this.handleLoadLibrary = this.handleLoadLibrary.bind(this);
    }

    render() {
        return (
            <div className="d-inline mr-1">
                <button type="button" className="btn btn-primary" data-toggle="modal" data-target="#loadControlModal">Load</button>

                <div className="modal fade" id="loadControlModal" tabIndex="-1" role="dialog" aria-labelledby="loadControlModalLabel" aria-hidden="true">
                    <div className="modal-dialog" role="document">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title" id="loadControlModalLabel">Modal title</h5>
                                <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div className="modal-body">
                                <form>
                                    <label for="loadLibraryName">Library name</label>
                                    <input type="text" name="loadLibraryName" id="loadLibraryNameId" placeholder="Library name" value={this.state.libraryName} onChange={this.libraryNameChange} className="form-control" />
                                </form>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" data-dismiss="modal">Cancel</button>
                                <button type="button" className="btn btn-primary" onClick={this.handleLoadLibrary}>Ok</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    libraryNameChange(e) {
        this.setState({
            libraryName: e.target.value
        });
    }

    handleLoadLibrary() {
        this.props.loadLibrary(this.state.libraryName).then(function (result) {
            alert(result);
        }).catch(function (err) {
            alert(err);
        })

        $('#loadControlModal').modal('hide');
    }
}
