import React from 'react';

export default class LibraryInfo extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const libraryData = this.props.libraryData;

        /* eslint-disable quotes */
        return (
            <>
                <div>
                    <h3 className="d-inline mr-2">{libraryData.name}</h3>
                    
                    <span>with a total of {libraryData.numberOfBooks} books</span>
                </div>
            </>
        );
        /* eslint-enable quotes */
    }
}
