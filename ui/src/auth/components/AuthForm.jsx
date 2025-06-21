import React from "react";

export default function AuthForm({ children, title, onSubmit}) {

    return(
        <form onSubmit={onSubmit}>
            <center><h5>{title}</h5></center>
            <hr/>
            <div className="flex-column d-flex justify-content-center">
                {children}
            </div>
        </form>
    )
}