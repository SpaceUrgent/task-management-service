import React from 'react';
import ValidatedInput from './ValidatedInput';

const FormInput = ({
    id,
    name,
    type = 'text',
    placeholder,
    value,
    onChange,
    onBlur,
    isValid,
    showError,
    errorMessage,
    required = false,
    className = ''
}) => {
    return (
        <div className={className}>
            <ValidatedInput
                id={id}
                name={name}
                type={type}
                placeholder={placeholder}
                value={value}
                onChange={onChange}
                onBlur={onBlur}
                isValid={isValid}
                showError={showError}
                errorMessage={errorMessage}
                required={required}
            />
        </div>
    );
};

export default FormInput;