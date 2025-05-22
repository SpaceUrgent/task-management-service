import React, { useState } from "react";

export default function TaskComments({ comments = [], onAddComment }) {
    const [showForm, setShowForm] = useState(false);
    const [newComment, setNewComment] = useState("");

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!newComment.trim()) return;
        onAddComment(newComment.trim());
        setNewComment("");
        setShowForm(false);
    };

    return (
        <div className="accordion mt-4" id="taskCommentsAccordion">
            <div className="accordion-item">
                <h2 className="accordion-header" id="headingComments">
                    <button
                        className="accordion-button collapsed"
                        type="button"
                        data-bs-toggle="collapse"
                        data-bs-target="#collapseComments"
                        aria-expanded="false"
                        aria-controls="collapseComments"
                    >
                        Task Comments
                    </button>
                </h2>
                <div
                    id="collapseComments"
                    className="accordion-collapse collapse"
                    aria-labelledby="headingComments"
                    data-bs-parent="#taskCommentsAccordion"
                >
                    <div className="accordion-body">
                        {!showForm && (
                            <div className="d-flex justify-content-end mb-3">
                                <button
                                    className="btn btn-outline-primary"
                                    onClick={() => setShowForm(!showForm)}
                                >
                                    Add
                                </button>
                            </div>
                        )}

                        {showForm && (
                            <form onSubmit={handleSubmit}>
                                <div className="mb-3">
                                    <textarea
                                        className="form-control"
                                        rows={3}
                                        value={newComment}
                                        onChange={(e) => setNewComment(e.target.value)}
                                        placeholder="Write your comment..."
                                    />
                                </div>
                                <div className="d-flex justify-content-end gap-3">
                                    <button className="btn btn-primary" type="submit">
                                        Submit
                                    </button>
                                    <button className="btn btn-outline-secondary" onClick={() => setShowForm(!showForm)}>
                                        Cancel
                                    </button>
                                </div>
                                <hr />
                            </form>
                        )}

                        {comments.length === 0 ? (
                            <p className="text-muted">No comments yet.</p>
                        ) : (
                            <ul className="list-group">
                                {comments.map((comment, index) => (
                                    <li key={index} className="list-group-item">
                                        <div className="text-muted small">
                                            {comment.createdAt} by {comment.author.fullName}
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
    );
}
