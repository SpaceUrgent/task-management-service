import React from "react";

export default function LabeledValue({ label, value }) {

    return (
        <div className="input-group input-group-sm">
            <label className="input-group-text" htmlFor="owner">{label}</label>
            <input className="form-control" value={value} disabled={true}/>
        </div>
    )
}