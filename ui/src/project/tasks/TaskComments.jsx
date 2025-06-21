import React, { useState } from "react";
import {formatDateTime} from "../../common/Time";

export default function TaskComments({
    isExpanded = true, onToggleExpand, comments = [], onAddComment
}) {
    const [showForm, setShowForm] = useState(false);
    const [newComment, setNewComment] = useState("");
    const [submitError, setSubmitError] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!newComment.trim()) {
            setSubmitError("Comment cannot be empty");
            return;
        }
        setIsLoading(true);
        setSubmitError("");
        try {
            await onAddComment(newComment.trim());
            setNewComment("");
            setShowForm(false);
        } catch (error) {
            setSubmitError("Failed to add comment");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="accordion" id="taskCommentsAccordion">
            <div className="accordion-item">
                <h2 className="accordion-header" id="headingComments">
                    <button
                        className={`accordion-button ${isExpanded ? '' : 'collapsed'}`}
                        type="button"
                        onClick={onToggleExpand}
                        aria-expanded={isExpanded}
                        aria-controls="collapseComments"
                    >
                        Task Comments
                    </button>
                </h2>
                <div
                    id="collapseComments"
                    className={`accordion-collapse collapse ${isExpanded ? "show" : ""}`}
                    aria-labelledby="headingComments"
                >
                    <div className="accordion-body pt-2 pb-2">
                        <div className="row">
                            <div className="col p-0">
                                {!showForm && (
                                    <div className="d-flex justify-content-end">
                                        <button
                                            className="btn btn-sm btn-outline-primary"
                                            onClick={() => {
                                                setShowForm(true);
                                                setSubmitError("");
                                            }}
                                        >
                                            Add
                                        </button>
                                    </div>
                                )}

                                {showForm && (
                                    <form onSubmit={handleSubmit}>
                                        <div className="">
                                            <textarea
                                                className={`form-control${submitError ? ' is-invalid' : ''}`}
                                                rows={3}
                                                value={newComment}
                                                onChange={(e) => setNewComment(e.target.value)}
                                                placeholder="Write your comment..."
                                                disabled={isLoading}
                                            />
                                            {submitError && (
                                                <div className="invalid-feedback d-block">{submitError}</div>
                                            )}
                                        </div>
                                        <div className="d-flex justify-content-end gap-3">
                                            <button className="btn btn-sm btn-primary" type="submit" disabled={!newComment.trim() || isLoading}>
                                                {isLoading ? 'Submitting...' : 'Submit'}
                                            </button>
                                            <button className="btn btn-sm btn-outline-secondary" type="button" onClick={() => { setShowForm(false); setSubmitError(""); }} disabled={isLoading}>
                                                Cancel
                                            </button>
                                        </div>
                                        <hr />
                                    </form>
                                )}
                            </div>
                        </div>

                        <div className="row mt-2">
                            <div className="col p-0">
                                {comments.length === 0 ? (
                                    <p className="text-muted">No comments yet.</p>
                                ) : (
                                    <ul className="list-group">
                                        {comments.map((comment, index) => (
                                            <li key={index} className="list-group-item">
                                                <div className="text-muted small">
                                                    {formatDateTime(comment.createdAt)} by {comment.author.fullName}
                                                </div>
                                                <div className="text-break">{comment.content}</div>
                                            </li>
                                        ))}
                                    </ul>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
