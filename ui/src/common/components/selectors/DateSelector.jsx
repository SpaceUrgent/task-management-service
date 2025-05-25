import React from "react";

export default function DateSelector({ initialValue, onChange }) {

    return(
        <div className="input-group input-group-sm">
            <label className="input-group-text" htmlFor="dueDate">Due date</label>
            <input
                id="dueDate"
                type="date"
                value={initialValue}
                className="form-control"
                onChange={(e) => onChange(e.target.value)}
            />
        </div>
    )
}