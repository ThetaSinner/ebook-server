import React from 'react';
import $ from 'jquery';

export default class AddBookControl extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            url: '',
            type: 'WebLink'
        };

        this.handleUrlChanged = this.handleUrlChanged.bind(this);
        this.handleTypeChanged = this.handleTypeChanged.bind(this);
        this.handleAddBook = this.handleAddBook.bind(this);
    }

    render() {
        return (
            <>
                <button type="button" className="btn btn-primary" data-toggle="modal" data-target="#addBookControlModal">Add</button>

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
                                <button type="button" className="btn btn-secondary" data-dismiss="modal">Cancel</button>
                                <button type="button" className="btn btn-primary" onClick={this.handleAddBook}>Ok</button>
                            </div>
                        </div>
                    </div>
                </div>
            </>
        );
    }

    handleUrlChanged(e) {
        this.setState({
            url: e.target.value
        });
    }

    handleTypeChanged(e) {
        this.setState({
            type: e.target.value
        });
    }

    handleAddBook(e) {
        e.preventDefault();

        this.props.addBook(this.state.url, this.state.type).then(() => {
            $('#addBookControlModal').modal('hide');
        }).catch((err) => {
            // TODO
            console.error(err);
        });
    }
}