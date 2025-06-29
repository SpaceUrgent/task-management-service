import React from "react";
import MultiValueSelector from "./MultiValueSelector";

export default function LabeledMultiValueSelector({ label, values, selected, onChange }) {
    return (
        <div className="input-group input-group-sm">
            {label && <label className="input-group-text" htmlFor={label}>{label}</label>}
            <MultiValueSelector
                values={values}
                selected={selected}
                onChange={onChange}
            />
        </div>
    )
};