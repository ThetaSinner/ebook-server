import React from 'react';

export default class ErrorText extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const errorText = this.props.errorText;
        if (!errorText) {
            return null;
        }

        if (typeof errorText !== 'string') {
            /* eslint-disable no-console */
            console.error('Invalid error text', errorText);
            /* eslint-enable no-console */
            return null;
        }

        /* eslint-disable quotes */
        return (
            <>
                {errorText &&
                    <div className="alert alert-danger">
                        <span><strong>Error</strong> {errorText}</span>
                    </div>
                }
            </>
        );
        /* eslint-enable quotes */
    }
}
