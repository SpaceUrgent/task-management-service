import React from "react";

export default function ValidatedInput ({
                   id,
                   name,
                   type = "text",
                   placeholder = "",
                   onChange,
                   isValid,
                   onBlur,
                   showError,
                   errorMessage,
                   required = false
}) {
    const handleChange = (e) => {
        onChange(e.target.value);
    }

    return (
        <div className="mb-1 text-start">
            <label className="label form-label mb-0" htmlFor={id}>{name}</label>
            <input
                className={`form-control ${isValid ? "is-valid" : showError && "is-invalid"}`}
                id={id}
                type={type}
                name={name}
                placeholder={placeholder}
                onChange={handleChange}
                onBlur={onBlur}
                required={required}
            />
            {!isValid && showError &&
                <span className="text-danger span-warning">{errorMessage}</span>
            }
        </div>
    );
};