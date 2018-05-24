import React from 'react';

export default class LibraryFilter extends React.Component {
    constructor(props) {
        super(props);

        this.toggleHelpText = this.toggleHelpText.bind(this);
        this.queryChanged = this.queryChanged.bind(this);

        this.state = {
            showHelpText: false
        };
    }

    render() {
        const toggleHelpText = this.toggleHelpText;

        /* eslint-disable quotes */
        return (
            <>
                <div className="form-group">
                    <div>
                        <label htmlFor="filter-query">Type your filter query</label>
                        <div className="d-inline float-right" onClick={toggleHelpText}>
                            <i className={"material-icons es-icon-button-large" + (this.state.showHelpText ? " es-icon-button-toggled" : "")} aria-hidden="true" data-toggle="tooltip" data-placement="bottom" title="Toggle filter">help</i>
                        </div>
                    </div>
                    <input type="text" className="form-control" id="filter-query" placeholder="Enter query" onChange={this.queryChanged} />
                </div>

                {this.state.showHelpText &&
                    <p>Type what you like and you'll be filtering by title.</p>
                }
            </>
        );
        /* eslint-enable quotes */
    }

    toggleHelpText() {
        this.setState(prevState => ({
            showHelpText: !prevState.showHelpText
        }));
    }

    queryChanged(event) {
        this.props.updateFilterQuery(event.target.value);
    }
}
