import React from "react";

const ValidatedFormInput = ({
    label,
    name,
    type = "text",
    registration,
    errors = {},
    touchedFields = {},
    ...rest
}) => {
    const getInputClass = () => {
        if (touchedFields[name]) {
            if (errors[name]) return "is-invalid";
            return "is-valid";
        }
        return "";
    };

    return (
        <div className="mb-2">
            {label && (
                <label htmlFor={name} className="form-label ms-2 small fw-bold">{label}</label>
            )}
            <input
                id={name}
                name={name}
                type={type}
                className={`form-control ${getInputClass()}`}
                {...registration}
                {...rest}
            />
            {errors[name] && touchedFields[name] && (
                <span className="text-danger small">{errors[name].message}</span>
            )}
        </div>
    );
};

export default ValidatedFormInput;