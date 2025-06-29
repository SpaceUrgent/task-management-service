import React from "react";

export default function PaginationPanel({ currentPage, totalPages, onPrevious, onNext }) {
    return (
        <div className="d-flex justify-content-between align-items-center mt-auto pt-3 border-top">
            <button
                className="btn btn-sm btn-outline-secondary"
                disabled={currentPage <= 1}
                onClick={onPrevious}
            >
                Previous
            </button>
            <span>
                Page {currentPage} of {totalPages || 1}
            </span>
            <button
                className="btn btn-sm btn-outline-secondary"
                disabled={totalPages ? currentPage >= totalPages : true}
                onClick={onNext}
            >
                Next
            </button>
        </div>
    );
}
