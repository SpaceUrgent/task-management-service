import React from "react";

export default function TextArea({
                                     id,
                                     name,
                                     placeholder,
                                     onChange,
                                     rows = 1,
                                     required = false
}) {
    const handleChange = (e) => {
        e.preventDefault();
        onChange(e.target.value);
    }

    return(
        <div className="mb-1 text-start">
            <label className="label form-label mb-0" htmlFor={id}>{name}</label>
            <textarea
                className={`form-control`}
                id={id}
                name={name}
                placeholder={placeholder}
                onChange={handleChange}
                rows={rows}
                required={required}
            />
        </div>
    )
}