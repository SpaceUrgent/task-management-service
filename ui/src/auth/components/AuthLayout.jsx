import React from "react";

export default function AuthLayout({children}) {

    return(
        <main
            className="row m-0 align-content-center justify-content-center"
            style={{
                backgroundColor: "darkgray",
                height: '100vh',
                width: '100vw'
            }}
        >
                {children}
        </main>
    )
}