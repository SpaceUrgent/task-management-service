import React, {useState} from "react";

export default function DateSelector({ onChange }) {

    return(
        <div className="input-group">
            <label className="input-group-text" htmlFor="dueDate">Due date</label>
            <input
                id="dueDate"
                type="date"
                className="form-control"
                onChange={(e) => onChange(e.target.value)}
            />
        </div>
    )
}