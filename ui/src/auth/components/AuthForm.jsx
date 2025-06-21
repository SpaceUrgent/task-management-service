import React from "react";

export default function AuthForm({ 
    children, 
    title, 
    onSubmit, 
    submitError = "",
    submitButtonText = "Submit",
    showSubmitButton = true,
    footerContent,
    submitDisabled = false
}) {

    return(
        <form onSubmit={onSubmit}>
            <center><h5>{title}</h5></center>
            <hr/>
            <div className="flex-column d-flex justify-content-center">
                {children}
                <p/>
                {showSubmitButton && (
                    <div className="text-center">
                        <button 
                            type="submit" 
                            className="btn btn-primary mt-2 w-50"
                            disabled={submitDisabled}
                        >
                            {submitButtonText}
                        </button>
                        
                        {submitError && (
                            <div className="mt-2">
                                <span className="text-danger span-warning small">{submitError}</span>
                            </div>
                        )}
                        
                        {footerContent && (
                            <div className="mt-2">
                                {footerContent}
                            </div>
                        )}
                    </div>
                )}
            </div>
        </form>
    )
}