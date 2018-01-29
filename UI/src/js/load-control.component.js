import React from 'react';
import $ from 'jquery';

export default class LoadControl extends React.Component {
    constructor(props) {
        super(props);

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
                                <input type="text" name="loadLibraryName" id="loadLibraryNameId" placeholder="Library name" />
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

    handleLoadLibrary() {
        var libraryName = $('#loadLibraryNameId').val();

        this.props.loadLibrary(libraryName).then(function (result) {
            alert(result);
        }).catch(function (err) {
            alert(err);
        })

        $('#loadControlModal').modal('hide');
    }
}
