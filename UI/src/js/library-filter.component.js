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
                    <div>
                        <p>You can type in text to filter your books by title.</p>
                        <p>If you'd like something more specific there's a little query language:</p>
                        <ul>
                            <li>Tag search terms with a column, e.g. dickens:authors</li>
                            <li>You can combine terms with and '&amp;' and '|', e.g. expectations:title &amp; dickens:author</li>
                            <li>Invert a search item, e.g. !bleak:title</li>
                            <li>Group terms together, e.g. bleak:title | !(expectations:title &amp; dickens:author)</li>
                        </ul>
                        <p>The order of evaluation is what you'd expect if you've used a modern programming language. That is, (), !, &amp;, |</p>
                        <p>Search items are NOT case sensitive, but column tags are case sensitive. i.e. classics:TAGS won't work.</p>
                    </div>
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
