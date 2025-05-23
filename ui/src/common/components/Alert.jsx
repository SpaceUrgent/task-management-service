import React from "react";

export default function Alert({ error }) {
    return (
        <div className="alert alert-danger" role="alert">
            {error}
        </div>
    )
}