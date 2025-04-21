import React from "react";

export default function Selector({ value, options, onChange }) {

    return (
        <select
            className="form-select"
            value={value}
            onChange={(e) => onChange(e.target.value || null)}
        >
            {options.map((option) => (
                <option key={option.value} value={option.value}>
                    {option.label || option.value}
                </option>
            ))}
        </select>
    )
}