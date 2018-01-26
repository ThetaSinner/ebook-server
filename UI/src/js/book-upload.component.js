import React from 'react';
import ReactDOM from 'react-dom';
import $ from 'jquery';
import * as _ from 'lodash';

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

        const formData = new FormData();
        const files = this.state.files;
        for (var i = 0; i < files.length; i++) {
            formData.append('files', files.item(i));
            console.log(files.item(i).name);
        }

        console.log(formData);

        $.ajax({
            url: 'http://localhost:8080/upload',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(data) {
                console.log(data);
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.log(jqXHR, textStatus, errorThrown);
            }
        }).done(function (data) {
            console.log(data);
        });
    }
}

$(function() {
    ReactDOM.render(
        <BookUpload />,
        document.getElementById('upload')
    );
});
