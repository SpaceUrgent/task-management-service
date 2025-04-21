import React from "react";

export default function LabeledSelector({ label, value, onChange, options }) {
    return(
        <div className="input-group">
            <label className="input-group-text" htmlFor={label}>{label}</label>
            <select className="form-select"
                    id={label}
                    onChange={(e) => onChange(e.target.value)}
                    value={value}
            >
                {options.map((option) => (
                    <option value={option.value} key={option.value}>{option.label}</option>
                ))}
            </select>
        </div>
    )
}