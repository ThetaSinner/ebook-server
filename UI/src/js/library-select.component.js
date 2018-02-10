import React from 'react';

export default class LibrarySelect extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        /* eslint-disable quotes */
        return (
            <>
                {this.props.libraries.map((library) =>
                    <div key={library}>
                        <span>{library}</span>
                    </div>
                )}
            </>
        );
        /* eslint-enable quotes */
    }
}
