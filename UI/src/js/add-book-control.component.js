import React from 'react';
import $ from 'jquery';

/* eslint-disable no-unused-vars */
import ErrorText from './error-text.component';
/* eslint-enable no-unused-vars */

export default class AddBookControl extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            url: '',
            type: 'WebLink',
            errorText: null
        };

        this.handleUrlChanged = this.handleUrlChanged.bind(this);
        this.handleTypeChanged = this.handleTypeChanged.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
        this.handleAddBook = this.handleAddBook.bind(this);
    }

    render() {
        /* eslint-disable quotes */
        return (
            <>
                <div className="d-inline" data-toggle="modal" data-target="#addBookControlModal">
                    <i className="material-icons es-icon-button-large" aria-hidden="true" data-toggle="tooltip" data-placement="bottom" title="Add book">add</i>
                </div>

                <div className="modal fade" id="addBookControlModal" tabIndex="-1" role="dialog" aria-labelledby="addBookControlModalLabel" aria-hidden="true">
                    <div className="modal-dialog" role="document">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title" id="addBookControlModalLabel">Add a book</h5>
                                <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div className="modal-body">
                                <div className="mb-1">
                                    <ErrorText errorText={this.state.errorText} />
                                </div>
                                <form onSubmit={this.handleAddBook}>
                                    <label htmlFor="inputUrl">Input the URL for the book</label>
                                    <input type="text" id="inputUrl" multiple className="form-control" value={this.state.url} onChange={this.handleUrlChanged} />

                                    <label htmlFor="selectType">What type of URL is this?</label>
                                    <select id="selectType" className="custom-select" value={this.state.type} onChange={this.handleTypeChanged}>
                                        <option value="LocalUnmanaged">A file on your computer</option>
                                        <option value="WebLink">A link to a book somewhere on the internet</option>
                                        <option value="Other">Something else</option>
                                    </select>
                                </form>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" data-dismiss="modal" onClick={this.handleCancel}>Cancel</button>
                                <button type="button" className="btn btn-primary" onClick={this.handleAddBook}>Ok</button>
                            </div>
                        </div>
                    </div>
                </div>
            </>
        );
        /* eslint-enable quotes */
    }

    handleUrlChanged(e) {
        this.setState({
            url: e.target.value,
            errorText: null
        });
    }

    handleTypeChanged(e) {
        this.setState({
            type: e.target.value,
            errorText: null
        });
    }

    handleCancel() {
        this.setState({
            errorText: null
        });
    }

    handleAddBook() {
        this.props.addBook(this.state.url, this.state.type).then(() => {
            $('#addBookControlModal').modal('hide');

            this.setState({
                url: '',
                type: 'WebLink',
                errorText: null
            });
        }).catch((err) => {
            this.setState({
                errorText: err
            });
        });
    }
}