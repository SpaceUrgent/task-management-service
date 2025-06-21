import React from "react";

export default function AuthLayout({children}) {

    return(
        <main
            className="container-fluid vh-100"
        >
            <div className="row d-flex justify-content-center align-items-center h-100">
                <div className="col-md-9 col-lg-6 col-xl-5">
                    <img src="/login-page-image.webp" className="img-fluid" alt="Sample image"/>
                </div>
                <div className="col-md-8 col-lg-6 col-xl-4 offset-xl-1">
                    {children}
                </div>
            </div>
        </main>
)
}