import React from 'react';
import ReactDOM from 'react-dom';
import $ from 'jquery';

export default class BookUpload extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            files: []
        };

        this.handleSelectedFilesChanged = this.handleSelectedFilesChanged.bind(this);
        this.handleUpload = this.handleUpload.bind(this);
    }

    render() {
        return (
            <form>
                <label htmlFor="selectFiles">Choose books to upload</label>
                <input type="file" id="selectFiles" accept=".pdf" multiple className="form-control-file" onChange={this.handleSelectedFilesChanged} />
                <input type="submit" value="Upload" className="btn btn-primary" onClick={this.handleUpload} />
            </form>
        );
    }

    handleSelectedFilesChanged(e) {
        this.setState({
            files: e.target.files
        });
    }

    handleUpload(e) {
        e.preventDefault();

        console.log("Will send to server", this.state.files);
    }
}

$(function() {
    ReactDOM.render(
        <BookUpload />,
        document.getElementById('upload')
    );
});
