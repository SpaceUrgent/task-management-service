import React from "react";

export default function AuthForm({   children,
                                     title,
                                     onSubmit
}) {
    return(
        <div className="card p-2 p-3 text-center shadow p-3 mb-5 bg-body-tertiary rounded" style={{ width: '450px' }}>
            <h4 className="p-1">{title}</h4>
            <form onSubmit={onSubmit}>
                {children}
            </form>
        </div>
    )
}